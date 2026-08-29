package com.computerstorage.common.block;

import com.computerstorage.common.blockentity.CreativeEnergyCellBlockEntity;
import com.computerstorage.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * DEBUG/CREATIVE ONLY. An endless FE source for exercising machines while the mod has no real
 * generator. Not survival content: it has no recipe and is meant to be removed, or gated, before
 * anything ships.
 */
public class CreativeEnergyCellBlock extends BaseEntityBlock {
    public CreativeEnergyCellBlock(Properties properties) { super(properties); }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergyCellBlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.CREATIVE_ENERGY_CELL.get(),
                        CreativeEnergyCellBlockEntity::serverTick);
    }
}
