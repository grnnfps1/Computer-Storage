package com.computerstorage.common.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Client to server: put the stack the player is holding on the cursor into the storage index.
 *
 * <p>Carries intent only, never an item. The server reads the carried stack from the open menu and
 * decides what may move, so a tampered client cannot deposit something it does not hold.
 *
 * @param wholeStack true for a left-click, which offers everything on the cursor; false for a
 *                   right-click, which offers a single item
 */
public record DepositToIndexPacket(boolean wholeStack) implements NetworkMessage {
    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(wholeStack);
    }

    public static DepositToIndexPacket decode(FriendlyByteBuf buf) {
        return new DepositToIndexPacket(buf.readBoolean());
    }
}
