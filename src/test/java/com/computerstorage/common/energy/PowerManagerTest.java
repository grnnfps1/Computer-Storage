package com.computerstorage.common.energy;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.services.PowerManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerManagerTest {
    @Test
    void cpuPowerIsIncludedInDemand() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU, new CpuComponent("Test CPU", 1, 1.0, 5));
        PowerManager power = computer.services().get(PowerManager.class);
        assertEquals(6, power.demand(computer));
        power.consume(6);
        assertTrue(power.isPowered());
    }

    @Test
    void insufficientPowerIsDetected() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU, new CpuComponent("Test CPU", 1, 1.0, 5));
        PowerManager power = computer.services().get(PowerManager.class);
        power.demand(computer);
        power.consume(2);
        assertFalse(power.isPowered());
    }
}
