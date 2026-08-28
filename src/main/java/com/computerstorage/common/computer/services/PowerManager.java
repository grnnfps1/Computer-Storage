package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/** Computes internal FE demand from installed hardware. */
public final class PowerManager implements IComputerService {
    private int lastDemand;
    private int lastSupplied;
    private boolean powered;
    @Nullable private IEnergyStorage rail;

    /**
     * Binds the machine to the real FE buffer it draws from. Without a rail the manager supplies
     * itself, which keeps the domain tests independent of energy.
     */
    public void attachRail(@Nullable IEnergyStorage rail) { this.rail = rail; }

    public boolean hasRail() { return rail != null; }

    public int getLastDemand() { return lastDemand; }
    public int getLastSupplied() { return lastSupplied; }
    public boolean isPowered() { return powered; }

    public int demand(Computer computer) {
        int demand = 1;
        var cpu = computer.hardware().get(HardwareSlot.CPU);
        if (cpu instanceof CpuComponent processor) demand += processor.powerPerTick();
        if (computer.hardware().has(HardwareSlot.GPU)) demand += 4;
        if (computer.hardware().has(HardwareSlot.NIC)) demand += 1;
        if (computer.hardware().has(HardwareSlot.SSD1) || computer.hardware().has(HardwareSlot.SSD2)
                || computer.hardware().has(HardwareSlot.SSD3) || computer.hardware().has(HardwareSlot.SSD4)) demand += 1;
        lastDemand = demand;
        return demand;
    }

    public void consume(int supplied) {
        lastSupplied = Math.max(0, Math.min(lastDemand, supplied));
        powered = lastSupplied >= lastDemand;
    }

    @Override public void tick(Computer computer) {
        int required = demand(computer);
        consume(rail == null ? required : rail.extractEnergy(required, false));
    }
}
