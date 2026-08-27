package com.computerstorage.common.hardware;

import com.computerstorage.common.hardware.cpu.CpuComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HardwareManagerTest {
    @Test
    void installsComponentOnlyIntoMatchingSlot() {
        HardwareManager manager = new HardwareManager();
        CpuComponent cpu = new CpuComponent("Test CPU", 2, 2.0, 10);

        assertTrue(manager.install(HardwareSlot.CPU, cpu));
        assertSame(cpu, manager.get(HardwareSlot.CPU));
        assertFalse(manager.install(HardwareSlot.CPU, new CpuComponent("Second", 1, 1.0, 5)));
        assertFalse(manager.install(HardwareSlot.RAM1, cpu));
    }

    @Test
    void removalReturnsInstalledComponent() {
        HardwareManager manager = new HardwareManager();
        CpuComponent cpu = new CpuComponent("Test CPU", 1, 1.0, 5);
        manager.install(HardwareSlot.CPU, cpu);

        assertSame(cpu, manager.remove(HardwareSlot.CPU));
        assertFalse(manager.has(HardwareSlot.CPU));
        assertFalse(cpu.isInstalled());
    }
}
