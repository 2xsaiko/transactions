package net.dblsaiko.txlib;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TransactionDataOps} that implements a simple change list. Each
 * transaction holds parts of that change list, the items of the list describe
 * how to modify the backing object, e.g: "insert 10 buckets of water"
 *
 * @param <T> the change list item type
 */
public interface ChangelistDataOps<T> extends TransactionDataOps<List<T>> {

    /**
     * Insert a change list item at the end of the list.
     *
     * @param ta     the transaction to insert into
     * @param change the change list item to insert
     */
    default void insert(Transaction ta, T change) {
        List<T> changes = ta.get(this);
        if (changes == null) {
            changes = new ArrayList<>();
            ta.put(this, changes);
        }
        changes.add(change);
    }

    @Override
    default void applyChangesToTransaction(Transaction ta, List<T> changes) {
        ta.computeIfAbsent(this, _k -> new ArrayList<>()).addAll(changes);
    }

}
