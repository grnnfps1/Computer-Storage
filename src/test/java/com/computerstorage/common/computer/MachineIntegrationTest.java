package com.computerstorage.common.computer;

import com.computerstorage.common.computer.services.PowerManager;
import com.computerstorage.common.computer.services.StorageManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import com.computerstorage.common.hardware.storage.SsdComponent;
import net.minecraftforge.energy.EnergyStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the machine the way the block entity drives it: hardware in the sockets, a boot disk
 * present, an FE buffer attached, and nothing but repeated ticks.
 *
 * <p>Crucially it never calls {@link Computer#boot()}. That is the boundary the earlier tests did
 * not cross, and why they passed while the machine sat in OFF forever in game — nothing in the
 * block entity ever switched it on.
 */
class MachineIntegrationTest {

    private static final long SSD_CAPACITY = 1_024L;

    /** Stands in for the block's own FE buffer, which is a plain Forge EnergyStorage too. */
    private static EnergyStorage buffer(int stored) {
        EnergyStorage storage = new EnergyStorage(100_000, 2_000, 2_000);
        storage.receiveEnergy(stored, false);
        return storage;
    }

    private static Computer assembledMachine(EnergyStorage rail, boolean withBootDisk) {
        Computer computer = new Computer();
        computer.hardware().install(HardwareSlot.CPU, new CpuComponent("Bronze CPU", 1, 1.0, 5));
        computer.hardware().install(HardwareSlot.POWER, new PowerComponent("Basic PSU", 100_000, 2_000));
        computer.hardware().install(HardwareSlot.SSD1, new SsdComponent("1K SSD", SSD_CAPACITY));
        if (withBootDisk) computer.insertBootDisk();
        computer.services().get(PowerManager.class).attachRail(rail);
        return computer;
    }

    private static void tick(Computer computer, int times) {
        for (int i = 0; i < times; i++) computer.tick();
    }

    @Test
    void anAssembledPoweredMachineReachesRunningWithoutAnyoneCallingBoot() {
        Computer computer = assembledMachine(buffer(50_000), true);
        assertEquals(ComputerState.OFF, computer.getState(), "it must start switched off");

        tick(computer, 5);

        assertEquals(ComputerState.RUNNING, computer.getState(),
                "ticking the machine is all the block entity does; that has to be enough");
        assertTrue(computer.operatingSystem().isRunning());
    }

    @Test
    void ssdCapacityShowsUpOnceTheMachineRuns() {
        Computer computer = assembledMachine(buffer(50_000), true);
        StorageManager storage = computer.services().get(StorageManager.class);
        assertEquals(0L, storage.storage().capacity(), "an unpowered machine reports no capacity");

        tick(computer, 5);

        assertEquals(ComputerState.RUNNING, computer.getState());
        assertEquals(SSD_CAPACITY, storage.storage().capacity(),
                "the installed SSD must be reflected in the index capacity");
    }

    @Test
    void moreSsdsMeanMoreCapacity() {
        Computer computer = assembledMachine(buffer(50_000), true);
        computer.hardware().install(HardwareSlot.SSD2, new SsdComponent("4K SSD", 4_096L));
        tick(computer, 5);

        assertEquals(SSD_CAPACITY + 4_096L,
                computer.services().get(StorageManager.class).storage().capacity());
    }

    @Test
    void withoutEnergyTheMachineNeverSwitchesOn() {
        Computer computer = assembledMachine(buffer(0), true);

        tick(computer, 10);

        assertEquals(ComputerState.OFF, computer.getState(), "an empty buffer must leave it off");
        assertEquals(0L, computer.services().get(StorageManager.class).storage().capacity());
    }

    @Test
    void losingEnergyShutsARunningMachineDown() {
        EnergyStorage rail = buffer(50_000);
        Computer computer = assembledMachine(rail, true);
        tick(computer, 5);
        assertEquals(ComputerState.RUNNING, computer.getState());

        rail.extractEnergy(rail.getEnergyStored(), false);
        tick(computer, 2);

        assertEquals(ComputerState.OFF, computer.getState(), "a brownout must switch the machine off");
    }

    @Test
    void aPoweredMachineWithoutABootDiskStaysInPost() {
        Computer computer = assembledMachine(buffer(50_000), false);

        tick(computer, 10);

        assertEquals(ComputerState.POST, computer.getState());
        assertEquals(0L, computer.services().get(StorageManager.class).storage().capacity(),
                "no capacity is published while the machine cannot finish POST");
    }

    @Test
    void itStaysRunningTickAfterTickWhileTheBufferHasEnergy() {
        EnergyStorage rail = buffer(5_000);
        Computer computer = assembledMachine(rail, true);
        tick(computer, 5);
        assertEquals(ComputerState.RUNNING, computer.getState());

        for (int i = 0; i < 200; i++) {
            computer.tick();
            assertEquals(ComputerState.RUNNING, computer.getState(),
                    "the machine dropped out of RUNNING at tick " + i + " with "
                            + rail.getEnergyStored() + " FE still in the buffer");
        }
        assertTrue(rail.getEnergyStored() > 0, "this run must not have exhausted the buffer");
    }

    @Test
    void itOnlyDropsToOffOnceTheBufferCannotCoverAnotherTick() {
        EnergyStorage rail = buffer(300);
        Computer computer = assembledMachine(rail, true);
        tick(computer, 5);
        assertEquals(ComputerState.RUNNING, computer.getState());

        int ticksAlive = 0;
        while (computer.getState() == ComputerState.RUNNING && ticksAlive < 1_000) {
            computer.tick();
            ticksAlive++;
        }

        int perTick = computer.services().get(PowerManager.class).getLastDemand();
        assertEquals(ComputerState.OFF, computer.getState());
        assertTrue(rail.getEnergyStored() < perTick,
                "it must run on while the buffer can still cover a whole tick, but stopped holding "
                        + rail.getEnergyStored() + " FE against " + perTick + " FE per tick");
        assertTrue(ticksAlive > 10, "300 FE has to last far longer than a handful of ticks, was " + ticksAlive);
    }

    @Test
    void aBufferBelowOneTickOfDemandIsLeftUntouched() {
        EnergyStorage rail = buffer(3);
        Computer computer = assembledMachine(rail, true);

        tick(computer, 10);

        assertEquals(ComputerState.OFF, computer.getState(), "3 FE cannot run a machine that needs more");
        assertEquals(3, rail.getEnergyStored(),
                "a draw the machine cannot use must leave the buffer alone, not burn it down");
    }

    @Test
    void rechargingBringsTheMachineBack() {
        EnergyStorage rail = buffer(100);
        Computer computer = assembledMachine(rail, true);
        while (computer.getState() != ComputerState.OFF) computer.tick();
        assertEquals(ComputerState.OFF, computer.getState());

        rail.receiveEnergy(2_000, false);
        tick(computer, 5);

        assertEquals(ComputerState.RUNNING, computer.getState(), "energy must switch it back on");
    }

    @Test
    void runningTheMachineActuallyDrawsEnergyFromTheBuffer() {
        EnergyStorage rail = buffer(50_000);
        Computer computer = assembledMachine(rail, true);
        int before = rail.getEnergyStored();

        tick(computer, 5);

        assertTrue(rail.getEnergyStored() < before,
                "the machine must consume real FE, not supply itself");
    }
}
