package com.computerstorage.common.transfer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/** Persistent program collection used by a Computer Storage controller. */
public final class TransferProgramStore {
    private final List<TransferProgram> programs = new ArrayList<>();

    public List<TransferProgram> programs() { return List.copyOf(programs); }

    public boolean add(TransferProgram program) {
        if (programs.stream().anyMatch(p -> p.id().equals(program.id()))) return false;
        programs.add(program);
        return true;
    }

    public boolean remove(String id) {
        return programs.removeIf(p -> p.id().equals(id));
    }

    public void clear() { programs.clear(); }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (TransferProgram p : programs) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", p.id());
            entry.putString("source", p.sourceId());
            entry.putString("destination", p.destinationId());
            entry.putString("filterMode", p.filter().mode().name());
            entry.putString("filterItem", p.filter().itemId());
            entry.putInt("priority", p.priority());
            entry.putInt("maxItems", p.maxItemsPerOperation());
            entry.putInt("minSource", p.minSourceAmount());
            entry.putInt("maxDestination", p.maxDestinationAmount());
            entry.putString("condition", p.condition().name());
            entry.putLong("interval", p.schedule().intervalTicks());
            entry.putLong("offset", p.schedule().offsetTicks());
            list.add(entry);
        }
        tag.put("programs", list);
    }

    public void load(CompoundTag tag) {
        programs.clear();
        ListTag list = tag.getList("programs", Tag.TAG_COMPOUND);
        for (Tag raw : list) {
            CompoundTag e = (CompoundTag) raw;
            try {
                add(new TransferProgram(
                        e.getString("id"), e.getString("source"), e.getString("destination"),
                        new TransferFilter(TransferFilter.Mode.valueOf(e.getString("filterMode")), e.getString("filterItem")),
                        e.getInt("priority"), e.getInt("maxItems"), e.getInt("minSource"), e.getInt("maxDestination"),
                        TransferCondition.valueOf(e.getString("condition")),
                        new TransferSchedule(e.getLong("interval"), e.getLong("offset"))));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed persisted programs rather than preventing the computer from loading.
            }
        }
    }
}
