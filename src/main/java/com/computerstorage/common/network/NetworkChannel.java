package com.computerstorage.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.computerstorage.ComputerStorage.MOD_ID;

public final class NetworkChannel {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private static int nextId = 0;
    private NetworkChannel() {}

    public static void register() {
        CHANNEL.registerMessage(nextId++, SyncNetworkStatePacket.class,
                SyncNetworkStatePacket::encode, SyncNetworkStatePacket::decode,
                (message, contextSupplier) -> {
                    var context = contextSupplier.get();
                    context.enqueueWork(() -> ClientNetworkSync.apply(message));
                    context.setPacketHandled(true);
                });
        CHANNEL.registerMessage(nextId++, BindEndpointPacket.class,
                BindEndpointPacket::encode, BindEndpointPacket::decode,
                NetworkChannel::handleBindEndpoint);
    }

    private static void handleBindEndpoint(BindEndpointPacket message, java.util.function.Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null || player.containerMenu == null) return;
            var controller = player.serverLevel().getBlockEntity(message.pos());
            if (controller instanceof com.computerstorage.common.blockentity.MotherboardControllerBlockEntity be) {
                var logistics = be.computer().services().get(com.computerstorage.common.computer.services.LogisticsManager.class);
                logistics.bindLevel(player.serverLevel());
                if (message.validateAndBind(player, logistics.endpoints()) == com.computerstorage.common.transfer.EndpointBindingService.Result.OK) {
                    CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                            SyncNetworkStatePacket.from(logistics.endpoints()));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
