package com.computerstorage.common.computer.bios;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.hardware.HardwareSlot;

public final class Bios {
    public BiosResult post(Computer computer) {
        if (!computer.hardware().has(HardwareSlot.CPU)) return BiosResult.MISSING_CPU;
        if (!computer.hardware().has(HardwareSlot.POWER)) return BiosResult.MISSING_POWER;
        if (!computer.hasBootDevice()) return BiosResult.NO_BOOT_DEVICE;
        return BiosResult.OK;
    }
}
