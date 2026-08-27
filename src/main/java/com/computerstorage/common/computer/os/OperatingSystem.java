package com.computerstorage.common.computer.os;

import net.minecraft.nbt.CompoundTag;

public final class OperatingSystem {
    private OperatingSystemState state = OperatingSystemState.NOT_INSTALLED;
    private String version = "0.1.0";

    public boolean isInstalled() { return state != OperatingSystemState.NOT_INSTALLED; }
    public boolean isRunning() { return state == OperatingSystemState.RUNNING; }
    public OperatingSystemState state() { return state; }
    public String version() { return version; }

    public void install() {
        if (state == OperatingSystemState.NOT_INSTALLED) state = OperatingSystemState.INSTALLING;
    }

    public void finishInstallation() {
        if (state == OperatingSystemState.INSTALLING) state = OperatingSystemState.READY;
    }

    public void boot() {
        if (state == OperatingSystemState.READY) state = OperatingSystemState.RUNNING;
    }

    public void shutdown() {
        if (state == OperatingSystemState.RUNNING) state = OperatingSystemState.READY;
    }

    public void save(CompoundTag tag) {
        tag.putString("state", state.name());
        tag.putString("version", version);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("state")) state = OperatingSystemState.valueOf(tag.getString("state"));
        if (tag.contains("version")) version = tag.getString("version");
    }
}
