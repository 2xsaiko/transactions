package net.dblsaiko.txlib.test;

import java.util.Objects;

import net.dblsaiko.txlib.SimpleChangelistDataOps;
import net.dblsaiko.txlib.Transaction;

public class ItemInv {

    public String[] items = new String[9];

    public void setItem(int slot, String value) {
        this.items[slot] = value;
    }

    public void setItem(Transaction ta, int slot, String value) {
        DataOps ops = DataOps.getFor(this);
        ops.insert(ta, Action.set(slot, value));
    }

    public String takeItem(int slot) {
        String item = this.items[slot];
        this.items[slot] = null;
        return item;
    }

    public String takeItem(Transaction ta, int slot) {
        DataOps ops = DataOps.getFor(this);
        String s = ops.getCurrentState(ta).takeItem(slot);
        ops.insert(ta, Action.take(slot));
        return s;
    }

    public ItemInv copy() {
        ItemInv copy = new ItemInv();
        System.arraycopy(this.items, 0, copy.items, 0, 9);
        return copy;
    }

    private static final class DataOps implements SimpleChangelistDataOps<Action, ItemInv> {

        private final ItemInv inv;

        private DataOps(ItemInv inv) {
            this.inv = inv;
        }

        public static DataOps getFor(ItemInv inv) {
            return new DataOps(inv);
        }

        @Override
        public ItemInv copyState() {
            return this.inv.copy();
        }

        @Override
        public ItemInv getPersistentState() {
            return this.inv;
        }

        @Override
        public void apply(ItemInv receiver, Action change) {
            change.apply(receiver);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            DataOps that = (DataOps) o;
            return Objects.equals(this.inv, that.inv);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.inv);
        }

    }

    private static class Action {
        private final boolean take;
        private final int slot;
        private final String value;

        private Action(boolean take, int slot, String value) {
            this.take = take;
            this.slot = slot;
            this.value = value;
        }

        public void apply(ItemInv inv) {
            if (this.take) {
                inv.takeItem(this.slot);
            } else {
                inv.setItem(this.slot, this.value);
            }
        }

        public static Action take(int slot) {
            return new Action(true, slot, null);
        }

        public static Action set(int slot, String value) {
            return new Action(false, slot, value);
        }
    }

}
