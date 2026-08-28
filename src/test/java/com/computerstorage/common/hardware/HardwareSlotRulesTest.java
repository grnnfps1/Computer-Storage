package com.computerstorage.common.hardware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The per-slot placement rule the controller GUI and automation both apply. */
class HardwareSlotRulesTest {

    private static int index(HardwareSlot slot) { return slot.ordinal(); }

    @Test
    void hardwareSocketAcceptsOnlyItsOwnType() {
        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.CPU), "cpu_bronze"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.CPU), "ram_1gb"));

        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.RAM1), "ram_1gb"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.RAM1), "cpu_bronze"));

        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.GPU), "gpu_diamond"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.GPU), "nic_ethernet"));

        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.SSD1), "ssd_256k"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.SSD1), "psu_basic"));

        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.POWER), "psu_basic"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.POWER), "cooler_basic"));

        assertTrue(HardwareSlotRules.accepts(index(HardwareSlot.COOLER), "cooler_basic"));
        assertFalse(HardwareSlotRules.accepts(index(HardwareSlot.COOLER), "cpu_bronze"));
    }

    @Test
    void everyHardwareSocketRejectsAPlainItem() {
        for (HardwareSlot slot : HardwareSlot.values()) {
            assertFalse(HardwareSlotRules.accepts(index(slot), "dirt"),
                    "socket " + slot + " must reject a non-hardware item");
        }
    }

    @Test
    void internalBufferAcceptsAnything() {
        int firstBufferSlot = HardwareSlotRules.HARDWARE_SLOTS;
        assertTrue(HardwareSlotRules.accepts(firstBufferSlot, "dirt"));
        assertTrue(HardwareSlotRules.accepts(firstBufferSlot, "cpu_bronze"));
        assertTrue(HardwareSlotRules.accepts(firstBufferSlot + 15, "diamond_sword"));
    }

    @Test
    void everyRamSocketTakesRamAndNothingElse() {
        for (HardwareSlot slot : new HardwareSlot[]{HardwareSlot.RAM1, HardwareSlot.RAM2,
                HardwareSlot.RAM3, HardwareSlot.RAM4}) {
            assertTrue(HardwareSlotRules.accepts(index(slot), "ram_4gb"), "socket " + slot);
            assertFalse(HardwareSlotRules.accepts(index(slot), "ssd_1k"), "socket " + slot);
        }
    }

    @Test
    void typeOfMapsPathsToHardwareTypes() {
        assertSame(HardwareType.CPU, HardwareSlotRules.typeOf("cpu_quantum"));
        assertSame(HardwareType.POWER, HardwareSlotRules.typeOf("psu_elite"));
        assertSame(HardwareType.POWER, HardwareSlotRules.typeOf("power_supply"));
        assertNull(HardwareSlotRules.typeOf("dirt"));
        assertNull(HardwareSlotRules.typeOf(null));
    }

    @Test
    void negativeSlotIsRejected() {
        assertFalse(HardwareSlotRules.accepts(-1, "cpu_bronze"));
    }
}
