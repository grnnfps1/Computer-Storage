package com.computerstorage.common.storage;

import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Putting items into the index from the terminal. The rules that matter are that nothing is ever
 * destroyed, that the SSD capacity is a hard ceiling, and that a stopped machine takes nothing.
 */
@BootstrapMinecraft
class DepositServiceTest {

    private static final boolean RUNNING = true;
    private static final boolean OFFLINE = false;

    @Test
    void aWholeStackGoesIntoAnEmptyIndex() {
        VirtualStorage index = new VirtualStorage(1_024);

        ItemStack leftover = DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 64), RUNNING);

        assertTrue(leftover.isEmpty(), "everything fitted, so nothing comes back");
        assertEquals(64, index.count(new ItemStack(Items.IRON_INGOT)));
        assertEquals(64L, index.used());
    }

    @Test
    void depositsAccumulateOnTheSameEntry() {
        VirtualStorage index = new VirtualStorage(1_024);

        DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 40), RUNNING);
        DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 33), RUNNING);

        assertEquals(73, index.count(new ItemStack(Items.IRON_INGOT)));
    }

    // ---- capacity ------------------------------------------------------------------------

    @Test
    void afullIndexTakesNothingAndHandsTheStackBack() {
        VirtualStorage index = new VirtualStorage(10);
        DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 10), RUNNING);
        assertEquals(0L, index.available(), "the index must actually be full for this to mean anything");

        ItemStack offered = new ItemStack(Items.REDSTONE, 32);
        ItemStack leftover = DepositService.deposit(index, offered, RUNNING);

        assertEquals(32, leftover.getCount(), "a full index must give the whole stack back");
        assertEquals(0, index.count(new ItemStack(Items.REDSTONE)));
        assertEquals(10L, index.used(), "and must not have grown past its capacity");
    }

    @Test
    void onlyWhatFitsGoesInAndTheRestComesBack() {
        VirtualStorage index = new VirtualStorage(50);
        DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 30), RUNNING);

        ItemStack leftover = DepositService.deposit(index, new ItemStack(Items.REDSTONE, 64), RUNNING);

        assertEquals(20, index.count(new ItemStack(Items.REDSTONE)), "only the free 20 could be taken");
        assertEquals(44, leftover.getCount(), "the other 44 must be handed back, not swallowed");
        assertEquals(50L, index.used());
        assertEquals(0L, index.available());
    }

    @Test
    void anIndexWithNoCapacityAtAllTakesNothing() {
        VirtualStorage index = new VirtualStorage(0);

        ItemStack leftover = DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 5), RUNNING);

        assertEquals(5, leftover.getCount(), "no SSDs means no room, and the items stay with the player");
        assertTrue(index.isEmpty());
    }

    // ---- machine state -------------------------------------------------------------------

    @Test
    void anOfflineComputerAcceptsNothing() {
        VirtualStorage index = new VirtualStorage(1_024);

        ItemStack leftover = DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 64), OFFLINE);

        assertEquals(64, leftover.getCount(), "a stopped machine must give the whole stack back");
        assertTrue(index.isEmpty(), "and must not have stored anything");
        assertEquals(0L, index.used());
    }

    @Test
    void anOfflineComputerAcceptsNothingOnTheSingleItemPathEither() {
        VirtualStorage index = new VirtualStorage(1_024);

        ItemStack leftover = DepositService.depositAmount(index, new ItemStack(Items.IRON_INGOT, 64), 1, OFFLINE);

        assertEquals(64, leftover.getCount());
        assertTrue(index.isEmpty());
    }

    // ---- single item path ----------------------------------------------------------------

    @Test
    void aRightClickOffersOneItemAndKeepsTheRest() {
        VirtualStorage index = new VirtualStorage(1_024);

        ItemStack leftover = DepositService.depositAmount(index, new ItemStack(Items.IRON_INGOT, 64), 1, RUNNING);

        assertEquals(1, index.count(new ItemStack(Items.IRON_INGOT)));
        assertEquals(63, leftover.getCount(), "the rest stays on the cursor");
    }

    @Test
    void aSingleItemIntoAFullIndexChangesNothing() {
        VirtualStorage index = new VirtualStorage(1);
        DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, 1), RUNNING);

        ItemStack offered = new ItemStack(Items.REDSTONE, 8);
        ItemStack leftover = DepositService.depositAmount(index, offered, 1, RUNNING);

        assertEquals(8, leftover.getCount(), "nothing moved, so the cursor keeps all 8");
        assertEquals(0, index.count(new ItemStack(Items.REDSTONE)));
    }

    // ---- nothing is destroyed ------------------------------------------------------------

    @Test
    void anEmptyStackIsANoOp() {
        VirtualStorage index = new VirtualStorage(1_024);

        assertTrue(DepositService.deposit(index, ItemStack.EMPTY, RUNNING).isEmpty());
        assertTrue(index.isEmpty());
    }

    @Test
    void everythingOfferedIsEitherStoredOrReturned() {
        VirtualStorage index = new VirtualStorage(37);

        int offered = 64;
        ItemStack leftover = DepositService.deposit(index, new ItemStack(Items.IRON_INGOT, offered), RUNNING);
        int stored = index.count(new ItemStack(Items.IRON_INGOT));
        int returned = leftover.isEmpty() ? 0 : leftover.getCount();

        assertEquals(offered, stored + returned, "no item may vanish between the cursor and the index");
    }
}
