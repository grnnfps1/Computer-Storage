package com.computerstorage.common.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public final class EnergyNetwork {
    private final List<IEnergyStorage> sources = new ArrayList<>();
    private final List<IEnergyStorage> sinks = new ArrayList<>();

    public void addSource(IEnergyStorage storage) { if (storage != null) sources.add(storage); }
    public void addSink(IEnergyStorage storage) { if (storage != null) sinks.add(storage); }

    public int tick() {
        int moved = 0;
        for (IEnergyStorage source : sources) {
            for (IEnergyStorage sink : sinks) {
                if (source == sink || source.getEnergyStored() <= 0) continue;
                int amount = Math.min(source.getEnergyStored(), 100_000);
                int accepted = sink.receiveEnergy(source.extractEnergy(amount, true), false);
                if (accepted > 0) { source.extractEnergy(accepted, false); moved += accepted; }
            }
        }
        return moved;
    }
}
