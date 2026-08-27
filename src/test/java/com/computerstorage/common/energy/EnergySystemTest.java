package com.computerstorage.common.energy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergySystemTest {
    @Test
    void bufferRespectsCapacity() {
        EnergyBuffer buffer = new EnergyBuffer(100, 100, 100);
        assertEquals(100, buffer.receive(150));
        assertEquals(100, buffer.stored());
        assertEquals(0, buffer.receive(1));
    }

    @Test
    void generatorStoresOnlyAcceptedEnergy() {
        EnergyBuffer buffer = new EnergyBuffer(100, 100, 100);
        Generator generator = new Generator(new GeneratorProfile("test", 60, EnergyTier.BASIC));
        assertEquals(60, generator.generate(buffer));
        assertEquals(40, generator.generate(buffer));
        assertEquals(100, generator.generated());
        assertEquals(100, buffer.stored());
    }

    @Test
    void tiersHaveIncreasingCapacityAndTransfer() {
        assertTrue(EnergyTier.BASIC.capacity() < EnergyTier.ADVANCED.capacity());
        assertTrue(EnergyTier.ADVANCED.capacity() < EnergyTier.ELITE.capacity());
        assertTrue(EnergyTier.BASIC.transfer() < EnergyTier.QUANTUM.transfer());
    }
}
