package com.computerstorage.common.energy;

import net.minecraftforge.energy.EnergyStorage;

public final class EnergyGenerator extends EnergyStorage {
    private final EnergyTier tier;

    public EnergyGenerator(EnergyTier tier) {
        super(tier.capacity(), tier.transfer(), tier.transfer());
        this.tier = tier;
    }

    public EnergyTier tier() { return tier; }
    public int generate() { return receiveEnergy(tier.generation(), false); }
}
