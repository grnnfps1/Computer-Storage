package com.computerstorage.common.blockentity;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.hardware.HardwareComponentType;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.menu.MotherboardMenu;
import com.computerstorage.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public final class MotherboardControllerBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int HARDWARE_SLOTS = 13;
    public static final int INTERNAL_SLOTS = 16;
    public static final int SLOT_COUNT = HARDWARE_SLOTS + INTERNAL_SLOTS;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int ENERGY_TRANSFER = 2_000;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final Computer computer = new Computer();
    private final PersistedEnergyStorage energy = new PersistedEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER, ENERGY_TRANSFER);
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energy);
    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> new InvWrapper(this));

    public MotherboardControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOTHERBOARD_CONTROLLER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MotherboardControllerBlockEntity be) {
        be.computer.tick();
        be.syncHardwareFromInventory();
    }

    public Computer computer() { return computer; }
    public int energyStored() { return energy.getEnergyStored(); }
    public int energyCapacity() { return energy.getMaxEnergyStored(); }

    private void syncHardwareFromInventory() {
        for (HardwareSlot slot : HardwareSlot.values()) {
            int index = slot.ordinal();
            ItemStack stack = items.get(index);
            if (stack.isEmpty()) {
                if (computer.hardware().has(slot)) computer.hardware().remove(slot);
                continue;
            }
            HardwareComponentType expected = slot.type();
            if (computer.hardware().has(slot)) continue;
            HardwareComponentType actual = hardwareType(stack);
            if (actual == expected) computer.hardware().install(slot, new com.computerstorage.common.hardware.ItemHardwareComponent(stack));
        }
    }

    private static HardwareComponentType hardwareType(ItemStack stack) {
        String id = stack.getItem().builtInRegistryHolder().key().location().getPath();
        if (id.contains("cpu")) return HardwareComponentType.CPU;
        if (id.contains("ram")) return HardwareComponentType.RAM;
        if (id.contains("gpu")) return HardwareComponentType.GPU;
        if (id.contains("nic")) return HardwareComponentType.NIC;
        if (id.contains("ssd")) return HardwareComponentType.SSD;
        if (id.contains("power")) return HardwareComponentType.POWER;
        if (id.contains("cooler")) return HardwareComponentType.COOLER;
        return null;
    }

    public void writeScreenOpeningData(FriendlyByteBuf buffer) { buffer.writeBlockPos(worldPosition); }
    public boolean isUsableByPlayer(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX()+0.5D, worldPosition.getY()+0.5D, worldPosition.getZ()+0.5D) <= 64.0D; }
    @Override public Component getDisplayName() { return Component.translatable("block.computerstorage.motherboard_controller"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) { return new MotherboardMenu(id, inv, this); }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return ItemStackHelper.removeItem(items, slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStackHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return isUsableByPlayer(player); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot >= HARDWARE_SLOTS || hardwareType(stack) == HardwareSlot.values()[slot].type(); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ItemStackHelper.saveAllItems(tag, items); tag.putInt("Energy", energy.getEnergyStored()); CompoundTag c = new CompoundTag(); computer.save(c); tag.put("Computer", c); }
    @Override public void load(CompoundTag tag) { super.load(tag); ItemStackHelper.loadAllItems(tag, items); energy.setEnergy(tag.getInt("Energy")); if (tag.contains("Computer")) computer.load(tag.getCompound("Computer")); }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) { if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast(); if (capability == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast(); return super.getCapability(capability, side); }
    @Override public void invalidateCaps() { super.invalidateCaps(); energyCapability.invalidate(); itemCapability.invalidate(); }

    private static final class PersistedEnergyStorage extends EnergyStorage {
        private PersistedEnergyStorage(int capacity, int maxReceive, int maxExtract) { super(capacity, maxReceive, maxExtract); }
        private void setEnergy(int value) { energy = Math.max(0, Math.min(capacity, value)); }
    }
}
