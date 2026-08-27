package com.computerstorage.common.hardware;

import net.minecraft.nbt.CompoundTag;

public final class HardwareRegistry {
    private HardwareRegistry() {}
    public static CompoundTag createIdentity(IHardwareComponent component) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", component.getName());
        tag.putString("type", component.getType().name());
        return tag;
    }
}
