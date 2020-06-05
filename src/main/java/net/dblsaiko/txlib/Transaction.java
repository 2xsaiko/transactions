package net.dblsaiko.txlib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Transaction {

    private final Transaction parent;
    private Transaction child = null;

    private final Map<TransactionDataOps<Object>, Object> data = new HashMap<>();

    private boolean valid = true;

    Transaction(Transaction parent) {
        this.parent = parent;
    }

    /**
     * Gets a value from the internal data map.
     *
     * <p>Calling this will invalidate any child transactions.
     *
     * @see Map#get(Object)
     */
    @SuppressWarnings("unchecked")
    public <D> D get(TransactionDataOps<D> container) {
        this.checkValid();
        return (D) this.data.get(container);
    }

    /**
     * Puts a value into the internal data map.
     *
     * <p>Calling this will invalidate any child transactions.
     *
     * @see Map#put(Object, Object)
     */
    @SuppressWarnings("unchecked")
    public <D> D put(TransactionDataOps<D> container, D value) {
        this.checkValid();
        return (D) this.data.put((TransactionDataOps<Object>) container, value);
    }

    /**
     * <br>Calling this will invalidate any child transactions.
     *
     * @see Map#computeIfAbsent(Object, Function)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <D> D computeIfAbsent(TransactionDataOps<D> container, Function<TransactionDataOps<D>, D> op) {
        this.checkValid();
        return (D) this.data.computeIfAbsent((TransactionDataOps<Object>) container, (Function) op);
    }

    /**
     * Initializes the data map value for the {@code container} by going through
     * the transaction tree, picking the closest value and applying {@code op}
     * to it.
     *
     * <p>Calling this will invalidate any child transactions.
     *
     * @param container the container to init the value for
     * @param op        the map to apply before putting the value into the data
     *                  map
     * @param <D>       the data type
     */
    public <D> void initWithParent(TransactionDataOps<D> container, Function<D, D> op) {
        this.checkValid();
        if (!this.data.containsKey(container)) {
            Transaction current = this;
            while (current != null) {
                D data = current.get(container);
                if (data != null) {
                    this.put(container, op.apply(data));
                    return;
                }
                current = current.parent;
            }
        }
    }

    /**
     * Collect a list of all the data. The first element in the list will be
     * closest to the root of the transaction tree.
     *
     * <p>Calling this will invalidate any child transactions.
     *
     * @param container the container to get the data list for
     * @param <D>       the data type
     * @return the data list
     */
    public <D> List<D> collectData(TransactionDataOps<D> container) {
        this.checkValid();
        LinkedList<D> dataList = new LinkedList<>();
        Transaction current = this;
        while (current != null) {
            D data = current.get(container);
            if (data != null) {
                dataList.addFirst(data);
            }
            current = current.parent;
        }
        return dataList;
    }

    /**
     * Commit this transaction.
     * If this transaction has a parent, applies this transaction's changes to
     * the parent. Otherwise, applies this transaction's changes to the actual
     * target objects.
     *
     * <p>Calling this will invalidate any child transactions.
     */
    public void commit() {
        this.checkValid();
        if (this.parent != null) {
            this.data.forEach((k, v) -> k.applyChangesToTransaction(this.parent, v));
        } else {
            this.data.forEach(TransactionDataOps::applyChanges);
        }
        this.invalidate();
    }

    /**
     * Invalidate this transaction. After this is called, any operations on this
     * transaction will throw an {@link IllegalStateException}.
     *
     * <p>In most cases, this does not have to be called manually.
     */
    public void invalidate() {
        if (this.child != null) {
            this.child.invalidate();
            this.child = null;
        }
        if (this.parent != null) {
            this.parent.child = null;
        }
        this.valid = false;
    }

    public boolean isValid() {
        return this.valid;
    }

    private void checkValid() {
        if (!this.valid) throw new IllegalStateException("Transaction is not valid!");
    }

    /**
     * Create a new sub-transaction.
     *
     * <p>Calling this will invalidate any existing subtransactions of this
     * transaction.
     *
     * @return the new transaction
     */
    public Transaction createTransaction() {
        this.checkValid();
        if (this.child != null) {
            this.child.valid = false;
        }
        Transaction transaction = new Transaction(this);
        this.child = transaction;
        return transaction;
    }

    /**
     * Create a new transaction.
     *
     * <p>Calling this will invalidate any existing transactions.
     *
     * @return the new transaction
     */
    public static Transaction create() {
        return TransactionTracker.INSTANCE.create();
    }

}
