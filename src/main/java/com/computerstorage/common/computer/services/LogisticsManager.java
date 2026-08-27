package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;
import com.computerstorage.common.transfer.TransferNetwork;

/** Owns programmable item-routing for a Computer instance. */
public final class LogisticsManager implements IComputerService {
    private final TransferNetwork network = new TransferNetwork();
    private int lastMoved;

    public TransferNetwork network() { return network; }
    public int lastMoved() { return lastMoved; }

    @Override
    public void tick(Computer computer) {
        lastMoved = network.tick();
    }
}
