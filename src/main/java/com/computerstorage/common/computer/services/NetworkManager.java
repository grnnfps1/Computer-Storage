package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;

public final class NetworkManager implements IComputerService {
    private boolean connected;
    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }
    @Override public void tick(Computer computer) { }
}
