package com.computerstorage.common.energy;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.ComputerState;
import com.computerstorage.common.computer.services.PowerManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import com.computerstorage.common.hardware.storage.SsdComponent;
import net.minecraftforge.energy.IEnergyStorage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The creative cell's whole point is that it supplies energy and never runs down. These drive it
 * the way the block entity does: repeated pushes into neighbours, and a machine drawing from it
 * tick after tick.
 */
class CreativeEnergyCellTest {

    private static final int PER_TICK = 10_000;

    @Test
    void itHandsBackEverythingAskedForAndNeverDepletes() {
        CreativeEnergySource source = new CreativeEnergySource();
        int before = source.getEnergyStored();

        for (int i = 0; i < 1_000; i++) {
            assertEquals(PER_TICK, source.extractEnergy(PER_TICK, false),
                    "every extraction must be served in full, failed on call " + i);
            assertEquals(before, source.getEnergyStored(),
                    "the source must not run down, it did on call " + i);
        }
        assertTrue(source.canExtract());
    }

    @Test
    void nothingCanPushEnergyBackIntoIt() {
        CreativeEnergySource source = new CreativeEnergySource();

        assertEquals(0, source.receiveEnergy(PER_TICK, false), "a creative source accepts nothing");
        assertFalse(source.canReceive());
        assertEquals(source.getMaxEnergyStored(), source.getEnergyStored(), "it reads as always full");
    }

    @Test
    void itKeepsFillingANeighbourBufferWithoutRunningDown() {
        CreativeEnergySource source = new CreativeEnergySource();
        EnergyBuffer neighbour = new EnergyBuffer(100_000, 2_000, 2_000);
        int before = source.getEnergyStored();

        int moved = 0;
        for (int tick = 0; tick < 200; tick++) {
            moved += CreativeEnergyPush.push(source, List.of(neighbour), PER_TICK);
        }

        assertTrue(moved > 0, "the cell must actually move energy into the neighbour");
        assertEquals(neighbour.capacity(), neighbour.stored(), "the neighbour must end up full");
        assertEquals(before, source.getEnergyStored(), "and the cell must be untouched by all of it");
    }

    @Test
    void itLeavesNeighboursThatCannotReceiveAlone() {
        CreativeEnergySource source = new CreativeEnergySource();
        IEnergyStorage extractOnly = new EnergyBuffer(100_000, 0, 2_000);

        assertEquals(0, CreativeEnergyPush.push(source, List.of(extractOnly), PER_TICK));
        assertEquals(0, extractOnly.getEnergyStored());
    }

    @Test
    void aMachineWiredToTheCellStaysRunningIndefinitely() {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU, new CpuComponent("Bronze CPU", 1, 1.0, 5));
        computer.hardware().install(HardwareSlot.POWER, new PowerComponent("Basic PSU", 100_000, 2_000));
        computer.hardware().install(HardwareSlot.SSD1, new SsdComponent("1K SSD", 1_024L));
        computer.insertBootDisk();
        CreativeEnergySource source = new CreativeEnergySource();
        computer.services().get(PowerManager.class).attachRail(source);

        for (int i = 0; i < 5; i++) computer.tick();
        assertEquals(ComputerState.RUNNING, computer.getState());

        for (int i = 0; i < 10_000; i++) {
            computer.tick();
            assertEquals(ComputerState.RUNNING, computer.getState(),
                    "the machine dropped out of RUNNING at tick " + i + " on an endless source");
        }
        assertEquals(source.getMaxEnergyStored(), source.getEnergyStored(),
                "10000 ticks of draw must leave the creative cell exactly as full as it started");
    }
}
