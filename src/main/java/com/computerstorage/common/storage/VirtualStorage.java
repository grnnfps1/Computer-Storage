package com.computerstorage.common.storage;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/** Deterministic item index used by the computer storage layer. */
public final class VirtualStorage {
    private final Map<String, Integer> counts = new HashMap<>();
    private int capacity;

    public VirtualStorage(int capacity) { if (capacity < 0) throw new IllegalArgumentException("capacity"); this.capacity = capacity; }
    public int capacity() { return capacity; }
    public int used() { return counts.values().stream().mapToInt(Integer::intValue).sum(); }
    public int available() { return Math.max(0, capacity - used()); }
    public int count(ItemStack stack) { return stack.isEmpty() ? 0 : counts.getOrDefault(key(stack), 0); }
    public int insert(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int moved = Math.min(stack.getCount(), available());
        if (moved > 0) counts.merge(key(stack), moved, Integer::sum);
        return moved;
    }
    public int extract(ItemStack template, int amount) {
        if (template.isEmpty() || amount <= 0) return 0;
        String key = key(template);
        int moved = Math.min(amount, counts.getOrDefault(key, 0));
        if (moved > 0) {
            counts.computeIfPresent(key, (k, v) -> v == moved ? null : v - moved);
        }
        return moved;
    }
    public void setCapacity(int capacity) { if (capacity < used()) throw new IllegalArgumentException("capacity below usage"); this.capacity = capacity; }
    private static String key(ItemStack stack) { return stack.getItem().toString(); }
}
