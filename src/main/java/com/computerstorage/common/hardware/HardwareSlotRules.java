package com.computerstorage.common.hardware;

import org.jetbrains.annotations.Nullable;

/**
 * Decides which item may sit in which controller slot.
 *
 * <p>Keyed on the item's registry path rather than on an {@code ItemStack} so the rule can be
 * exercised without a live registry: the mod's own items do not exist in a plain test JVM.
 */
public final class HardwareSlotRules {
    /** Slots below this index are hardware sockets; the rest are the generic internal buffer. */
    public static final int HARDWARE_SLOTS = 13;

    private HardwareSlotRules() {}

    /** The hardware type an item's registry path denotes, or null when it is not hardware. */
    @Nullable
    public static HardwareType typeOf(@Nullable String itemPath) {
        if (itemPath == null) return null;
        if (itemPath.contains("cpu")) return HardwareType.CPU;
        if (itemPath.contains("ram")) return HardwareType.RAM;
        if (itemPath.contains("gpu")) return HardwareType.GPU;
        if (itemPath.contains("nic")) return HardwareType.NIC;
        if (itemPath.contains("ssd")) return HardwareType.SSD;
        if (itemPath.contains("power") || itemPath.contains("psu")) return HardwareType.POWER;
        if (itemPath.contains("cooler")) return HardwareType.COOLER;
        return null;
    }

    /**
     * Whether the given controller slot accepts the item. Hardware sockets accept only their own
     * type; the internal buffer accepts anything by design.
     */
    public static boolean accepts(int slotIndex, @Nullable String itemPath) {
        if (slotIndex < 0) return false;
        if (slotIndex >= HARDWARE_SLOTS) return true;
        return typeOf(itemPath) == HardwareSlot.values()[slotIndex].type();
    }
}
