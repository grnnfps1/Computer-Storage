package com.computerstorage.common.hardware;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/** Runtime hardware representation backed by the physical ItemStack installed in a slot. */
public final class ItemHardwareComponent implements IHardwareComponent {
    private ItemStack stack;
    private boolean installed;

    public ItemHardwareComponent(ItemStack stack) {
        this.stack = stack.copyWithCount(1);
    }

    @Override public void onInstalled() { installed = true; }
    @Override public void onRemoved() { installed = false; }
    @Override public void tick() { }
    @Override public String getName() { return stack.getHoverName().getString(); }
    @Override public HardwareType getType() {
        // One source of truth; the old fallback made every unknown item claim to be a cooler.
        return HardwareSlotRules.typeOf(stack.getItem().builtInRegistryHolder().key().location().getPath());
    }
    @Override public void save(CompoundTag tag) {
        tag.putBoolean("Installed", installed);
        CompoundTag item = new CompoundTag();
        stack.save(item);
        tag.put("Item", item);
    }
    @Override public void load(CompoundTag tag) {
        if (tag.contains("Item")) stack = ItemStack.of(tag.getCompound("Item"));
        installed = tag.getBoolean("Installed");
    }
    public ItemStack stack() { return stack.copy(); }
}
