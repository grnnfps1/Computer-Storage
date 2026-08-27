package com.computerstorage.common.storage;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VirtualStorageTest {
    @Test
    void insertRespectsCapacity() {
        VirtualStorage storage = new VirtualStorage(10);
        assertEquals(10, storage.insert(new ItemStack(Items.IRON_INGOT, 16)));
        assertEquals(10, storage.used());
        assertEquals(0, storage.available());
    }

    @Test
    void extractReturnsOnlyAvailableAmount() {
        VirtualStorage storage = new VirtualStorage(20);
        ItemStack iron = new ItemStack(Items.IRON_INGOT, 12);
        storage.insert(iron);
        assertEquals(7, storage.extract(iron, 7));
        assertEquals(5, storage.count(iron));
        assertEquals(5, storage.extract(iron, 20));
        assertEquals(0, storage.used());
    }

    @Test
    void capacityCannotBeLowerThanUsage() {
        VirtualStorage storage = new VirtualStorage(10);
        storage.insert(new ItemStack(Items.IRON_INGOT, 10));
        assertThrows(IllegalArgumentException.class, () -> storage.setCapacity(9));
    }
}
