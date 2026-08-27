package com.computerstorage.common.energy;

public enum EnergyTier {
    BASIC(100_000, 2_000),
    ADVANCED(1_000_000, 10_000),
    ELITE(10_000_000, 50_000),
    QUANTUM(100_000_000, 250_000);

    private final int capacity;
    private final int transfer;

    EnergyTier(int capacity, int transfer) {
        this.capacity = capacity;
        this.transfer = transfer;
    }

    public int capacity() { return capacity; }
    public int transfer() { return transfer; }
}
