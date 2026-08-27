package com.computerstorage.common.network;

import net.minecraft.network.FriendlyByteBuf;

/** Wire-format helpers shared by future server/client packets. */
public final class NetworkPackets {
    private NetworkPackets() {}

    public static void writeString(FriendlyByteBuf buf, String value, int maxLength) {
        if (value == null || value.length() > maxLength) throw new IllegalArgumentException("Invalid network string");
        buf.writeUtf(value, maxLength);
    }

    public static String readString(FriendlyByteBuf buf, int maxLength) {
        return buf.readUtf(maxLength);
    }
}
