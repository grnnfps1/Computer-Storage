package com.computerstorage.common.hardware;

import net.minecraft.nbt.CompoundTag;

/** Base implementation for hardware that stores only its identity and lifecycle state. */
public abstract class HardwareComponent implements IHardwareComponent {
    private boolean installed;

    @Override
    public void onInstalled() { installed = true; }

    @Override
    public void onRemoved() { installed = false; }

    @Override
    public void tick() { }

    public boolean isInstalled() { return installed; }

    @Override
    public void save(CompoundTag tag) {
        tag.putBoolean("Installed", installed);
        tag.putString("Type", getType().name());
    }

    @Override
    public void load(CompoundTag tag) {
        installed = tag.getBoolean("Installed");
    }
}
