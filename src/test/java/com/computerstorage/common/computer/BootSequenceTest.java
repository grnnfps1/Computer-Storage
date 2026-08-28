package com.computerstorage.common.computer;

import com.computerstorage.common.computer.bios.BiosResult;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The boot device now drives the state machine. Before this, nothing ever called
 * {@code insertBootDisk()}, so POST always answered NO_BOOT_DEVICE and the machine could not
 * leave the POST state.
 */
class BootSequenceTest {

    private static Computer machineWithCpuAndPower() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU, new CpuComponent("Bronze CPU", 1, 1.0, 5));
        computer.hardware().install(HardwareSlot.POWER, new PowerComponent("Basic PSU", 100_000, 2_000));
        return computer;
    }

    private static void tick(Computer computer, int times) {
        for (int i = 0; i < times; i++) computer.tick();
    }

    @Test
    void withABootDiskTheMachineReachesRunning() {
        Computer computer = machineWithCpuAndPower();
        computer.insertBootDisk();

        computer.boot();
        assertEquals(ComputerState.POST, computer.getState());

        tick(computer, 4);

        assertEquals(BiosResult.OK, computer.lastPost());
        assertEquals(ComputerState.RUNNING, computer.getState(), "the disk must carry the machine to RUNNING");
        assertTrue(computer.operatingSystem().isRunning(), "the OS must have been installed and booted");
    }

    @Test
    void withoutABootDiskTheMachineStaysInPostReportingNoBootDevice() {
        Computer computer = machineWithCpuAndPower();

        computer.boot();
        tick(computer, 4);

        assertEquals(BiosResult.NO_BOOT_DEVICE, computer.lastPost());
        assertEquals(ComputerState.POST, computer.getState(), "no boot device means no progress past POST");
        assertFalse(computer.operatingSystem().isInstalled(), "the OS must not install itself without a disk");
        assertNotEquals(ComputerState.RUNNING, computer.getState());
    }

    @Test
    void ejectingTheDiskStopsTheMachineFromBooting() {
        Computer computer = machineWithCpuAndPower();
        computer.insertBootDisk();
        assertTrue(computer.ejectBootDisk());

        computer.boot();
        tick(computer, 4);

        assertEquals(BiosResult.NO_BOOT_DEVICE, computer.lastPost());
        assertEquals(ComputerState.POST, computer.getState());
    }

    @Test
    void missingHardwareStillBlocksBootEvenWithADisk() {
        Computer computer = new Computer();
        computer.insertBootDisk();

        computer.boot();
        tick(computer, 4);

        assertEquals(BiosResult.MISSING_CPU, computer.lastPost());
        assertEquals(ComputerState.POST, computer.getState());
    }

    @Test
    void theMachineKeepsRunningOnceBooted() {
        Computer computer = machineWithCpuAndPower();
        computer.insertBootDisk();
        computer.boot();
        tick(computer, 4);
        assertEquals(ComputerState.RUNNING, computer.getState());

        long uptimeAfterBoot = computer.getUptime();
        tick(computer, 5);

        assertEquals(ComputerState.RUNNING, computer.getState());
        assertTrue(computer.getUptime() > uptimeAfterBoot, "a running machine must accumulate uptime");
    }
}
