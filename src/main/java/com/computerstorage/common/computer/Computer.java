package com.computerstorage.common.computer;

import com.computerstorage.common.computer.services.*;
import com.computerstorage.common.hardware.HardwareManager;
import net.minecraft.nbt.CompoundTag;

public final class Computer {
    private final ServiceContainer services = new ServiceContainer();
    private ComputerState state = ComputerState.OFF;
    private long uptime;

    public Computer() {
        services.register(HardwareManager.class, new HardwareManager());
        services.register(PowerManager.class, new PowerManager());
        services.register(TaskManager.class, new TaskManager());
        services.register(TemperatureManager.class, new TemperatureManager());
        services.register(NetworkManager.class, new NetworkManager());
        services.register(LogisticsManager.class, new LogisticsManager());
    }

    public void tick() {
        if (state == ComputerState.OFF) return;
        services.get(HardwareManager.class).tick();
        services.get(PowerManager.class).tick(this);
        services.get(TaskManager.class).tick(this);
        services.get(TemperatureManager.class).tick(this);
        services.get(NetworkManager.class).tick(this);
        services.get(LogisticsManager.class).tick(this);
        uptime++;
    }

    public void boot() { if (state == ComputerState.OFF) state = ComputerState.POST; }
    public void shutdown() { state = ComputerState.SHUTDOWN; }
    public void powerOff() { state = ComputerState.OFF; }
    public ComputerState getState() { return state; }
    public long getUptime() { return uptime; }
    public ServiceContainer services() { return services; }

    public void save(CompoundTag tag) {
        tag.putString("state", state.name());
        tag.putLong("uptime", uptime);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("state")) state = ComputerState.valueOf(tag.getString("state"));
        uptime = tag.getLong("uptime");
    }
}
