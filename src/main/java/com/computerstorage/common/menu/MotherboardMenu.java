package com.computerstorage.common.menu;

import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import com.computerstorage.common.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class MotherboardMenu extends AbstractContainerMenu {
    private final MotherboardControllerBlockEntity controller;

    public MotherboardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory,
                (MotherboardControllerBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public MotherboardMenu(int containerId, Inventory playerInventory, MotherboardControllerBlockEntity controller) {
        super(ModMenus.MOTHERBOARD.get(), containerId);
        this.controller = controller;
        for (int i = 0; i < 13; i++) addSlot(new Slot(controller, i, 8 + (i % 7) * 18, 42 + (i / 7) * 18));
        for (int i = 0; i < 16; i++) addSlot(new Slot(controller, 13 + i, 8 + (i % 8) * 18, 76 + (i / 8) * 18));
        addPlayerInventory(playerInventory);
    }

    public MotherboardControllerBlockEntity getController() { return controller; }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 124 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 182));
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem().copy();
        if (index < 29) {
            if (!moveItemStackTo(slot.getItem(), 29, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(slot.getItem(), 13, 29, false)) return ItemStack.EMPTY;
        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return original;
    }

    @Override public boolean stillValid(Player player) { return controller.isUsableByPlayer(player); }
}
