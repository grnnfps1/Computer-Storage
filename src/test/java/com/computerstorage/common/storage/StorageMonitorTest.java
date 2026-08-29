package com.computerstorage.common.storage;

import com.computerstorage.common.blockentity.ControllerLink;
import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The monitor's readable pieces: listing, search, withdrawal arithmetic, and the controller link. */
@BootstrapMinecraft
class StorageMonitorTest {

    private static VirtualStorage indexWith(Object... itemsAndCounts) {
        VirtualStorage index = new VirtualStorage(100_000);
        for (int i = 0; i < itemsAndCounts.length; i += 2) {
            index.insert(new ItemStack((net.minecraft.world.item.Item) itemsAndCounts[i],
                    (Integer) itemsAndCounts[i + 1]));
        }
        return index;
    }

    // ---- listing -------------------------------------------------------------------------

    @Test
    void snapshotListsEveryStoredItemWithItsFullCount() {
        VirtualStorage index = indexWith(Items.IRON_INGOT, 300, Items.DIAMOND, 7);

        List<ItemStack> listing = index.snapshot();

        assertEquals(2, listing.size());
        assertEquals(300, listing.stream().filter(s -> s.getItem() == Items.IRON_INGOT)
                .mapToInt(ItemStack::getCount).sum(), "counts past a stack must be reported whole");
        assertEquals(7, listing.stream().filter(s -> s.getItem() == Items.DIAMOND)
                .mapToInt(ItemStack::getCount).sum());
    }

    @Test
    void snapshotDoesNotEmptyTheIndex() {
        VirtualStorage index = indexWith(Items.IRON_INGOT, 10);

        index.snapshot();

        assertEquals(10, index.count(new ItemStack(Items.IRON_INGOT)), "listing must not consume");
        assertFalse(index.isEmpty());
    }

    // ---- search --------------------------------------------------------------------------

    @Test
    void searchFiltersByName() {
        List<ItemStack> listing = indexWith(Items.IRON_INGOT, 5, Items.GOLD_INGOT, 5, Items.DIAMOND, 5).snapshot();

        List<ItemStack> ingots = IndexQuery.filter(listing, "ingot");

        assertEquals(2, ingots.size());
        assertTrue(ingots.stream().allMatch(s -> s.getHoverName().getString().toLowerCase().contains("ingot")));
    }

    @Test
    void searchIsCaseInsensitiveAndTrims() {
        List<ItemStack> listing = indexWith(Items.DIAMOND, 3).snapshot();

        assertEquals(1, IndexQuery.filter(listing, "DIAMOND").size());
        assertEquals(1, IndexQuery.filter(listing, "  diamond  ").size());
    }

    @Test
    void aBlankSearchShowsEverything() {
        List<ItemStack> listing = indexWith(Items.IRON_INGOT, 5, Items.DIAMOND, 5).snapshot();

        assertEquals(2, IndexQuery.filter(listing, "").size());
        assertEquals(2, IndexQuery.filter(listing, "   ").size());
        assertEquals(2, IndexQuery.filter(listing, null).size());
    }

    @Test
    void aSearchThatMatchesNothingShowsNothing() {
        List<ItemStack> listing = indexWith(Items.DIAMOND, 5).snapshot();

        assertTrue(IndexQuery.filter(listing, "zzzz").isEmpty());
    }

    // ---- withdrawal amount ---------------------------------------------------------------

    @Test
    void aPlainClickTakesOneAndShiftTakesAStack() {
        ItemStack iron = new ItemStack(Items.IRON_INGOT);

        assertEquals(1, IndexQuery.withdrawAmount(iron, 500, false), "click takes one");
        assertEquals(64, IndexQuery.withdrawAmount(iron, 500, true), "shift takes a full stack");
    }

    @Test
    void aStackIsCappedByTheItemsOwnMaxSize() {
        assertEquals(1, IndexQuery.withdrawAmount(new ItemStack(Items.DIAMOND_SWORD), 90, true),
                "an unstackable item still yields one");
    }

    @Test
    void neverMoreThanTheIndexHolds() {
        ItemStack iron = new ItemStack(Items.IRON_INGOT);

        assertEquals(10, IndexQuery.withdrawAmount(iron, 10, true), "a shift-click cannot invent items");
        assertEquals(0, IndexQuery.withdrawAmount(iron, 0, true));
        assertEquals(0, IndexQuery.withdrawAmount(iron, 0, false));
    }

    // ---- withdrawal against the index ------------------------------------------------------

    @Test
    void withdrawingTakesExactlyWhatWasAskedAndLeavesTheRest() {
        VirtualStorage index = indexWith(Items.IRON_INGOT, 100);
        List<ItemStack> received = new ArrayList<>();

        int moved = WithdrawalService.withdraw(index, new ItemStack(Items.IRON_INGOT), 64,
                stack -> { received.add(stack.copy()); return ItemStack.EMPTY; });

        assertEquals(64, moved);
        assertEquals(64, received.get(0).getCount());
        assertEquals(36, index.count(new ItemStack(Items.IRON_INGOT)), "the rest stays in the index");
    }

    @Test
    void askingForMoreThanExistsTakesOnlyWhatExists() {
        VirtualStorage index = indexWith(Items.DIAMOND, 10);

        int moved = WithdrawalService.withdraw(index, new ItemStack(Items.DIAMOND), 64,
                stack -> ItemStack.EMPTY);

        assertEquals(10, moved);
        assertTrue(index.isEmpty());
    }

    @Test
    void whatTheInventoryCannotHoldGoesBackToTheIndex() {
        VirtualStorage index = indexWith(Items.IRON_INGOT, 64);

        // The receiver accepts only 20 and hands back the remainder, as a full inventory would.
        int moved = WithdrawalService.withdraw(index, new ItemStack(Items.IRON_INGOT), 64,
                stack -> stack.copyWithCount(stack.getCount() - 20));

        assertEquals(20, moved, "only what the player could carry counts as withdrawn");
        assertEquals(44, index.count(new ItemStack(Items.IRON_INGOT)), "the rest must not be destroyed");
    }

    @Test
    void withdrawingSomethingTheIndexDoesNotHaveMovesNothing() {
        VirtualStorage index = indexWith(Items.IRON_INGOT, 5);

        assertEquals(0, WithdrawalService.withdraw(index, new ItemStack(Items.DIAMOND), 5,
                stack -> ItemStack.EMPTY));
        assertEquals(5, index.count(new ItemStack(Items.IRON_INGOT)));
    }

    // ---- controller link -------------------------------------------------------------------

    @Test
    void theMonitorSearchesTheSixNeighbours() {
        BlockPos origin = new BlockPos(10, 64, -3);

        List<BlockPos> candidates = ControllerLink.candidatePositions(origin);

        assertEquals(6, candidates.size());
        assertTrue(candidates.contains(origin.above()));
        assertTrue(candidates.contains(origin.below()));
        assertTrue(candidates.contains(origin.north()));
        assertTrue(candidates.contains(origin.south()));
        assertTrue(candidates.contains(origin.east()));
        assertTrue(candidates.contains(origin.west()));
        assertFalse(candidates.contains(origin), "the monitor's own position is not a candidate");
    }

    @Test
    void aMonitorStandingAloneFindsNoComputer() {
        assertNull(ControllerLink.firstMatching(BlockPos.ZERO, pos -> null, String.class),
                "no neighbour, no computer");
    }

    @Test
    void aMonitorNextToAControllerFindsIt() {
        BlockPos origin = BlockPos.ZERO;
        Map<BlockPos, Object> world = new HashMap<>();
        world.put(origin.east(), "controller");

        assertEquals("controller", ControllerLink.firstMatching(origin, world::get, String.class));
    }

    @Test
    void neighboursOfTheWrongKindAreIgnored() {
        BlockPos origin = BlockPos.ZERO;
        Map<BlockPos, Object> world = new HashMap<>();
        world.put(origin.above(), 42);
        world.put(origin.north(), "controller");

        assertEquals("controller", ControllerLink.firstMatching(origin, world::get, String.class),
                "an unrelated block must not satisfy the link");
        assertNull(ControllerLink.firstMatching(origin, pos -> 42, String.class));
    }

    @Test
    void onlyNeighboursCountNotTheMonitorItself() {
        BlockPos origin = BlockPos.ZERO;
        Map<BlockPos, Object> world = new HashMap<>();
        world.put(origin, "controller");

        assertNull(ControllerLink.firstMatching(origin, world::get, String.class),
                "a monitor must not link to whatever occupies its own position");
    }
}
