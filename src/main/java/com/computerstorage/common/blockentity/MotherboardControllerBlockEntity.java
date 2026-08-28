package com.computerstorage.common.blockentity;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.hardware.HardwareManager;
import com.computerstorage.common.hardware.HardwareSlot;
import com.computerstorage.common.hardware.HardwareSlotRules;
import com.computerstorage.common.hardware.HardwareType;
import com.computerstorage.common.hardware.ItemHardwareComponent;
import com.computerstorage.common.menu.MotherboardMenu;
import com.computerstorage.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import com.computerstorage.common.storage.StorageIntake;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
    private ControllerRuntime runtime;
    private final PersistedEnergyStorage energy = new PersistedEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER, ENERGY_TRANSFER);
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energy);
    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> new InvWrapper(this));

    public MotherboardControllerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.MOTHERBOARD_CONTROLLER.get(), pos, state); }
    public static void serverTick(Level level, BlockPos pos, BlockState state, MotherboardControllerBlockEntity be) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel)
            be.computer.services().get(com.computerstorage.common.computer.services.LogisticsManager.class).bindLevel(serverLevel);
        be.runtime().tick(be.energy);
        be.setChanged();
    }

    private ControllerRuntime runtime() {
        if (runtime == null) runtime = new ControllerRuntime(this, computer, HARDWARE_SLOTS,
                MotherboardControllerBlockEntity::componentFor, MotherboardControllerBlockEntity::isBootDisk);
        return runtime;
    }


    static boolean isBootDisk(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof com.computerstorage.common.item.BootDiskItem;
    }


    /** Hands back every item the machine holds: sockets, buffer, and the logical index. */
    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this);
        for (ItemStack stored : computer.storage().storage().drainAll()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stored);
        }
    }

    /**
     * TEMPORARY DEBUG HOOK. The mod has no generator yet, so there is no in-game way to put FE
     * into the machine. Remove this together with the redstone-block interaction in
     * MotherboardControllerBlock once a real energy source exists.
     */
    public int debugCharge(int amount) { return energy.receiveEnergy(amount, false); }

    public long storageUsed() { return computer.storage().storage().used(); }
    public long storageCapacity() { return computer.storage().storage().capacity(); }
    public Computer computer() { return computer; }
    public int energyStored() { return energy.getEnergyStored(); }
    public int energyCapacity() { return energy.getMaxEnergyStored(); }
    private HardwareManager hardware() { return computer.services().get(HardwareManager.class); }
    private void syncHardwareFromInventory() {
        HardwareManager manager = hardware();
        for (HardwareSlot slot : HardwareSlot.values()) {
            int index = slot.ordinal(); ItemStack stack = items.get(index);
            if (stack.isEmpty()) { if (manager.has(slot)) manager.remove(slot); continue; }
            if (manager.has(slot)) continue;
            HardwareType actual = hardwareType(stack);
            if (actual == slot.type()) manager.install(slot, new ItemHardwareComponent(stack));
        }
    }
    private static String itemPath(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().key().location().getPath();
    }

    /** The typed component the hardware item carries, so its stats survive installation. */
    private static com.computerstorage.common.hardware.IHardwareComponent componentFor(ItemStack stack) {
        if (stack.getItem() instanceof com.computerstorage.common.item.HardwareItem hardware) return hardware.createComponent();
        return new ItemHardwareComponent(stack);
    }

    private static HardwareType hardwareType(ItemStack stack) {
        return HardwareSlotRules.typeOf(itemPath(stack));
    }
    public void writeScreenOpeningData(FriendlyByteBuf buffer) { buffer.writeBlockPos(worldPosition); }
    public boolean isUsableByPlayer(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX()+.5D, worldPosition.getY()+.5D, worldPosition.getZ()+.5D) <= 64.0D; }
    @Override public Component getDisplayName() { return Component.translatable("block.computerstorage.motherboard_controller"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) { return new MotherboardMenu(id, inv, this); }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return isUsableByPlayer(player); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return HardwareSlotRules.accepts(slot, itemPath(stack)); }
    @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putInt("Energy", energy.getEnergyStored()); net.minecraft.nbt.CompoundTag c = new net.minecraft.nbt.CompoundTag(); computer.save(c); tag.put("Computer", c); }
    @Override public void load(net.minecraft.nbt.CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, items); energy.setEnergy(tag.getInt("Energy")); if (tag.contains("Computer")) computer.load(tag.getCompound("Computer")); }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) { if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast(); if (capability == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast(); return super.getCapability(capability, side); }
    @Override public void invalidateCaps() { super.invalidateCaps(); energyCapability.invalidate(); itemCapability.invalidate(); }
    private static final class PersistedEnergyStorage extends EnergyStorage { private PersistedEnergyStorage(int c,int r,int x){super(c,r,x);} private void setEnergy(int value){energy=Math.max(0,Math.min(capacity,value));} }
}
