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
        CHANNEL.registerMessage(nextId++, CreateTransferProgramPacket.class,
                CreateTransferProgramPacket::encode, CreateTransferProgramPacket::decode,
                NetworkChannel::handleCreateTransferProgram);
        CHANNEL.registerMessage(nextId++, SyncStorageIndexPacket.class,
                SyncStorageIndexPacket::encode, SyncStorageIndexPacket::decode,
                (message, contextSupplier) -> {
                    var context = contextSupplier.get();
                    context.enqueueWork(() -> com.computerstorage.client.network.ClientStorageSync.apply(message));
                    context.setPacketHandled(true);
                });
        CHANNEL.registerMessage(nextId++, WithdrawFromIndexPacket.class,
                WithdrawFromIndexPacket::encode, WithdrawFromIndexPacket::decode,
                NetworkChannel::handleWithdraw);
        CHANNEL.registerMessage(nextId++, DepositToIndexPacket.class,
                DepositToIndexPacket::encode, DepositToIndexPacket::decode,
                NetworkChannel::handleDeposit);
    }

    /** Sends the listing behind an open monitor to one player. */
    public static void sendIndex(ServerPlayer player, com.computerstorage.common.menu.StorageMonitorMenu menu) {
        var controller = menu.controller();
        boolean running = menu.computerRunning();
        var listing = running && controller != null
                ? controller.computer().storage().storage().snapshot()
                : java.util.List.<net.minecraft.world.item.ItemStack>of();
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncStorageIndexPacket(running, listing));
    }

    private static void handleDeposit(DepositToIndexPacket message, java.util.function.Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null || !(player.containerMenu instanceof com.computerstorage.common.menu.StorageMonitorMenu menu)) return;
            if (!menu.stillValid(player)) return;
            var controller = menu.controller();
            if (controller == null) return;

            // The carried stack is read from the menu, never from the packet: the client says only
            // what it wants done. The machine state is re-checked here for the same reason.
            net.minecraft.world.item.ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) return;

            var index = controller.computer().storage().storage();
            net.minecraft.world.item.ItemStack leftover = message.wholeStack()
                    ? com.computerstorage.common.storage.DepositService.deposit(index, carried, menu.computerRunning())
                    : com.computerstorage.common.storage.DepositService.depositAmount(index, carried, 1, menu.computerRunning());
            if (leftover == carried) return;

            menu.setCarried(leftover);
            controller.setChanged();
            sendIndex(player, menu);
        });
        context.setPacketHandled(true);
    }

    private static void handleWithdraw(WithdrawFromIndexPacket message, java.util.function.Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null || !(player.containerMenu instanceof com.computerstorage.common.menu.StorageMonitorMenu menu)) return;
            if (!menu.stillValid(player)) return;
            // Re-validated server side: the monitor may have been unlinked or the machine stopped
            // between the click and the packet arriving.
            if (!menu.computerRunning()) return;
            var controller = menu.controller();
            if (controller == null) return;

            var index = controller.computer().storage().storage();
            var template = message.template();
            int amount = com.computerstorage.common.storage.IndexQuery.withdrawAmount(
                    template, index.count(template), message.wholeStack());
            com.computerstorage.common.storage.WithdrawalService.withdraw(index, template, amount,
                    leftover -> player.getInventory().add(leftover) ? net.minecraft.world.item.ItemStack.EMPTY : leftover);
            controller.setChanged();
            sendIndex(player, menu);
        });
        context.setPacketHandled(true);
    }

    private static void handleBindEndpoint(BindEndpointPacket message, java.util.function.Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null || !(player.containerMenu instanceof com.computerstorage.common.menu.MotherboardMenu menu)) return;
            var controller = menu.getController();
            if (controller == null || !controller.isUsableByPlayer(player)) return;
            var logistics = controller.computer().services().get(com.computerstorage.common.computer.services.LogisticsManager.class);
            logistics.bindLevel(player.serverLevel());
            if (message.validateAndBind(player, logistics.endpoints()) == com.computerstorage.common.transfer.EndpointBindingService.Result.OK) {
                CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                        SyncNetworkStatePacket.from(logistics.endpoints()));
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleCreateTransferProgram(CreateTransferProgramPacket message, java.util.function.Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player == null || !(player.containerMenu instanceof com.computerstorage.common.menu.MotherboardMenu menu)) return;
            var controller = menu.getController();
            if (controller == null || !controller.isUsableByPlayer(player)) return;
            var logistics = controller.computer().services().get(com.computerstorage.common.computer.services.LogisticsManager.class);
            logistics.bindLevel(player.serverLevel());
            logistics.addProgram(controller.computer(), message.toProgram());
        });
        context.setPacketHandled(true);
    }
}
