package net.dblsaiko.txlib.test;

import net.dblsaiko.txlib.FullCopyDataOps;
import net.dblsaiko.txlib.Transaction;

public class FluidInv {

    private String type;
    private int amount;

    private final DataOps ops = new DataOps(this);

    public int insert(String fluid, int amount) {
        if (this.type == null || this.amount <= 0) {
            this.type = fluid;
            this.amount = amount;
            return 0;
        } else {
            return amount;
        }
    }

    public int insert(Transaction ta, String fluid, int amount) {
        return ta.get(this.ops).insert(fluid, amount);
    }

    public int extract(String fluid, int max) {
        if (fluid.equals(this.type)) {
            int extracted = Math.max(0, Math.min(max, this.amount));
            this.amount -= extracted;
            return extracted;
        } else {
            return 0;
        }
    }

    public int extract(Transaction ta, String fluid, int max) {
        return this.ops.getCurrentState(ta).extract(fluid, max);
    }

    public String getType() {
        return this.type;
    }

    public String getType(Transaction ta) {
        return this.ops.getCurrentState(ta).getType();
    }

    public int getAmount() {
        return this.amount;
    }

    public int getAmount(Transaction ta) {
        return this.ops.getCurrentState(ta).getAmount();
    }

    public FluidInv copy() {
        FluidInv copy = new FluidInv();
        copy.copyFrom(this);
        return copy;
    }

    public void copyFrom(FluidInv other) {
        this.type = other.type;
        this.amount = other.amount;
    }

    private static final class DataOps implements FullCopyDataOps<FluidInv> {

        private final FluidInv inv;

        private DataOps(FluidInv inv) {
            this.inv = inv;
        }

        @Override
        public void applyChanges(FluidInv changes) {
            this.inv.copyFrom(changes);
        }

        @Override
        public FluidInv copy(FluidInv value) {
            return value.copy();
        }

        @Override
        public FluidInv getPersistentState() {
            return this.inv;
        }

    }

}
