package com.computerstorage.common.hardware.cooling;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class CoolerComponent extends HardwareComponent {
    private final String name;
    private final int coolingPerTick;

    public CoolerComponent(String name, int coolingPerTick) {
        this.name = name;
        this.coolingPerTick = Math.max(1, coolingPerTick);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.COOLER; }
    public int coolingPerTick() { return coolingPerTick; }
}
