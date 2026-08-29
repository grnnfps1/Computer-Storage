package com.computerstorage.common.menu;

import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The client's mirror of the machine state. These lock in the thing that was broken: the monitor
 * must answer from what the server published, and must not show an index for a stopped machine.
 */
@BootstrapMinecraft
class MonitorSnapshotTest {

    private static List<ItemStack> listing() {
        return List.of(new ItemStack(Items.IRON_INGOT, 73), new ItemStack(Items.REDSTONE, 512));
    }

    @Test
    void aRunningMachineKeepsBothTheStateAndTheListing() {
        MonitorSnapshot snapshot = new MonitorSnapshot();

        snapshot.accept(true, listing());

        assertTrue(snapshot.running(), "the synced state is what the monitor must report");
        assertEquals(2, snapshot.listing().size());
        assertEquals(73, snapshot.listing().get(0).getCount(), "counts must survive the sync");
    }

    @Test
    void aStoppedMachinePublishesNoListingEvenIfEntriesArrived() {
        MonitorSnapshot snapshot = new MonitorSnapshot();

        snapshot.accept(false, listing());

        assertFalse(snapshot.running());
        assertTrue(snapshot.listing().isEmpty(),
                "a stopped machine must not leave a clickable index on screen");
        assertEquals(2, snapshot.rawListing().size(), "though what arrived is still remembered");
    }

    @Test
    void aMachineThatStopsWhileTheScreenIsOpenGoesDark() {
        MonitorSnapshot snapshot = new MonitorSnapshot();
        snapshot.accept(true, listing());
        assertTrue(snapshot.running());

        snapshot.accept(false, List.of());

        assertFalse(snapshot.running(), "the next sync must be able to switch it off");
        assertTrue(snapshot.listing().isEmpty());
    }

    @Test
    void aMachineThatStartsWhileTheScreenIsOpenComesUp() {
        MonitorSnapshot snapshot = new MonitorSnapshot();
        snapshot.accept(false, List.of());

        snapshot.accept(true, listing());

        assertTrue(snapshot.running(), "energy arriving must show up without reopening the screen");
        assertEquals(2, snapshot.listing().size());
    }

    @Test
    void clearingForgetsTheMachine() {
        MonitorSnapshot snapshot = new MonitorSnapshot();
        snapshot.accept(true, listing());

        snapshot.clear();

        assertFalse(snapshot.running());
        assertTrue(snapshot.listing().isEmpty());
    }

    @Test
    void aNullListingIsTreatedAsEmpty() {
        MonitorSnapshot snapshot = new MonitorSnapshot();

        snapshot.accept(true, null);

        assertTrue(snapshot.running());
        assertTrue(snapshot.listing().isEmpty());
    }

    @Test
    void theSnapshotDoesNotAliasTheCallersList() {
        MonitorSnapshot snapshot = new MonitorSnapshot();
        List<ItemStack> source = new ArrayList<>(listing());

        snapshot.accept(true, source);
        source.clear();

        assertEquals(2, snapshot.listing().size(), "the snapshot must own its copy");
        assertThrows(UnsupportedOperationException.class, () -> snapshot.listing().clear(),
                "and hand out a listing nobody can mutate underneath it");
    }
}
