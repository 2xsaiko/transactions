package net.dblsaiko.txlib.test.cauldron;

import java.util.Objects;

import net.dblsaiko.txlib.FullCopyDataOps;
import net.dblsaiko.txlib.Transaction;

public class CauldronOps implements FullCopyDataOps<CauldronState> {

    private final ShittyWorld world;
    private final int pos;

    private CauldronOps(ShittyWorld world, int pos) {
        this.world = world;
        this.pos = pos;
    }

    public static CauldronOps create(ShittyWorld world, int pos) {
        return new CauldronOps(world, pos);
    }

    public int insert(Transaction ta, String fluid, int amount) {
        CauldronState currentState = this.getCurrentState(ta);
        if (currentState.isEmpty()) {
            currentState = currentState.with(fluid, amount);
        } else if (currentState.fluid.equals(fluid)) {
            currentState = currentState.withLevel(currentState.level + amount);
        } else {
            return amount;
        }
        ta.put(this, currentState);
        return 0;
    }

    public int extract(Transaction ta, String fluid, int maxAmount) {
        CauldronState currentState = this.getCurrentState(ta);
        if (currentState.fluid.equals(fluid)) {
            int toExtract = Math.min(maxAmount, currentState.level);
            currentState = currentState.withLevel(currentState.level - toExtract);
            ta.put(this, currentState);
            return toExtract;
        } else {
            return 0;
        }
    }

    @Override
    public CauldronState getPersistentState() {
        return this.world.getBlockState(this.pos);
    }

    @Override
    public CauldronState copy(CauldronState value) {
        return value;
    }

    @Override
    public void applyChanges(CauldronState changes) {
        this.world.setBlockState(this.pos, changes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CauldronOps that = (CauldronOps) o;
        return this.pos == that.pos &&
            Objects.equals(this.world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.pos);
    }

}
