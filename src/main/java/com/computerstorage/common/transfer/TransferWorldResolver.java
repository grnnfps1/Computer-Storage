package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Map;

/** Resolves named logistics nodes to live Forge item handlers in a server level. */
public final class TransferWorldResolver {
    private final ServerLevel level;
    private final Map<String, Endpoint> endpoints = new HashMap<>();

    public TransferWorldResolver(ServerLevel level) { this.level = level; }

    public void bind(String id, BlockPos pos) { bind(id, pos, null); }

    public void bind(String id, BlockPos pos, Direction side) {
        endpoints.put(id, new Endpoint(pos.immutable(), side));
    }

    public void unbind(String id) { endpoints.remove(id); }

    public IItemHandler resolve(String id) {
        Endpoint endpoint = endpoints.get(id);
        if (endpoint == null) return null;
        BlockEntity be = level.getBlockEntity(endpoint.pos());
        if (be == null) return null;
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER, endpoint.side()).resolve().orElse(null);
    }

    public record Endpoint(BlockPos pos, Direction side) {}
}
