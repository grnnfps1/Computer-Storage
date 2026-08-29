package com.computerstorage.common.menu;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What the server last told a client about the machine behind a monitor.
 *
 * <p>The client's copy of the controller block entity is a stub: its {@code Computer} is a fresh
 * object, the block ticker is server-only, and the block entity syncs no NBT, so asking that copy
 * for the machine state always answers OFF no matter what the real machine is doing. The state has
 * to travel in the packet instead, and this is where what arrived is kept.
 *
 * <p>Split out of the menu so the rule can be tested without a Level or a registry.
 */
public final class MonitorSnapshot {
    private boolean running;
    private List<ItemStack> listing = List.of();

    /** Takes what the server sent. A null listing is treated as an empty one. */
    public void accept(boolean running, List<ItemStack> synced) {
        this.running = running;
        this.listing = synced == null || synced.isEmpty()
                ? List.of()
                : Collections.unmodifiableList(new ArrayList<>(synced));
    }

    public boolean running() { return running; }

    /**
     * The listing to show. A stopped machine publishes nothing, so the entries are withheld rather
     * than left on screen as a stale index the player could still click.
     */
    public List<ItemStack> listing() { return running ? listing : List.of(); }

    /** Everything the server sent, running or not. Useful for asserting what actually arrived. */
    public List<ItemStack> rawListing() { return listing; }

    /** Forgets the machine, so a monitor that loses its link cannot keep showing a live index. */
    public void clear() {
        running = false;
        listing = List.of();
    }
}
