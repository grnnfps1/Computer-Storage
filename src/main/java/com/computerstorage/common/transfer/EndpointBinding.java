package com.computerstorage.common.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** Immutable, validated binding request for a world inventory endpoint. */
public record EndpointBinding(String id, BlockPos pos, @Nullable Direction side) {
    public static final int MAX_ID_LENGTH = 64;

    public EndpointBinding {
        if (!validId(id)) throw new IllegalArgumentException("Invalid endpoint id");
        if (pos == null) throw new IllegalArgumentException("Endpoint position cannot be null");
        pos = pos.immutable();
    }

    private static boolean validId(String id) {
        return id != null && !id.isBlank() && id.length() <= MAX_ID_LENGTH
                && id.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.');
    }
}
