package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;

public final class TemperatureManager implements IComputerService {
    private double temperature = 25.0;
    public double getTemperature() { return temperature; }
    public void setTemperature(double value) { temperature = Math.max(0, value); }
    @Override public void tick(Computer computer) { temperature = Math.max(25.0, temperature - 0.01); }
}
