package com.computerstorage.common.hardware.power;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class PowerComponent extends HardwareComponent {
    private final String name;
    private final int capacity;
    private final int maxInput;

    public PowerComponent(String name, int capacity, int maxInput) {
        this.name = name;
        this.capacity = Math.max(1, capacity);
        this.maxInput = Math.max(1, maxInput);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.POWER; }
    public int capacity() { return capacity; }
    public int maxInput() { return maxInput; }
}
