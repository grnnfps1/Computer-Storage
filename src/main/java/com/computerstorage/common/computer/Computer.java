package com.computerstorage.common.computer;

import com.computerstorage.common.computer.bios.Bios;
import com.computerstorage.common.computer.bios.BiosResult;
import com.computerstorage.common.computer.os.OperatingSystem;
import com.computerstorage.common.computer.services.*;
import com.computerstorage.common.hardware.HardwareManager;
import net.minecraft.nbt.CompoundTag;

public final class Computer {
    private final ServiceContainer services = new ServiceContainer();
    private final Bios bios = new Bios();
    private final OperatingSystem operatingSystem = new OperatingSystem();
    private ComputerState state = ComputerState.OFF;
    private long uptime;
    private BiosResult lastPost = BiosResult.NO_BOOT_DEVICE;
    private boolean bootDiskInserted;

    public Computer() {
        services.register(HardwareManager.class, new HardwareManager());
        services.register(PowerManager.class, new PowerManager());
        services.register(TaskManager.class, new TaskManager());
        services.register(TemperatureManager.class, new TemperatureManager());
        services.register(NetworkManager.class, new NetworkManager());
        services.register(LogisticsManager.class, new LogisticsManager());
        services.register(StorageManager.class, new StorageManager());
    }

    public void tick() {
        if (state == ComputerState.OFF) return;
        if (state == ComputerState.POST) {
            lastPost = bios.post(this);
            if (lastPost == BiosResult.OK) state = operatingSystem.isInstalled() ? ComputerState.RUNNING : ComputerState.BIOS;
            return;
        }
        services.get(HardwareManager.class).tick();
        services.get(PowerManager.class).tick(this);
        services.get(TaskManager.class).tick(this);
        services.get(TemperatureManager.class).tick(this);
        services.get(NetworkManager.class).tick(this);
        services.get(StorageManager.class).tick(this);
        services.get(LogisticsManager.class).tick(this);
        uptime++;
    }

    public void boot() { if (state == ComputerState.OFF) state = ComputerState.POST; }
    public void shutdown() { operatingSystem.shutdown(); state = ComputerState.SHUTDOWN; }
    public void powerOff() { operatingSystem.shutdown(); state = ComputerState.OFF; }
    public BiosResult biosPost() { return bios.post(this); }
    public void enterBootloader() { if (state == ComputerState.BIOS) state = ComputerState.BOOTLOADER; }
    public void enterRunning() { if (state == ComputerState.BOOTLOADER && operatingSystem.isInstalled()) { operatingSystem.boot(); state = ComputerState.RUNNING; } }
    public boolean hasBootDevice() { return bootDiskInserted; }
    public boolean insertBootDisk() { if (bootDiskInserted) return false; bootDiskInserted = true; return true; }
    public boolean ejectBootDisk() { if (!bootDiskInserted) return false; bootDiskInserted = false; return true; }
    public HardwareManager hardware() { return services.get(HardwareManager.class); }
    public OperatingSystem operatingSystem() { return operatingSystem; }
    public StorageManager storage() { return services.get(StorageManager.class); }
    public BiosResult lastPost() { return lastPost; }
    public ComputerState getState() { return state; }
    public long getUptime() { return uptime; }
    public ServiceContainer services() { return services; }

    public void save(CompoundTag tag) {
        tag.putString("state", state.name());
        tag.putLong("uptime", uptime);
        tag.putString("lastPost", lastPost.name());
        tag.putBoolean("BootDiskInserted", bootDiskInserted);
        CompoundTag os = new CompoundTag();
        operatingSystem.save(os);
        tag.put("OperatingSystem", os);
        CompoundTag storageTag = new CompoundTag();
        storage().save(storageTag);
        tag.put("Storage", storageTag);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("state")) state = ComputerState.valueOf(tag.getString("state"));
        uptime = tag.getLong("uptime");
        if (tag.contains("lastPost")) lastPost = BiosResult.valueOf(tag.getString("lastPost"));
        bootDiskInserted = tag.getBoolean("BootDiskInserted");
        if (tag.contains("OperatingSystem")) operatingSystem.load(tag.getCompound("OperatingSystem"));
        if (tag.contains("Storage")) storage().load(tag.getCompound("Storage"));
    }
}
