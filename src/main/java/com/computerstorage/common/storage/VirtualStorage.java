package com.computerstorage.common.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/** Persistent logical item index backed by the computer's physical SSD capacity. */
public final class VirtualStorage {
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private long capacity;

    public VirtualStorage() { this(0); }
    public VirtualStorage(long capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity");
        this.capacity = capacity;
    }

    public long capacity() { return capacity; }
    public long used() { return entries.values().stream().mapToLong(Entry::count).sum(); }
    public long available() { return Math.max(0L, capacity - used()); }
    public boolean isEmpty() { return entries.isEmpty(); }

    public void setCapacity(long capacity) {
        if (capacity < used()) throw new IllegalArgumentException("capacity below usage");
        this.capacity = Math.max(0L, capacity);
    }

    public int insert(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int moved = (int) Math.min((long) stack.getCount(), available());
        if (moved <= 0) return 0;
        String key = key(stack);
        Entry entry = entries.get(key);
        if (entry == null) entries.put(key, new Entry(stack.copyWithCount(1), moved));
        else entry.add(moved);
        return moved;
    }

    public ItemStack extract(ItemStack template, int amount) {
        if (template.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        String key = key(template);
        Entry entry = entries.get(key);
        if (entry == null) return ItemStack.EMPTY;
        int moved = Math.min(amount, entry.count());
        ItemStack result = entry.prototype().copyWithCount(moved);
        entry.remove(moved);
        if (entry.count() == 0) entries.remove(key);
        return result;
    }

    public int count(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Entry entry = entries.get(key(stack));
        return entry == null ? 0 : entry.count();
    }

    public void clear() { entries.clear(); }

    public void save(CompoundTag tag) {
        tag.putLong("Capacity", capacity);
        ListTag list = new ListTag();
        for (Entry entry : entries.values()) {
            CompoundTag item = new CompoundTag();
            entry.prototype().save(item);
            item.putInt("StorageCount", entry.count());
            list.add(item);
        }
        tag.put("Items", list);
    }

    public void load(CompoundTag tag) {
        entries.clear();
        capacity = Math.max(0L, tag.getLong("Capacity"));
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (Tag value : list) {
            CompoundTag item = (CompoundTag) value;
            ItemStack stack = ItemStack.of(item);
            int count = item.getInt("StorageCount");
            if (!stack.isEmpty() && count > 0) entries.put(key(stack), new Entry(stack.copyWithCount(1), count));
        }
    }

    private static String key(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().key().location() + "|" + stack.getTag();
    }

    private static final class Entry {
        private final ItemStack prototype;
        private int count;
        private Entry(ItemStack prototype, int count) { this.prototype = prototype; this.count = count; }
        ItemStack prototype() { return prototype; }
        int count() { return count; }
        void add(int amount) { count = Math.addExact(count, amount); }
        void remove(int amount) { count -= amount; }
    }
}
