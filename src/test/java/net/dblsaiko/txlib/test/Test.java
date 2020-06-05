package net.dblsaiko.txlib.test;

import net.dblsaiko.txlib.Transaction;
import net.dblsaiko.txlib.test.cauldron.CauldronOps;
import net.dblsaiko.txlib.test.cauldron.CauldronState;
import net.dblsaiko.txlib.test.cauldron.ShittyWorld;

public class Test {

    @org.junit.Test
    public void test() {
        ShittyWorld world = new ShittyWorld();
        world.setBlockState(2, CauldronState.EMPTY.with("water", 100));

        ItemInv inv = new ItemInv();
        FluidInv finv = new FluidInv();
        CauldronOps cauldron = CauldronOps.create(world, 2);

        inv.setItem(2, "stick");
        finv.insert("water", 100);
        Transaction ta = Transaction.create();
        while (true) {
            Transaction ta1 = ta.createTransaction();
            if (finv.extract(ta1, "water", 3) == 3) {
                inv.setItem(ta1, 2, inv.takeItem(ta1, 2) + 1);
                cauldron.extract(ta1, "water", 1);
                ta1.commit();
            } else {
                break;
            }
        }
        ta.commit();
    }

}
