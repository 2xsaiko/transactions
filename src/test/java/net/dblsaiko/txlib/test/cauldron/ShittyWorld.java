package net.dblsaiko.txlib.test.cauldron;

public final class ShittyWorld {

    private final CauldronState[] blocks = new CauldronState[1024];

    public void setBlockState(int pos, CauldronState state) {
        this.blocks[pos] = state;
    }

    public CauldronState getBlockState(int pos) {
        return this.blocks[pos];
    }

}
