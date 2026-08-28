package com.computerstorage.common.blockentity;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.ComputerState;
import com.computerstorage.common.computer.services.PowerManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.IHardwareComponent;
import com.computerstorage.common.storage.StorageIntake;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The controller's per-tick sequence, separated from the block entity so the whole chain can be
 * driven in a test: sockets to hardware, buffer to boot device, energy to the machine, and buffer
 * to the storage index.
 *
 * <p>The component factory is injected: production builds the typed component the hardware item
 * carries, so the installed part keeps its real stats instead of an untyped wrapper.
 */
public final class ControllerRuntime {
    private final Container inventory;
    private final Computer computer;
    private final int hardwareSlots;
    private final Function<ItemStack, IHardwareComponent> componentFactory;
    private final Predicate<ItemStack> isBootDisk;

    public ControllerRuntime(Container inventory, Computer computer, int hardwareSlots,
                             Function<ItemStack, IHardwareComponent> componentFactory,
                             Predicate<ItemStack> isBootDisk) {
        this.inventory = inventory;
        this.computer = computer;
        this.hardwareSlots = hardwareSlots;
        this.componentFactory = componentFactory;
        this.isBootDisk = isBootDisk;
    }

    /** Runs one server tick of the machine against the given energy buffer. */
    public void tick(@Nullable IEnergyStorage rail) {
        syncHardware();
        syncBootDevice();
        computer.services().get(PowerManager.class).attachRail(rail);
        computer.tick();
        drainBufferIntoIndex();
    }

    private void syncHardware() {
        var manager = computer.hardware();
        for (HardwareSlot slot : HardwareSlot.values()) {
            int index = slot.ordinal();
            if (index >= inventory.getContainerSize()) continue;
            ItemStack stack = inventory.getItem(index);
            if (stack.isEmpty()) {
                if (manager.has(slot)) manager.remove(slot);
                continue;
            }
            if (manager.has(slot)) continue;
            IHardwareComponent component = componentFactory.apply(stack);
            if (component != null && component.getType() == slot.type()) manager.install(slot, component);
        }
    }

    private void syncBootDevice() {
        boolean present = false;
        for (int slot = hardwareSlots; slot < inventory.getContainerSize(); slot++) {
            if (isBootDisk.test(inventory.getItem(slot))) { present = true; break; }
        }
        if (present) computer.insertBootDisk(); else computer.ejectBootDisk();
    }

    private void drainBufferIntoIndex() {
        if (computer.getState() != ComputerState.RUNNING) return;
        StorageIntake.drain(inventory, hardwareSlots, inventory.getContainerSize(),
                computer.storage().storage(), stack -> !isBootDisk.test(stack));
    }
}
