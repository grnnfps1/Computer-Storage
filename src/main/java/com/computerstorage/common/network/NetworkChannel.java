package com.computerstorage.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.computerstorage.ComputerStorage.MOD_ID;

public final class NetworkChannel {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int nextId = 0;

    private NetworkChannel() {}

    public static void register() {
        CHANNEL.registerMessage(nextId++, SyncNetworkStatePacket.class,
                SyncNetworkStatePacket::encode,
                SyncNetworkStatePacket::decode,
                (message, contextSupplier) -> contextSupplier.get().enqueueWork(() -> {
                    // Client application is intentionally registered in the next client-sync step.
                }));
    }
}
