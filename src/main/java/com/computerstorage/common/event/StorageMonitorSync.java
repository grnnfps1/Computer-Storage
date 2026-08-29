package com.computerstorage.common.event;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.menu.StorageMonitorMenu;
import com.computerstorage.common.network.NetworkChannel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Pushes the storage listing to players watching a monitor.
 *
 * <p>Throttled rather than sent every tick: the listing is a full snapshot, and a busy index would
 * otherwise send one packet per player per tick for no visible gain.
 */
@Mod.EventBusSubscriber(modid = ComputerStorage.MOD_ID)
public final class StorageMonitorSync {
    private static final int INTERVAL_TICKS = 10;

    private StorageMonitorSync() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.getServer().getTickCount() % INTERVAL_TICKS != 0) return;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof StorageMonitorMenu menu) NetworkChannel.sendIndex(player, menu);
        }
    }
}
