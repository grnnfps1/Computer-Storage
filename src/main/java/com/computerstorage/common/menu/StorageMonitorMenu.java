package com.computerstorage.common.menu;

import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import com.computerstorage.common.blockentity.StorageMonitorBlockEntity;
import com.computerstorage.common.computer.ComputerState;
import com.computerstorage.common.network.SyncStorageIndexPacket;
import com.computerstorage.common.registry.ModMenus;
import com.computerstorage.common.storage.DepositService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Carries only the player's own inventory: the storage index holds far more distinct items than
 * slots could represent, so the listing is synced separately and drawn as a virtual grid.
 */
public final class StorageMonitorMenu extends AbstractContainerMenu {
    /**
     * Where the player's own inventory sits inside the terminal window. The screen draws its
     * panels around these, so the two must move together.
     */
    public static final int INVENTORY_LEFT = 182;
    public static final int INVENTORY_TOP = 162;
    public static final int HOTBAR_TOP = 220;

    private final StorageMonitorBlockEntity monitor;
    /** Client-side mirror of what the server last published: machine state and index listing. */
    private final MonitorSnapshot snapshot = new MonitorSnapshot();

    public StorageMonitorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory,
                (StorageMonitorBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public StorageMonitorMenu(int containerId, Inventory playerInventory, StorageMonitorBlockEntity monitor) {
        super(ModMenus.STORAGE_MONITOR.get(), containerId);
        this.monitor = monitor;
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col + row * 9 + 9,
                    INVENTORY_LEFT + col * 18, INVENTORY_TOP + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInventory, col, INVENTORY_LEFT + col * 18, HOTBAR_TOP));
    }

    public StorageMonitorBlockEntity monitor() { return monitor; }

    @Nullable public MotherboardControllerBlockEntity controller() {
        return monitor == null ? null : monitor.controller();
    }

    /**
     * Whether the machine is actually running.
     *
     * <p>Answered from opposite ends depending on the side. On the server the controller is the
     * live machine and is asked directly. On the client that same lookup finds a block entity whose
     * Computer is never ticked and never synced, so it would always answer OFF; there the answer is
     * the one the server sent.
     */
    public boolean computerRunning() {
        if (clientSide()) return snapshot.running();
        MotherboardControllerBlockEntity controller = controller();
        return controller != null && controller.computer().getState() == ComputerState.RUNNING;
    }

    private boolean clientSide() {
        return monitor != null && monitor.getLevel() != null && monitor.getLevel().isClientSide;
    }

    public List<ItemStack> listing() { return snapshot.listing(); }

    /** Takes a synced state and listing straight from {@link SyncStorageIndexPacket}. */
    public void acceptState(boolean running, List<ItemStack> synced) { snapshot.accept(running, synced); }

    public MonitorSnapshot snapshot() { return snapshot; }

    /**
     * Shift-clicking a stack in the player's inventory sends it to the storage index.
     *
     * <p>There is nowhere else for it to go: every slot in this menu belongs to the player, so the
     * usual container-to-container shuffle has no meaning here. Whatever the index cannot hold is
     * put straight back in the slot it came from, so a full index costs the player nothing.
     */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (player.level().isClientSide) return ItemStack.EMPTY;
        MotherboardControllerBlockEntity controller = controller();
        if (controller == null) return ItemStack.EMPTY;

        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack leftover = DepositService.deposit(controller.computer().storage().storage(),
                slot.getItem(), computerRunning());
        if (leftover == slot.getItem()) return ItemStack.EMPTY;

        slot.set(leftover);
        slot.setChanged();
        controller.setChanged();
        // Always empty: the move is one-shot, and a non-empty return would make the vanilla click
        // loop call this again for the same slot.
        return ItemStack.EMPTY;
    }

    @Override public boolean stillValid(Player player) {
        return monitor != null && monitor.isUsableByPlayer(player);
    }
}
