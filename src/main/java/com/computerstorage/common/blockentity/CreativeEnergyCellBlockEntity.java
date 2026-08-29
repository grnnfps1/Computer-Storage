package com.computerstorage.common.blockentity;

import com.computerstorage.common.energy.CreativeEnergyPush;
import com.computerstorage.common.energy.CreativeEnergySource;
import com.computerstorage.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * DEBUG/CREATIVE ONLY. Pushes an endless supply of FE into every adjacent block that accepts it,
 * and exposes the same endless source through the ENERGY capability on all six sides so machines
 * that pull rather than receive are fed too.
 */
public final class CreativeEnergyCellBlockEntity extends BlockEntity {
    /** Per neighbour, per tick. Far above any machine's draw, so nothing is ever starved. */
    public static final int TRANSFER_PER_TICK = 10_000;

    private final CreativeEnergySource source = new CreativeEnergySource();
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> source);

    public CreativeEnergyCellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ENERGY_CELL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  CreativeEnergyCellBlockEntity cell) {
        cell.pushToNeighbours(level, pos);
    }

    private void pushToNeighbours(Level level, BlockPos pos) {
        List<IEnergyStorage> neighbours = new ArrayList<>(Direction.values().length);
        for (Direction direction : Direction.values()) {
            BlockEntity neighbour = level.getBlockEntity(pos.relative(direction));
            if (neighbour == null) continue;
            neighbour.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
                    .ifPresent(neighbours::add);
        }
        CreativeEnergyPush.push(source, neighbours, TRANSFER_PER_TICK);
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }
}
