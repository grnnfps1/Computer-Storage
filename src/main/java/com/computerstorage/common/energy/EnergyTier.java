package com.computerstorage.common.energy;

public enum EnergyTier {
    BASIC(100_000, 40, 2_000),
    ADVANCED(1_000_000, 160, 10_000),
    ELITE(10_000_000, 640, 50_000),
    QUANTUM(100_000_000, 2_560, 250_000),
    SINGULARITY(1_000_000_000, 10_240, 1_000_000);

    private final int capacity;
    private final int generation;
    private final int transfer;

    EnergyTier(int capacity, int generation, int transfer) {
        this.capacity = capacity;
        this.generation = generation;
        this.transfer = transfer;
    }
    public int capacity() { return capacity; }
    public int generation() { return generation; }
    public int transfer() { return transfer; }
}
