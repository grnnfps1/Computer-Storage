package com.computerstorage.common.blockentity;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.ComputerState;
import com.computerstorage.common.computer.services.StorageManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.IHardwareComponent;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import com.computerstorage.common.hardware.storage.SsdComponent;
import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.energy.EnergyStorage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end exercise of the controller as the world drives it: items sitting in the sockets and
 * the buffer, an FE buffer beside them, and nothing but repeated server ticks.
 *
 * <p>This covers the whole chain in one place — tick runs, energy is drawn, the assembled machine
 * boots, the SSDs publish their capacity, and a stack in the buffer lands in the index — rather
 * than each link in isolation, which is how earlier breaks slipped through.
 */
@BootstrapMinecraft
class ControllerRuntimeTest {

    private static final int HARDWARE_SLOTS = 13;
    private static final int SLOT_COUNT = HARDWARE_SLOTS + 16;
    private static final long SSD_CAPACITY = 1_024L;

    /** Stands in for the registry: maps plain items onto the roles the mod's items would have. */
    private static final Item CPU = Items.DIAMOND;
    private static final Item PSU = Items.REDSTONE_BLOCK;
    private static final Item SSD = Items.LAPIS_LAZULI;
    private static final Item BOOT_DISK = Items.MUSIC_DISC_CAT;
    private static final Item CARGO = Items.IRON_INGOT;

    /** Mirrors production: the item hands over the typed component it carries. */
    private static final Map<Item, java.util.function.Supplier<IHardwareComponent>> PARTS = new HashMap<>();
    static {
        PARTS.put(CPU, () -> new CpuComponent("Bronze CPU", 1, 1.0, 5));
        PARTS.put(PSU, () -> new PowerComponent("Basic PSU", 100_000, 2_000));
        PARTS.put(SSD, () -> new SsdComponent("1K SSD", SSD_CAPACITY));
    }

    private static IHardwareComponent partOf(ItemStack stack) {
        var factory = PARTS.get(stack.getItem());
        return factory == null ? null : factory.get();
    }
    private static boolean isBootDisk(ItemStack stack) { return stack.getItem() == BOOT_DISK; }

    private static final class Machine {
        final Bench inventory = new Bench();
        final Computer computer = new Computer();
        final EnergyStorage rail = new EnergyStorage(100_000, 2_000, 2_000);
        final ControllerRuntime runtime = new ControllerRuntime(
                inventory, computer, HARDWARE_SLOTS,
                ControllerRuntimeTest::partOf, ControllerRuntimeTest::isBootDisk);

        Machine assemble() {
            inventory.setItem(HardwareSlot.CPU.ordinal(), new ItemStack(CPU));
            inventory.setItem(HardwareSlot.POWER.ordinal(), new ItemStack(PSU));
            inventory.setItem(HardwareSlot.SSD1.ordinal(), new ItemStack(SSD));
            return this;
        }

        Machine withBootDisk() {
            inventory.setItem(HARDWARE_SLOTS, new ItemStack(BOOT_DISK));
            return this;
        }

        Machine charged(int fe) { rail.receiveEnergy(fe, false); return this; }

        Machine tick(int times) {
            for (int i = 0; i < times; i++) runtime.tick(rail);
            return this;
        }

        long capacity() { return computer.services().get(StorageManager.class).storage().capacity(); }
        ComputerState state() { return computer.getState(); }
    }

    @Test
    void theWholeChainWorksFromSocketsToStoredItem() {
        Machine machine = new Machine().assemble().withBootDisk().charged(50_000);
        machine.inventory.setItem(HARDWARE_SLOTS + 1, new ItemStack(CARGO, 12));
        int energyBefore = machine.rail.getEnergyStored();

        machine.tick(10);

        assertEquals(ComputerState.RUNNING, machine.state(), "an assembled, powered machine must run");
        assertTrue(machine.rail.getEnergyStored() < energyBefore, "it must burn real FE");
        assertEquals(SSD_CAPACITY, machine.capacity(), "the installed SSD must publish its capacity");
        assertEquals(12, machine.computer.storage().storage().count(new ItemStack(CARGO)),
                "the stack in the buffer must land in the index");
        assertTrue(machine.inventory.getItem(HARDWARE_SLOTS + 1).isEmpty(), "and leave the buffer slot");
    }

    @Test
    void theHardwareInTheSocketsIsInstalled() {
        Machine machine = new Machine().assemble().withBootDisk().charged(50_000).tick(3);

        assertTrue(machine.computer.hardware().has(HardwareSlot.CPU));
        assertTrue(machine.computer.hardware().has(HardwareSlot.POWER));
        assertTrue(machine.computer.hardware().has(HardwareSlot.SSD1));
    }

    @Test
    void theBootDiskIsNotSwallowedByTheIndex() {
        Machine machine = new Machine().assemble().withBootDisk().charged(50_000).tick(10);

        assertEquals(ComputerState.RUNNING, machine.state());
        assertTrue(isBootDisk(machine.inventory.getItem(HARDWARE_SLOTS)),
                "the boot disk must stay in its slot, or the machine loses its boot device");
    }

    @Test
    void withoutEnergyNothingHappens() {
        Machine machine = new Machine().assemble().withBootDisk().tick(10);

        assertEquals(ComputerState.OFF, machine.state());
        assertEquals(0L, machine.capacity());
    }

    @Test
    void withoutABootDiskItStaysInPostAndStoresNothing() {
        Machine machine = new Machine().assemble().charged(50_000);
        machine.inventory.setItem(HARDWARE_SLOTS, new ItemStack(CARGO, 5));

        machine.tick(10);

        assertEquals(ComputerState.POST, machine.state());
        assertEquals(0L, machine.capacity());
        assertEquals(5, machine.inventory.getItem(HARDWARE_SLOTS).getCount(), "nothing may be absorbed");
    }

    @Test
    void removingTheSsdRemovesTheCapacity() {
        Machine machine = new Machine().assemble().withBootDisk().charged(50_000).tick(6);
        assertEquals(SSD_CAPACITY, machine.capacity());

        machine.inventory.setItem(HardwareSlot.SSD1.ordinal(), ItemStack.EMPTY);
        machine.tick(3);

        assertEquals(0L, machine.capacity(), "pulling the SSD must drop the capacity");
    }

    @Test
    void aWrongTypeInASocketIsNotInstalled() {
        Machine machine = new Machine().assemble().withBootDisk().charged(50_000);
        machine.inventory.setItem(HardwareSlot.GPU.ordinal(), new ItemStack(CPU));

        machine.tick(5);

        assertTrue(machine.computer.hardware().has(HardwareSlot.CPU));
        assertFalse(machine.computer.hardware().has(HardwareSlot.GPU),
                "a CPU must not install into the GPU socket");
    }

    /** Plain 29-slot container standing in for the block entity's inventory. */
    private static final class Bench implements Container {
        private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

        @Override public int getContainerSize() { return SLOT_COUNT; }
        @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); }
        @Override public void setChanged() { }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { items.clear(); }
    }
}
