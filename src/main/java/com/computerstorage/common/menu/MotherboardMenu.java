package com.computerstorage.common.menu;

import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import com.computerstorage.common.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.computerstorage.common.hardware.HardwareSlotRules;
import com.computerstorage.common.hardware.SlotDistribution;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class MotherboardMenu extends AbstractContainerMenu {
    /** Slots 0..MACHINE_SLOTS-1 belong to the machine; the rest are the player's. */
    private static final int MACHINE_SLOTS = 29;

    private final MotherboardControllerBlockEntity controller;
    /** Server-side values mirrored to the client; the screen has no other way to see them. */
    private int energy, storageUsed, storageCapacity;

    public MotherboardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory,
                (MotherboardControllerBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public MotherboardMenu(int containerId, Inventory playerInventory, MotherboardControllerBlockEntity controller) {
        super(ModMenus.MOTHERBOARD.get(), containerId);
        this.controller = controller;
        for (int i = 0; i < 13; i++) addSlot(new TypedHardwareSlot(controller, i, 8 + (i % 7) * 18, 52 + (i / 7) * 18));
        for (int i = 0; i < 16; i++) addSlot(new Slot(controller, 13 + i, 8 + (i % 8) * 18, 105 + (i / 8) * 18));
        addPlayerInventory(playerInventory);
        syncInt(() -> controller.energyStored(), value -> energy = value);
        syncInt(() -> clampToInt(controller.storageUsed()), value -> storageUsed = value);
        syncInt(() -> clampToInt(controller.storageCapacity()), value -> storageCapacity = value);
    }

    public MotherboardControllerBlockEntity getController() { return controller; }

    public int energy() { return energy; }
    public int storageUsed() { return storageUsed; }
    public int storageCapacity() { return storageCapacity; }

    private void syncInt(java.util.function.IntSupplier read, java.util.function.IntConsumer write) {
        addDataSlot(new DataSlot() {
            @Override public int get() { return read.getAsInt(); }
            @Override public void set(int value) { write.accept(value); }
        });
    }

    private static int clampToInt(long value) { return (int) Math.max(0L, Math.min(Integer.MAX_VALUE, value)); }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 158 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 216));
    }

    /**
     * Smart shift-click. The sockets carry no visible label, so an item shift-clicked from the
     * player's inventory routes itself: hardware to the sockets of its own type, spread one per
     * socket so four RAM sticks fill four sockets, and anything else into the generic buffer.
     * Items already inside the machine go back to the player.
     */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack inSlot = slot.getItem();
        ItemStack original = inSlot.copy();

        boolean moved = index < MACHINE_SLOTS
                ? moveItemStackTo(inSlot, MACHINE_SLOTS, slots.size(), true)
                : moveIntoMachine(inSlot);
        if (!moved) return ItemStack.EMPTY;

        if (inSlot.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (inSlot.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, inSlot);
        return original;
    }

    /** Routes an incoming stack to the sockets of its type, or to the buffer when it is not hardware. */
    private boolean moveIntoMachine(ItemStack stack) {
        int[] sockets = HardwareSlotRules.socketRange(
                HardwareSlotRules.typeOf(stack.getItem().builtInRegistryHolder().key().location().getPath()));
        if (sockets != null) {
            return SlotDistribution.spreadOnePerSlot(controller, sockets[0], sockets[1], stack) > 0;
        }
        return moveItemStackTo(stack, HardwareSlotRules.HARDWARE_SLOTS, MACHINE_SLOTS, false);
    }

    @Override public boolean stillValid(Player player) { return controller.isUsableByPlayer(player); }

    /**
     * Hardware socket that applies the container's per-slot type rule to mouse placement.
     * Vanilla {@link Slot#mayPlace} returns true unconditionally and never consults
     * {@link Container#canPlaceItem}, so without this the rule only bound automation.
     */
    static final class TypedHardwareSlot extends Slot {
        TypedHardwareSlot(Container container, int index, int x, int y) { super(container, index, x, y); }

        @Override public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(getContainerSlot(), stack);
        }
    }
}
