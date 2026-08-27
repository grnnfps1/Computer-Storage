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
        String id = stack.getItem().builtInRegistryHolder().key().location().getPath();
        if (id.contains("cpu")) return HardwareType.CPU;
        if (id.contains("ram")) return HardwareType.RAM;
        if (id.contains("gpu")) return HardwareType.GPU;
        if (id.contains("nic")) return HardwareType.NIC;
        if (id.contains("ssd")) return HardwareType.SSD;
        if (id.contains("power")) return HardwareType.POWER;
        return HardwareType.COOLER;
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
