package com.computerstorage.common.transfer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.List;

public final class TransferProgramStore {
    private final List<TransferProgram> programs = new ArrayList<>();
    public List<TransferProgram> programs() { return List.copyOf(programs); }
    public boolean add(TransferProgram program) { if (program == null || programs.stream().anyMatch(p -> p.id().equals(program.id()))) return false; programs.add(program); return true; }
    public boolean remove(String id) { return programs.removeIf(p -> p.id().equals(id)); }
    public boolean setEnabled(String id, boolean enabled) {
        for (int i=0;i<programs.size();i++) { TransferProgram p=programs.get(i); if(p.id().equals(id)){ programs.set(i,new TransferProgram(p.id(),p.sourceId(),p.destinationId(),p.filter(),p.priority(),p.maxItemsPerOperation(),p.minSourceAmount(),p.maxDestinationAmount(),p.condition(),p.schedule(),enabled)); return true; } }
        return false;
    }
    public void clear() { programs.clear(); }
    public void save(CompoundTag tag) {
        ListTag list=new ListTag(); for(TransferProgram p:programs){ CompoundTag e=new CompoundTag(); e.putString("id",p.id()); e.putString("source",p.sourceId()); e.putString("destination",p.destinationId()); e.putString("filterMode",p.filter().mode().name()); e.putString("filterItem",p.filter().itemId()); e.putInt("priority",p.priority()); e.putInt("maxItems",p.maxItemsPerOperation()); e.putInt("minSource",p.minSourceAmount()); e.putInt("maxDestination",p.maxDestinationAmount()); e.putString("condition",p.condition().name()); e.putLong("interval",p.schedule().intervalTicks()); e.putLong("offset",p.schedule().offsetTicks()); e.putBoolean("enabled",p.enabled()); list.add(e); } tag.put("programs",list);
    }
    public void load(CompoundTag tag) {
        programs.clear(); ListTag list=tag.getList("programs",Tag.TAG_COMPOUND); for(Tag raw:list){ CompoundTag e=(CompoundTag)raw; try{ add(new TransferProgram(e.getString("id"),e.getString("source"),e.getString("destination"),new TransferFilter(TransferFilter.Mode.valueOf(e.getString("filterMode")),e.getString("filterItem")),e.getInt("priority"),e.getInt("maxItems"),e.getInt("minSource"),e.getInt("maxDestination"),TransferCondition.valueOf(e.getString("condition")),new TransferSchedule(e.getLong("interval"),e.getLong("offset")),e.getBoolean("enabled"))); } catch(IllegalArgumentException ignored){} }
    }
}
