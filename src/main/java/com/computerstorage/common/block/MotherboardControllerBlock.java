package com.computerstorage.common.block;

import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MotherboardControllerBlock extends BaseEntityBlock {
    /** TEMPORARY DEBUG constant, see use(). */
    private static final int DEBUG_CHARGE_FE = 20_000;

    public MotherboardControllerBlock(Properties properties) { super(properties); }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MotherboardControllerBlockEntity controller)) return InteractionResult.CONSUME;

        // TEMPORARY DEBUG: no generator exists yet, so a redstone block in hand charges the
        // machine to let it be exercised in a dev world. Remove with debugCharge().
        if (player.getItemInHand(hand).is(net.minecraft.world.item.Items.REDSTONE_BLOCK)) {
            int accepted = controller.debugCharge(DEBUG_CHARGE_FE);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "[debug] +" + accepted + " FE"), true);
            return InteractionResult.CONSUME;
        }

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
            NetworkHooks.openScreen(serverPlayer, controller, controller::writeScreenOpeningData);
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MotherboardControllerBlockEntity controller) controller.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MotherboardControllerBlockEntity(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, com.computerstorage.common.registry.ModBlockEntities.MOTHERBOARD_CONTROLLER.get(), MotherboardControllerBlockEntity::serverTick);
    }
}
