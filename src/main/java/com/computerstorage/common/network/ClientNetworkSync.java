package com.computerstorage.common.network;

import com.computerstorage.client.network.ClientNetworkState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/** Applies server snapshots only on the physical client. */
public final class ClientNetworkSync {
    private ClientNetworkSync() {}

    public static void apply(SyncNetworkStatePacket packet) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworkState.apply(packet));
    }
}
