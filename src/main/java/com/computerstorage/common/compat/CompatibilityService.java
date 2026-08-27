package com.computerstorage.common.compat;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/** Central compatibility entry point. Optional integrations can be added without changing logistics. */
public final class CompatibilityService {
    private CompatibilityService() {}

    @Nullable public static IItemHandler itemHandler(BlockEntity block, net.minecraft.core.Direction side) {
        return CapabilityAdapter.items(block, side);
    }

    @Nullable public static IEnergyStorage energyHandler(BlockEntity block, net.minecraft.core.Direction side) {
        return CapabilityAdapter.energy(block, side);
    }

    public static boolean isItemCompatible(BlockEntity block, net.minecraft.core.Direction side) {
        return CapabilityAdapter.supportsItems(block, side);
    }

    public static boolean isEnergyCompatible(BlockEntity block, net.minecraft.core.Direction side) {
        return CapabilityAdapter.supportsEnergy(block, side);
    }
}
