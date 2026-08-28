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
    void drainAllHandsBackEverythingAndEmptiesTheIndex() {
        VirtualStorage storage = new VirtualStorage(1_000);
        storage.insert(new ItemStack(Items.IRON_INGOT, 40));
        storage.insert(new ItemStack(Items.DIAMOND, 7));

        java.util.List<ItemStack> drained = storage.drainAll();

        int iron = drained.stream().filter(s -> s.getItem() == Items.IRON_INGOT).mapToInt(ItemStack::getCount).sum();
        int diamond = drained.stream().filter(s -> s.getItem() == Items.DIAMOND).mapToInt(ItemStack::getCount).sum();
        assertEquals(40, iron, "every stored item must come back");
        assertEquals(7, diamond);
        assertTrue(storage.isEmpty(), "the index must be empty after draining");
        assertEquals(0, storage.used());
    }

    @Test
    void drainAllSplitsIntoStacksTheWorldCanHold() {
        VirtualStorage storage = new VirtualStorage(1_000);
        storage.insert(new ItemStack(Items.IRON_INGOT, 200));

        java.util.List<ItemStack> drained = storage.drainAll();

        assertEquals(200, drained.stream().mapToInt(ItemStack::getCount).sum());
        for (ItemStack stack : drained) {
            assertTrue(stack.getCount() <= stack.getMaxStackSize(), "no stack may exceed its max size");
        }
        assertEquals(4, drained.size(), "200 iron ingots is four stacks of at most 64");
    }

    @Test
    void drainAllOnAnEmptyIndexReturnsNothing() {
        assertTrue(new VirtualStorage(100).drainAll().isEmpty());
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
