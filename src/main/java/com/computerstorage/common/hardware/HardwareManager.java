package com.computerstorage.common.hardware;

import java.util.EnumMap;
import java.util.Map;

public final class HardwareManager {
    private final Map<HardwareSlot, IHardwareComponent> components = new EnumMap<>(HardwareSlot.class);

    public boolean install(HardwareSlot slot, IHardwareComponent component) {
        if (slot.type() != component.getType() || components.containsKey(slot)) return false;
        components.put(slot, component);
        component.onInstalled();
        return true;
    }

    public IHardwareComponent remove(HardwareSlot slot) {
        IHardwareComponent component = components.remove(slot);
        if (component != null) component.onRemoved();
        return component;
    }

    public IHardwareComponent get(HardwareSlot slot) { return components.get(slot); }
    public boolean has(HardwareSlot slot) { return components.containsKey(slot); }
    public Map<HardwareSlot, IHardwareComponent> view() { return Map.copyOf(components); }

    public void tick() { components.values().forEach(IHardwareComponent::tick); }
}
