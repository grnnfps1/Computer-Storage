package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.storage.VirtualStorage;

/** Coordinates virtual storage mounted in the computer's SSD slots. */
public final class StorageManager implements IComputerService {
    private final VirtualStorage storage = new VirtualStorage(0);

    public VirtualStorage storage() { return storage; }

    @Override
    public void tick(Computer computer) {
        int capacity = 0;
        for (HardwareSlot slot : new HardwareSlot[]{HardwareSlot.SSD1, HardwareSlot.SSD2, HardwareSlot.SSD3, HardwareSlot.SSD4}) {
            var component = computer.hardware().get(slot);
            if (component instanceof com.computerstorage.common.hardware.storage.SsdComponent ssd) capacity += ssd.capacity();
        }
        if (capacity >= storage.used()) storage.setCapacity(capacity);
    }
}
