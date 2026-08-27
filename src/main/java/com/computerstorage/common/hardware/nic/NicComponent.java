package com.computerstorage.common.hardware.nic;

import com.computerstorage.common.hardware.HardwareComponent;
import com.computerstorage.common.hardware.HardwareType;

public final class NicComponent extends HardwareComponent {
    public enum Mode { ETHERNET, WIRELESS, QUANTUM }

    private final String name;
    private final Mode mode;
    private final int bandwidth;

    public NicComponent(String name, Mode mode, int bandwidth) {
        this.name = name;
        this.mode = mode;
        this.bandwidth = Math.max(1, bandwidth);
    }

    @Override public String getName() { return name; }
    @Override public HardwareType getType() { return HardwareType.NIC; }
    public Mode mode() { return mode; }
    public int bandwidth() { return bandwidth; }
}
