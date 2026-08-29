package com.computerstorage.client.network;

import com.computerstorage.common.menu.StorageMonitorMenu;
import com.computerstorage.common.network.SyncStorageIndexPacket;
import net.minecraft.client.Minecraft;

/** Hands a synced index listing to the monitor screen the player has open. */
public final class ClientStorageSync {
    private ClientStorageSync() {}

    public static void apply(SyncStorageIndexPacket message) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (player.containerMenu instanceof StorageMonitorMenu menu)
            menu.acceptState(message.running(), message.listing());
    }
}
