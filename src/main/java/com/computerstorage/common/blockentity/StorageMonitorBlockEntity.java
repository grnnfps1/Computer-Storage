package com.computerstorage.common.blockentity;

import com.computerstorage.common.menu.StorageMonitorMenu;
import com.computerstorage.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Stateless terminal: the index it shows lives in the attached controller. */
public final class StorageMonitorBlockEntity extends BlockEntity implements MenuProvider {
    public StorageMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STORAGE_MONITOR.get(), pos, state);
    }

    public void writeScreenOpeningData(FriendlyByteBuf buffer) { buffer.writeBlockPos(worldPosition); }

    @Nullable
    public MotherboardControllerBlockEntity controller() { return ControllerLink.find(level, worldPosition); }

    public boolean isUsableByPlayer(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + .5D, worldPosition.getY() + .5D,
                worldPosition.getZ() + .5D) <= 64.0D;
    }

    @Override public Component getDisplayName() {
        return Component.translatable("block.computerstorage.storage_monitor");
    }

    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new StorageMonitorMenu(id, inv, this);
    }
}
