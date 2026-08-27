package com.computerstorage.common.hardware;

import net.minecraft.nbt.CompoundTag;

public abstract class AbstractHardwareComponent implements IHardwareComponent {
    private final String name;
    private final HardwareType type;

    protected AbstractHardwareComponent(String name, HardwareType type) { this.name = name; this.type = type; }
    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return type; }
    @Override public void onInstalled() { }
    @Override public void onRemoved() { }
    @Override public void tick() { }
    @Override public void save(CompoundTag tag) { tag.putString("name", name); tag.putString("type", type.name()); }
    @Override public void load(CompoundTag tag) { }
}
