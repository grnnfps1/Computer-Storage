package com.computerstorage.common.storage;

import com.computerstorage.test.BootstrapMinecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@BootstrapMinecraft
class VirtualStorageTest {
    @Test
    void insertRespectsCapacity() {
        VirtualStorage storage = new VirtualStorage(10);
        assertEquals(10, storage.insert(new ItemStack(Items.IRON_INGOT, 16)));
        assertEquals(10, storage.used());
        assertEquals(0, storage.available());
    }

    @Test
    void extractReturnsStoredStack() {
        VirtualStorage storage = new VirtualStorage(20);
        ItemStack iron = new ItemStack(Items.IRON_INGOT, 12);
        storage.insert(iron);
        ItemStack extracted = storage.extract(iron, 7);
        assertEquals(7, extracted.getCount());
        assertEquals(5, storage.count(iron));
        assertEquals(5, storage.extract(iron, 20).getCount());
        assertEquals(0, storage.used());
    }

    @Test
    void capacityCannotBeLowerThanUsage() {
        VirtualStorage storage = new VirtualStorage(10);
        storage.insert(new ItemStack(Items.IRON_INGOT, 10));
        assertThrows(IllegalArgumentException.class, () -> storage.setCapacity(9));
    }

    @Test
    void nbtCreatesDistinctEntries() {
        VirtualStorage storage = new VirtualStorage(20);
        ItemStack a = new ItemStack(Items.PAPER, 3);
        a.getOrCreateTag().putString("variant", "a");
        ItemStack b = new ItemStack(Items.PAPER, 4);
        b.getOrCreateTag().putString("variant", "b");
        storage.insert(a);
        storage.insert(b);
        assertEquals(3, storage.count(a));
        assertEquals(4, storage.count(b));
    }

    @Test
    void savesAndRestoresItems() {
        VirtualStorage original = new VirtualStorage(100);
        original.insert(new ItemStack(Items.DIAMOND, 32));
        CompoundTag tag = new CompoundTag();
        original.save(tag);
        VirtualStorage restored = new VirtualStorage();
        restored.load(tag);
        assertEquals(100, restored.capacity());
        assertEquals(32, restored.count(new ItemStack(Items.DIAMOND)));
    }
}
