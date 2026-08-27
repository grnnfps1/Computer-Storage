package com.computerstorage.common.hardware.cpu;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;
import net.minecraft.nbt.CompoundTag;

public final class CpuComponent extends HardwareComponent {
    private final String name;
    private final int threads;
    private final double clockGHz;
    private final int powerPerTick;

    public CpuComponent(String name, int threads, double clockGHz, int powerPerTick) {
        this.name = name;
        this.threads = Math.max(1, threads);
        this.clockGHz = Math.max(0.1, clockGHz);
        this.powerPerTick = Math.max(0, powerPerTick);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.CPU; }
    public int threads() { return threads; }
    public double clockGHz() { return clockGHz; }
    public int powerPerTick() { return powerPerTick; }

    @Override public void save(CompoundTag tag) {
        super.save(tag);
        tag.putString("Name", name);
        tag.putInt("Threads", threads);
        tag.putDouble("ClockGHz", clockGHz);
        tag.putInt("PowerPerTick", powerPerTick);
    }
}
