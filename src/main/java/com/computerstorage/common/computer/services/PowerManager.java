package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;

public final class PowerManager implements IComputerService {
    private long energy;
    private long capacity = 100_000;
    public long getEnergy() { return energy; }
    public long getCapacity() { return capacity; }
    public void setEnergy(long value) { energy = Math.max(0, Math.min(capacity, value)); }
    @Override public void tick(Computer computer) { }
}
