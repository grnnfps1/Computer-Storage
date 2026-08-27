package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

/** Server-authoritative endpoint binding with basic ownership/distance validation. */
public final class EndpointBindingService {
    public static final double MAX_BIND_DISTANCE_SQR = 64.0D;

    private EndpointBindingService() {}

    public static Result bind(ServerPlayer player, WorldTransferEndpointRegistry registry,
                              String id, BlockPos pos, Direction side) {
        if (player == null || registry == null || pos == null) return Result.INVALID;
        if (!player.serverLevel().hasChunkAt(pos)) return Result.CHUNK_UNLOADED;
        if (player.distanceToSqr(pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D) > MAX_BIND_DISTANCE_SQR)
            return Result.TOO_FAR;
        if (player.serverLevel().getBlockEntity(pos) == null) return Result.NO_BLOCK_ENTITY;
        if (!registry.register(id, player.serverLevel(), pos, side)) return Result.INVALID;
        return Result.OK;
    }

    public enum Result { OK, INVALID, CHUNK_UNLOADED, TOO_FAR, NO_BLOCK_ENTITY }
}
