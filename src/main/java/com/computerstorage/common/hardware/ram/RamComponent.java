package com.computerstorage.common.hardware.ram;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class RamComponent extends HardwareComponent {
    private final String name;
    private final int capacityMb;
    private final int cacheMultiplier;

    public RamComponent(String name, int capacityMb, int cacheMultiplier) {
        this.name = name;
        this.capacityMb = Math.max(128, capacityMb);
        this.cacheMultiplier = Math.max(1, cacheMultiplier);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.RAM; }
    public int capacityMb() { return capacityMb; }
    public int cacheMultiplier() { return cacheMultiplier; }
}
