package com.computerstorage.common.block;

import com.computerstorage.common.blockentity.ControllerLink;
import com.computerstorage.common.blockentity.StorageMonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/** Window onto an adjacent computer's storage index. Holds nothing of its own. */
public class StorageMonitorBlock extends BaseEntityBlock {
    public StorageMonitorBlock(Properties properties) { super(properties); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof StorageMonitorBlockEntity monitor))
            return InteractionResult.CONSUME;

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            // The terminal opens whether or not a computer is attached. The screen has a real
            // offline state now, and refusing to open leaves the player holding a block that
            // looks broken with nothing explaining why.
            NetworkHooks.openScreen(serverPlayer, monitor, monitor::writeScreenOpeningData);
            // Fill the screen immediately; the periodic push would otherwise leave it blank for
            // up to half a second.
            if (serverPlayer.containerMenu instanceof com.computerstorage.common.menu.StorageMonitorMenu menu)
                com.computerstorage.common.network.NetworkChannel.sendIndex(serverPlayer, menu);
            if (ControllerLink.find(level, pos) == null) {
                player.displayClientMessage(
                        Component.translatable("message.computerstorage.no_computer"), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageMonitorBlockEntity(pos, state);
    }
}
