package com.computerstorage.common.serialization;

import com.computerstorage.common.computer.Computer;
import net.minecraft.nbt.CompoundTag;

public final class ComputerSerializer {
    public CompoundTag save(Computer computer) { CompoundTag tag = new CompoundTag(); computer.save(tag); return tag; }
    public void load(Computer computer, CompoundTag tag) { computer.load(tag); }
}
