package com.computerstorage.common.energy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnergyRuntimeTest {
    @Test void tiersScale() {
        assertTrue(EnergyTier.ADVANCED.generation() > EnergyTier.BASIC.generation());
        assertTrue(EnergyTier.QUANTUM.capacity() > EnergyTier.ELITE.capacity());
    }

    @Test void generatorProducesEnergy() {
        EnergyGenerator generator = new EnergyGenerator(EnergyTier.BASIC);
        assertEquals(40, generator.generate());
        assertEquals(40, generator.getEnergyStored());
    }

    @Test void networkMovesEnergy() {
        EnergyGenerator source = new EnergyGenerator(EnergyTier.BASIC);
        EnergyGenerator sink = new EnergyGenerator(EnergyTier.BASIC);
        source.generate();
        EnergyNetwork network = new EnergyNetwork();
        network.addSource(source);
        network.addSink(sink);
        assertEquals(40, network.tick());
        assertEquals(0, source.getEnergyStored());
        assertEquals(40, sink.getEnergyStored());
    }
}
