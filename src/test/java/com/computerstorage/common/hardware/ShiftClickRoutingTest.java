package com.computerstorage.common.hardware;

import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Where a shift-clicked item lands. The sockets have no visible label, so the routing has to be
 * right: each type to its own sockets, and repeated sockets filled one at a time rather than
 * stacked into the first.
 */
@BootstrapMinecraft
class ShiftClickRoutingTest {

    private static final int HARDWARE_SLOTS = HardwareSlotRules.HARDWARE_SLOTS;
    private static final int MACHINE_SLOTS = HARDWARE_SLOTS + 16;

    /** Stands in for the registry: plain items mapped to the paths the mod's items would have. */
    private static final Map<Item, String> PATHS = new HashMap<>();
    static {
        PATHS.put(Items.DIAMOND, "cpu_bronze");
        PATHS.put(Items.REDSTONE, "ram_1gb");
        PATHS.put(Items.EMERALD, "gpu_diamond");
        PATHS.put(Items.GOLD_INGOT, "nic_ethernet");
        PATHS.put(Items.REDSTONE_BLOCK, "psu_basic");
        PATHS.put(Items.ICE, "cooler_basic");
        PATHS.put(Items.LAPIS_LAZULI, "ssd_1k");
        PATHS.put(Items.DIRT, "dirt");
    }

    private static int[] socketsFor(Item item) {
        return HardwareSlotRules.socketRange(HardwareSlotRules.typeOf(PATHS.get(item)));
    }

    // ---- socket ranges -------------------------------------------------------------------

    @Test
    void eachTypeMapsToItsOwnSockets() {
        assertArrayEquals(new int[]{0, 1}, socketsFor(Items.DIAMOND), "CPU");
        assertArrayEquals(new int[]{1, 5}, socketsFor(Items.REDSTONE), "four RAM sockets");
        assertArrayEquals(new int[]{5, 6}, socketsFor(Items.EMERALD), "GPU");
        assertArrayEquals(new int[]{6, 7}, socketsFor(Items.GOLD_INGOT), "NIC");
        assertArrayEquals(new int[]{7, 8}, socketsFor(Items.REDSTONE_BLOCK), "PSU");
        assertArrayEquals(new int[]{8, 9}, socketsFor(Items.ICE), "cooler");
        assertArrayEquals(new int[]{9, 13}, socketsFor(Items.LAPIS_LAZULI), "four SSD sockets");
    }

    @Test
    void anItemThatIsNotHardwareHasNoSocket() {
        assertNull(socketsFor(Items.DIRT), "plain items belong in the buffer, not a socket");
        assertNull(HardwareSlotRules.socketRange(null));
    }

    // ---- distribution --------------------------------------------------------------------

    @Test
    void fourRamSticksFillFourSocketsOneEach() {
        Bench bench = new Bench();
        ItemStack ram = new ItemStack(Items.REDSTONE, 4);
        int[] sockets = socketsFor(Items.REDSTONE);

        int filled = SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], ram);

        assertEquals(4, filled);
        assertTrue(ram.isEmpty(), "the whole stack must be consumed");
        for (int slot = 1; slot <= 4; slot++) {
            assertEquals(1, bench.getItem(slot).getCount(), "socket " + slot + " holds exactly one stick");
        }
    }

    @Test
    void aStackBiggerThanTheSocketsLeavesTheRemainder() {
        Bench bench = new Bench();
        ItemStack ram = new ItemStack(Items.REDSTONE, 7);
        int[] sockets = socketsFor(Items.REDSTONE);

        assertEquals(4, SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], ram));
        assertEquals(3, ram.getCount(), "what does not fit stays with the player");
    }

    @Test
    void occupiedSocketsAreSkippedAndTheRestStillFill() {
        Bench bench = new Bench();
        bench.setItem(2, new ItemStack(Items.REDSTONE));
        ItemStack ram = new ItemStack(Items.REDSTONE, 4);
        int[] sockets = socketsFor(Items.REDSTONE);

        assertEquals(3, SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], ram),
                "three sockets were free");
        assertEquals(1, ram.getCount());
    }

    @Test
    void ssdsSpreadAcrossTheirFourSockets() {
        Bench bench = new Bench();
        ItemStack ssd = new ItemStack(Items.LAPIS_LAZULI, 4);
        int[] sockets = socketsFor(Items.LAPIS_LAZULI);

        assertEquals(4, SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], ssd));
        for (int slot = 9; slot <= 12; slot++) assertEquals(1, bench.getItem(slot).getCount());
    }

    @Test
    void aSingleSocketTakesOneAndLeavesTheRest() {
        Bench bench = new Bench();
        ItemStack cpus = new ItemStack(Items.DIAMOND, 3);
        int[] sockets = socketsFor(Items.DIAMOND);

        assertEquals(1, SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], cpus));
        assertEquals(1, bench.getItem(0).getCount());
        assertEquals(2, cpus.getCount());
    }

    @Test
    void fullSocketsMoveNothing() {
        Bench bench = new Bench();
        for (int slot = 1; slot <= 4; slot++) bench.setItem(slot, new ItemStack(Items.REDSTONE));
        ItemStack ram = new ItemStack(Items.REDSTONE, 2);
        int[] sockets = socketsFor(Items.REDSTONE);

        assertEquals(0, SlotDistribution.spreadOnePerSlot(bench, sockets[0], sockets[1], ram),
                "nothing moves when every socket is taken");
        assertEquals(2, ram.getCount());
    }

    @Test
    void theSocketRuleStillGuardsDistribution() {
        Bench bench = new Bench();
        ItemStack cpu = new ItemStack(Items.DIAMOND, 4);
        int[] ramSockets = socketsFor(Items.REDSTONE);

        assertEquals(0, SlotDistribution.spreadOnePerSlot(bench, ramSockets[0], ramSockets[1], cpu),
                "a CPU must not be distributed into RAM sockets");
        assertEquals(4, cpu.getCount());
    }

    @Test
    void nonHardwareGoesToTheBufferWhichAcceptsIt() {
        Bench bench = new Bench();
        assertNull(socketsFor(Items.DIRT), "routing sends it to the buffer instead of a socket");
        assertTrue(bench.canPlaceItem(HARDWARE_SLOTS, new ItemStack(Items.DIRT)));
        assertFalse(bench.canPlaceItem(0, new ItemStack(Items.DIRT)), "and never into a socket");
    }

    /** Container applying the real per-slot rule, standing in for the controller. */
    private static final class Bench implements Container {
        private final NonNullList<ItemStack> items = NonNullList.withSize(MACHINE_SLOTS, ItemStack.EMPTY);

        @Override public int getContainerSize() { return MACHINE_SLOTS; }
        @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); }
        @Override public void setChanged() { }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { items.clear(); }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) {
            return HardwareSlotRules.accepts(slot, PATHS.get(stack.getItem()));
        }
    }
}
