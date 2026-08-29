package com.computerstorage.common.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Moves energy from a source into whatever neighbours will take it. Split out of the block entity
 * so the push can be driven in a test without a Level, the same way ControllerRuntime is.
 */
public final class CreativeEnergyPush {
    private CreativeEnergyPush() {}

    /** Offers every neighbour up to {@code perNeighbour} FE. Returns the total actually moved. */
    public static int push(IEnergyStorage source, Iterable<IEnergyStorage> neighbours, int perNeighbour) {
        if (source == null || neighbours == null || perNeighbour <= 0) return 0;
        int moved = 0;
        for (IEnergyStorage neighbour : neighbours) {
            if (neighbour == null || neighbour == source || !neighbour.canReceive()) continue;
            int offered = source.extractEnergy(perNeighbour, true);
            if (offered <= 0) continue;
            int accepted = neighbour.receiveEnergy(offered, false);
            if (accepted <= 0) continue;
            source.extractEnergy(accepted, false);
            moved += accepted;
        }
        return moved;
    }
}
