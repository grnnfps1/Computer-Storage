package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record TransferEndpoint(String id, BlockPos pos, Direction side) {
    public TransferEndpoint {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank");
        if (pos == null) throw new IllegalArgumentException("pos cannot be null");
    }
}
