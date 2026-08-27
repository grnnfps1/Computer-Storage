package com.computerstorage.common.transfer;

import net.minecraftforge.items.IItemHandler;

import java.util.Objects;

/** A named endpoint in the Computer Storage logistics network. */
public final class TransferNode {
    private final String id;
    private final IItemHandler inventory;

    public TransferNode(String id, IItemHandler inventory) {
        this.id = Objects.requireNonNull(id);
        this.inventory = Objects.requireNonNull(inventory);
    }

    public String id() { return id; }
    public IItemHandler inventory() { return inventory; }
}
