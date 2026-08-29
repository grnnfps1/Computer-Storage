package com.computerstorage.common.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * DEBUG/CREATIVE ONLY. An FE source that never runs out: every extraction hands back the whole
 * amount asked for and the stored energy never moves. Nothing can push energy back into it.
 *
 * <p>This is a testing tool, not survival content. It exists so a machine can be driven with
 * continuous power while the mod has no real generator.
 */
public final class CreativeEnergySource implements IEnergyStorage {
    /**
     * Deliberately not {@link Integer#MAX_VALUE}: callers that add a stored value to something
     * else would overflow into a negative number and read as an empty buffer.
     */
    public static final int UNLIMITED = 1_000_000_000;

    @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }

    @Override public int extractEnergy(int maxExtract, boolean simulate) {
        return Math.max(0, maxExtract);
    }

    @Override public int getEnergyStored() { return UNLIMITED; }
    @Override public int getMaxEnergyStored() { return UNLIMITED; }
    @Override public boolean canExtract() { return true; }
    @Override public boolean canReceive() { return false; }
}
