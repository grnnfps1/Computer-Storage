package com.computerstorage.common.hardware.gpu;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class GpuComponent extends HardwareComponent {
    private final String name;
    private final int taskMultiplier;

    public GpuComponent(String name, int taskMultiplier) {
        this.name = name;
        this.taskMultiplier = Math.max(1, taskMultiplier);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.GPU; }
    public int taskMultiplier() { return taskMultiplier; }
}
