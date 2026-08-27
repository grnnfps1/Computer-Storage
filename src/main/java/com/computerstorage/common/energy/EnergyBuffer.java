package com.computerstorage.common.energy;

import net.minecraftforge.energy.EnergyStorage;

/** Mutable FE buffer used by generators, batteries and computer power rails. */
public final class EnergyBuffer extends EnergyStorage {
    public EnergyBuffer(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int receive(int amount) { return receiveEnergy(amount, false); }
    public int extract(int amount) { return extractEnergy(amount, false); }
    public int stored() { return getEnergyStored(); }
    public int capacity() { return getMaxEnergyStored(); }
}
