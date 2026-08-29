package com.computerstorage.common.network;

import com.computerstorage.common.menu.MonitorSnapshot;
import com.computerstorage.test.BootstrapMinecraft;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The wire between the running machine and the monitor screen. The state has to survive the trip:
 * the client cannot work it out for itself, because its copy of the controller never ticks.
 */
@BootstrapMinecraft
class SyncStorageIndexPacketTest {

    private static SyncStorageIndexPacket roundTrip(SyncStorageIndexPacket original) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);
        return SyncStorageIndexPacket.decode(buf);
    }

    @Test
    void theRunningFlagSurvivesTheWire() {
        SyncStorageIndexPacket decoded = roundTrip(new SyncStorageIndexPacket(true, List.of()));

        assertTrue(decoded.running(), "a running machine must arrive as running");
        assertFalse(roundTrip(new SyncStorageIndexPacket(false, List.of())).running());
    }

    @Test
    void countsBeyondAStackSurviveTheWire() {
        SyncStorageIndexPacket decoded = roundTrip(new SyncStorageIndexPacket(true,
                List.of(new ItemStack(Items.IRON_INGOT, 73), new ItemStack(Items.REDSTONE, 90_112))));

        assertEquals(2, decoded.listing().size());
        assertEquals(Items.IRON_INGOT, decoded.listing().get(0).getItem());
        assertEquals(73, decoded.listing().get(0).getCount());
        assertEquals(90_112, decoded.listing().get(1).getCount(),
                "an index count far past a stack must not be clamped on the way over");
    }

    @Test
    void aFloodOfEntriesIsBounded() {
        List<ItemStack> huge = new ArrayList<>();
        for (int i = 0; i < SyncStorageIndexPacket.MAX_ENTRIES + 500; i++) {
            huge.add(new ItemStack(Items.IRON_INGOT, 1));
        }

        SyncStorageIndexPacket decoded = roundTrip(new SyncStorageIndexPacket(true, huge));

        assertEquals(SyncStorageIndexPacket.MAX_ENTRIES, decoded.listing().size());
    }

    @Test
    void whatArrivesIsWhatTheMonitorThenReports() {
        SyncStorageIndexPacket decoded = roundTrip(new SyncStorageIndexPacket(true,
                List.of(new ItemStack(Items.IRON_INGOT, 73))));

        MonitorSnapshot snapshot = new MonitorSnapshot();
        snapshot.accept(decoded.running(), decoded.listing());

        assertTrue(snapshot.running(), "the monitor must read the machine state that was sent");
        assertEquals(1, snapshot.listing().size());
        assertEquals(73, snapshot.listing().get(0).getCount());
    }

    @Test
    void anOfflineMachineArrivesOfflineAndShowsNothing() {
        SyncStorageIndexPacket decoded = roundTrip(new SyncStorageIndexPacket(false, List.of()));

        MonitorSnapshot snapshot = new MonitorSnapshot();
        snapshot.accept(decoded.running(), decoded.listing());

        assertFalse(snapshot.running());
        assertTrue(snapshot.listing().isEmpty());
    }
}
