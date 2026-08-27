package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;

public final class TaskManager implements IComputerService {
    private int activeTasks;
    public int getActiveTasks() { return activeTasks; }
    public void setActiveTasks(int tasks) { activeTasks = Math.max(0, tasks); }
    @Override public void tick(Computer computer) { }
}
