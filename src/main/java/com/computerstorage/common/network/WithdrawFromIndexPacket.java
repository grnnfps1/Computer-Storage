package com.computerstorage.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * Client to server: take one item, or a whole stack, out of the storage index.
 *
 * <p>The client sends what it wants, never how much is available; the server re-reads the index
 * and decides, so a tampered client cannot conjure items.
 */
public record WithdrawFromIndexPacket(ItemStack template, boolean wholeStack) implements NetworkMessage {
    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(template.copyWithCount(1));
        buf.writeBoolean(wholeStack);
    }

    public static WithdrawFromIndexPacket decode(FriendlyByteBuf buf) {
        return new WithdrawFromIndexPacket(buf.readItem(), buf.readBoolean());
    }
}
