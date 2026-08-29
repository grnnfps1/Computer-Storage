package com.computerstorage.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Server to client: the storage index listing behind an open monitor.
 *
 * <p>Sent whole rather than as a diff; the listing is small enough in practice and a full snapshot
 * cannot drift out of sync with the server.
 */
public record SyncStorageIndexPacket(boolean running, List<ItemStack> listing) implements NetworkMessage {
    /** Guards against a hostile or broken server flooding the client. */
    public static final int MAX_ENTRIES = 4096;

    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(running);
        int count = Math.min(listing.size(), MAX_ENTRIES);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) {
            ItemStack stack = listing.get(i);
            buf.writeItem(stack.copyWithCount(1));
            buf.writeVarInt(Math.max(0, stack.getCount()));
        }
    }

    public static SyncStorageIndexPacket decode(FriendlyByteBuf buf) {
        boolean running = buf.readBoolean();
        int count = Math.min(buf.readVarInt(), MAX_ENTRIES);
        List<ItemStack> listing = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ItemStack prototype = buf.readItem();
            int stored = buf.readVarInt();
            ItemStack shown = prototype.copy();
            shown.setCount(stored);
            listing.add(shown);
        }
        return new SyncStorageIndexPacket(running, listing);
    }
}
