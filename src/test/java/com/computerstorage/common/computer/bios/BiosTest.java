package com.computerstorage.common.computer.bios;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.ItemHardwareComponent;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiosTest {
    @Test
    void reportsMissingCpuBeforeBootDevice() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.POWER,
                new PowerComponent("Basic PSU", 100_000, 2_000));
        assertEquals(BiosResult.MISSING_CPU, computer.biosPost());
    }

    @Test
    void reportsMissingPowerWhenCpuExists() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU,
                new CpuComponent("Bronze CPU", 1, 1.0, 5));
        assertEquals(BiosResult.MISSING_POWER, computer.biosPost());
    }

    @Test
    void reportsNoBootDeviceWhenMinimumHardwareExists() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU,
                new CpuComponent("Bronze CPU", 1, 1.0, 5));
        computer.hardware().install(HardwareSlot.POWER,
                new PowerComponent("Basic PSU", 100_000, 2_000));
        assertEquals(BiosResult.NO_BOOT_DEVICE, computer.biosPost());
    }

    @Test
    void acceptsBootDeviceWhenMinimumHardwareExists() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU,
                new CpuComponent("Bronze CPU", 1, 1.0, 5));
        computer.hardware().install(HardwareSlot.POWER,
                new PowerComponent("Basic PSU", 100_000, 2_000));
        computer.insertBootDisk();
        assertEquals(BiosResult.OK, computer.biosPost());
    }
}
