package com.computerstorage.common.energy;

public record GeneratorProfile(String id, int generationPerTick, EnergyTier tier) {
    public GeneratorProfile {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id");
        if (generationPerTick < 0) throw new IllegalArgumentException("generationPerTick");
        if (tier == null) throw new IllegalArgumentException("tier");
    }
}
