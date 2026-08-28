package com.computerstorage.common.menu;

import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.HardwareSlotRules;
import com.computerstorage.common.menu.MotherboardMenu.TypedHardwareSlot;
import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the menu/slot layer, which nothing exercised before: vanilla {@link Slot#mayPlace}
 * returns true unconditionally, so the controller's per-slot type rule bound automation through
 * {@code InvWrapper} but never the player's mouse.
 */
@BootstrapMinecraft
class MotherboardMenuSlotTest {

    /** Records what the slot asked about and answers with a fixed verdict. */
    private static final class ScriptedContainer extends StubContainer {
        private final boolean verdict;
        private int askedSlot = Integer.MIN_VALUE;
        private int calls;

        private ScriptedContainer(boolean verdict) { this.verdict = verdict; }

        @Override public boolean canPlaceItem(int slot, ItemStack stack) {
            askedSlot = slot;
            calls++;
            return verdict;
        }
    }

    /** Applies the real rule, standing in for the registry by mapping items to registry paths. */
    private static final class RuleContainer extends StubContainer {
        private final Map<Item, String> paths = new HashMap<>();

        private RuleContainer put(Item item, String path) { paths.put(item, path); return this; }

        @Override public boolean canPlaceItem(int slot, ItemStack stack) {
            return HardwareSlotRules.accepts(slot, paths.get(stack.getItem()));
        }
    }

    @Test
    void hardwareSlotAsksTheContainerAndHonoursAYes() {
        ScriptedContainer container = new ScriptedContainer(true);
        TypedHardwareSlot slot = new TypedHardwareSlot(container, HardwareSlot.CPU.ordinal(), 0, 0);

        assertTrue(slot.mayPlace(new ItemStack(Items.DIRT)));
        assertEquals(1, container.calls, "mayPlace must consult the container");
        assertEquals(HardwareSlot.CPU.ordinal(), container.askedSlot, "wrong slot index forwarded");
    }

    @Test
    void hardwareSlotHonoursANo() {
        ScriptedContainer container = new ScriptedContainer(false);
        TypedHardwareSlot slot = new TypedHardwareSlot(container, HardwareSlot.RAM1.ordinal(), 0, 0);

        assertFalse(slot.mayPlace(new ItemStack(Items.DIRT)));
        assertEquals(1, container.calls);
        assertEquals(HardwareSlot.RAM1.ordinal(), container.askedSlot);
    }

    @Test
    void cpuSocketTakesACpuAndRefusesRam() {
        RuleContainer container = new RuleContainer()
                .put(Items.DIAMOND, "cpu_bronze")
                .put(Items.REDSTONE, "ram_1gb");
        TypedHardwareSlot cpuSocket = new TypedHardwareSlot(container, HardwareSlot.CPU.ordinal(), 0, 0);

        assertTrue(cpuSocket.mayPlace(new ItemStack(Items.DIAMOND)), "a CPU belongs in the CPU socket");
        assertFalse(cpuSocket.mayPlace(new ItemStack(Items.REDSTONE)), "RAM must not enter the CPU socket");
    }

    @Test
    void ramSocketTakesRamAndRefusesACpu() {
        RuleContainer container = new RuleContainer()
                .put(Items.DIAMOND, "cpu_bronze")
                .put(Items.REDSTONE, "ram_1gb");
        TypedHardwareSlot ramSocket = new TypedHardwareSlot(container, HardwareSlot.RAM1.ordinal(), 0, 0);

        assertTrue(ramSocket.mayPlace(new ItemStack(Items.REDSTONE)));
        assertFalse(ramSocket.mayPlace(new ItemStack(Items.DIAMOND)));
    }

    @Test
    void hardwareSocketRefusesAPlainItem() {
        RuleContainer container = new RuleContainer().put(Items.DIRT, "dirt");
        for (HardwareSlot socket : HardwareSlot.values()) {
            TypedHardwareSlot slot = new TypedHardwareSlot(container, socket.ordinal(), 0, 0);
            assertFalse(slot.mayPlace(new ItemStack(Items.DIRT)), "socket " + socket + " must refuse dirt");
        }
    }

    @Test
    void plainSlotOverTheInternalBufferStillAcceptsAnything() {
        RuleContainer container = new RuleContainer().put(Items.DIRT, "dirt");
        Slot buffer = new Slot(container, HardwareSlotRules.HARDWARE_SLOTS, 0, 0);

        assertTrue(buffer.mayPlace(new ItemStack(Items.DIRT)), "the internal buffer is generic by design");
    }

    /** Minimal Container so the tests can focus on placement rules. */
    private abstract static class StubContainer implements Container {
        @Override public int getContainerSize() { return HardwareSlotRules.HARDWARE_SLOTS + 16; }
        @Override public boolean isEmpty() { return true; }
        @Override public ItemStack getItem(int slot) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { }
        @Override public void setChanged() { }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { }
    }
}
