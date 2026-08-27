package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.storage.SsdComponent;
import com.computerstorage.common.storage.VirtualStorage;
import net.minecraft.nbt.CompoundTag;

/** Coordinates virtual storage mounted in the computer's physical SSD slots. */
public final class StorageManager implements IComputerService {
    private static final HardwareSlot[] SSD_SLOTS = {HardwareSlot.SSD1, HardwareSlot.SSD2, HardwareSlot.SSD3, HardwareSlot.SSD4};
    private final VirtualStorage storage = new VirtualStorage();

    public VirtualStorage storage() { return storage; }

    @Override
    public void tick(Computer computer) {
        long capacity = 0;
        for (HardwareSlot slot : SSD_SLOTS) {
            var component = computer.hardware().get(slot);
            if (component instanceof SsdComponent ssd) capacity += ssd.capacity();
        }
        if (capacity >= storage.used()) storage.setCapacity(capacity);
    }

    public void save(CompoundTag tag) { storage.save(tag); }
    public void load(CompoundTag tag) { storage.load(tag); }
}
