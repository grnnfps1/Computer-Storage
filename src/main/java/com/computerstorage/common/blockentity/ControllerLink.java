package com.computerstorage.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The single place that answers "which computer is this peripheral attached to".
 *
 * <p>Today the answer is adjacency: the six blocks touching the peripheral. When cable networks
 * arrive, only {@link #candidatePositions} and {@link #find} change — the monitor, its menu, its
 * screen and the packets all go through here and know nothing about how the link is made.
 */
public final class ControllerLink {
    private ControllerLink() {}

    /** The positions searched for a controller, nearest first. */
    public static List<BlockPos> candidatePositions(BlockPos origin) {
        List<BlockPos> candidates = new ArrayList<>(Direction.values().length);
        for (Direction direction : Direction.values()) candidates.add(origin.relative(direction));
        return candidates;
    }

    /** Resolves the attached controller, or null when the peripheral stands alone. */
    @Nullable
    public static MotherboardControllerBlockEntity find(@Nullable BlockGetter level, @Nullable BlockPos origin) {
        if (level == null || origin == null) return null;
        return find(origin, level::getBlockEntity);
    }

    /**
     * Walks the candidate positions in order and returns the first thing of the wanted type.
     *
     * <p>Generic over the type so the search itself can be exercised with a stand-in: block
     * entities cannot be allocated without a registry.
     */
    @Nullable
    public static <T> T firstMatching(BlockPos origin, Function<BlockPos, ?> at, Class<T> type) {
        if (origin == null || at == null || type == null) return null;
        for (BlockPos candidate : candidatePositions(origin)) {
            Object found = at.apply(candidate);
            if (type.isInstance(found)) return type.cast(found);
        }
        return null;
    }

    /** Lookup with the world access injected. */
    @Nullable
    public static MotherboardControllerBlockEntity find(BlockPos origin,
                                                        Function<BlockPos, BlockEntity> blockEntityAt) {
        return firstMatching(origin, blockEntityAt, MotherboardControllerBlockEntity.class);
    }
}
