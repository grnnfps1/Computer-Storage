package com.computerstorage.common.hardware;

import net.minecraft.nbt.CompoundTag;

public interface IHardwareComponent {
    void onInstalled();
    void onRemoved();
    void tick();
    String getName();
    HardwareType getType();
    void save(CompoundTag tag);
    void load(CompoundTag tag);
}
