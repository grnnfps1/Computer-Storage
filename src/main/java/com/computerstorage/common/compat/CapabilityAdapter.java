package com.computerstorage.common.compat;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/** Neutral capability facade used by Computer Storage integration code. */
public final class CapabilityAdapter {
    private CapabilityAdapter() {}

    @Nullable
    public static IItemHandler items(BlockEntity block, @Nullable Direction side) {
        if (block == null) return null;
        return block.getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
    }

    @Nullable
    public static IEnergyStorage energy(BlockEntity block, @Nullable Direction side) {
        if (block == null) return null;
        return block.getCapability(ForgeCapabilities.ENERGY, side).orElse(null);
    }

    public static boolean supportsItems(BlockEntity block, @Nullable Direction side) {
        return items(block, side) != null;
    }

    public static boolean supportsEnergy(BlockEntity block, @Nullable Direction side) {
        return energy(block, side) != null;
    }
}
