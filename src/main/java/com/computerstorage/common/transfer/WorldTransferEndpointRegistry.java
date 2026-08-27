package com.computerstorage.common.transfer;

import com.computerstorage.common.compat.CompatibilityService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/** Resolves persistent endpoint IDs to live compatible item handlers in a server level. */
public final class WorldTransferEndpointRegistry {
    private final Map<String, Entry> entries = new LinkedHashMap<>();

    public boolean register(String id, ServerLevel level, BlockPos pos, @Nullable Direction side) {
        if (!validId(id) || level == null || pos == null) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || !CompatibilityService.isItemCompatible(be, side)) return false;
        entries.put(id, new Entry(level.dimension().location().toString(), pos.immutable(), side));
        return true;
    }

    public boolean unregister(String id) { return entries.remove(id) != null; }

    @Nullable
    public IItemHandler resolve(String id, ServerLevel level) {
        Entry entry = entries.get(id);
        if (entry == null || level == null || !entry.dimension().equals(level.dimension().location().toString())) return null;
        BlockEntity be = level.getBlockEntity(entry.pos());
        return CompatibilityService.itemHandler(be, entry.side());
    }

    public boolean contains(String id) { return entries.containsKey(id); }
    public int size() { return entries.size(); }
    public Map<String, Entry> snapshot() { return Map.copyOf(entries); }

    private static boolean validId(String id) {
        return id != null && !id.isBlank() && id.length() <= 64
                && id.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.');
    }

    public record Entry(String dimension, BlockPos pos, @Nullable Direction side) {}
}
