package com.computerstorage.common.hardware.storage;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class SsdComponent extends HardwareComponent {
    private final String name;
    private final long capacity;

    public SsdComponent(String name, long capacity) {
        this.name = name;
        this.capacity = Math.max(1, capacity);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.SSD; }
    public long capacity() { return capacity; }
}
