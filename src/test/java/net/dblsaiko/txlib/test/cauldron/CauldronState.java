package net.dblsaiko.txlib.test.cauldron;

import java.util.Objects;

public final class CauldronState {

    public static final CauldronState EMPTY = new CauldronState(null, 0);

    public final String fluid;
    public final int level;

    private CauldronState(String fluid, int level) {
        this.fluid = fluid;
        this.level = level;
    }

    public CauldronState with(String fluid, int level) {
        if (level < 0 || fluid == null) return EMPTY;
        return new CauldronState(fluid, level);
    }

    public CauldronState withFluid(String fluid) {
        return this.with(fluid, this.level);
    }

    public CauldronState withLevel(int level) {
        return this.with(this.fluid, level);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CauldronState that = (CauldronState) o;
        return this.level == that.level &&
            Objects.equals(this.fluid, that.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fluid, this.level);
    }

    @Override
    public String toString() {
        return String.format("CauldronState { fluid: '%s', level: %d }", this.fluid, this.level);
    }

}
