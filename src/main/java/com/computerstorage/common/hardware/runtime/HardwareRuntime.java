package com.computerstorage.common.hardware.runtime;

import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.HardwareType;
import com.computerstorage.common.hardware.IHardwareComponent;
import com.computerstorage.common.hardware.HardwareManager;

public final class HardwareRuntime {
    private HardwareRuntime() {}
    public static boolean has(HardwareManager manager, HardwareSlot slot) { return manager != null && manager.has(slot); }
    public static boolean readyForPost(HardwareManager manager) {
        if (manager == null) return false;
        IHardwareComponent cpu = manager.get(HardwareSlot.CPU), power = manager.get(HardwareSlot.POWER);
        return cpu != null && power != null && cpu.getType() == HardwareType.CPU && power.getType() == HardwareType.POWER;
    }
}
