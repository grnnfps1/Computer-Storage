package com.computerstorage.common.storage;

import com.computerstorage.test.BootstrapMinecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The storage entry point: items placed in the buffer leave it and land in the index, bounded by
 * the capacity the installed SSDs provide. Nothing called {@link VirtualStorage#insert} from a
 * gameplay path before this.
 */
@BootstrapMinecraft
class StorageIntakeTest {

    private static final int SIZE = 16;

    private static SimpleBuffer bufferWith(int slot, ItemStack stack) {
        SimpleBuffer buffer = new SimpleBuffer();
        buffer.setItem(slot, stack);
        return buffer;
    }

    @Test
    void itemEntersTheIndexAndLeavesTheBuffer() {
        SimpleBuffer buffer = bufferWith(0, new ItemStack(Items.IRON_INGOT, 32));
        VirtualStorage index = new VirtualStorage(1_024);

        int moved = StorageIntake.drain(buffer, 0, SIZE, index);

        assertEquals(32, moved);
        assertEquals(32, index.count(new ItemStack(Items.IRON_INGOT)));
        assertTrue(buffer.getItem(0).isEmpty(), "the stack must leave the buffer slot");
        assertEquals(32, index.used());
    }

    @Test
    void ssdCapacityIsRespectedAndTheRemainderStaysInTheBuffer() {
        SimpleBuffer buffer = bufferWith(0, new ItemStack(Items.DIAMOND, 64));
        VirtualStorage index = new VirtualStorage(10);

        int moved = StorageIntake.drain(buffer, 0, SIZE, index);

        assertEquals(10, moved, "only what fits may move");
        assertEquals(10, index.count(new ItemStack(Items.DIAMOND)));
        assertEquals(54, buffer.getItem(0).getCount(), "the remainder must survive in the buffer");
        assertEquals(0, index.available());
    }

    @Test
    void aFullIndexMovesNothingAndDestroysNothing() {
        SimpleBuffer buffer = bufferWith(0, new ItemStack(Items.DIAMOND, 64));
        VirtualStorage index = new VirtualStorage(0);

        assertEquals(0, StorageIntake.drain(buffer, 0, SIZE, index));
        assertEquals(64, buffer.getItem(0).getCount());
        assertTrue(index.isEmpty());
    }

    @Test
    void differentCapacitiesStoreDifferentAmounts() {
        SimpleBuffer small = bufferWith(0, new ItemStack(Items.IRON_INGOT, 64));
        SimpleBuffer large = bufferWith(0, new ItemStack(Items.IRON_INGOT, 64));

        int intoSmall = StorageIntake.drain(small, 0, SIZE, new VirtualStorage(8));
        int intoLarge = StorageIntake.drain(large, 0, SIZE, new VirtualStorage(64));

        assertEquals(8, intoSmall);
        assertEquals(64, intoLarge);
        assertTrue(intoLarge > intoSmall, "a bigger SSD must hold more");
    }

    @Test
    void theFilterKeepsTheBootDiskOutOfTheIndex() {
        SimpleBuffer buffer = new SimpleBuffer();
        buffer.setItem(0, new ItemStack(Items.MUSIC_DISC_CAT, 1));
        buffer.setItem(1, new ItemStack(Items.IRON_INGOT, 5));
        VirtualStorage index = new VirtualStorage(1_024);

        int moved = StorageIntake.drain(buffer, 0, SIZE, index,
                stack -> stack.getItem() != Items.MUSIC_DISC_CAT);

        assertEquals(5, moved);
        assertFalse(buffer.getItem(0).isEmpty(), "the protected stack must stay put");
        assertTrue(buffer.getItem(1).isEmpty());
    }

    @Test
    void drainsEverySlotInTheRange() {
        SimpleBuffer buffer = new SimpleBuffer();
        buffer.setItem(0, new ItemStack(Items.IRON_INGOT, 4));
        buffer.setItem(7, new ItemStack(Items.GOLD_INGOT, 6));
        buffer.setItem(15, new ItemStack(Items.DIAMOND, 2));
        VirtualStorage index = new VirtualStorage(1_024);

        assertEquals(12, StorageIntake.drain(buffer, 0, SIZE, index));
        assertEquals(4, index.count(new ItemStack(Items.IRON_INGOT)));
        assertEquals(6, index.count(new ItemStack(Items.GOLD_INGOT)));
        assertEquals(2, index.count(new ItemStack(Items.DIAMOND)));
    }

    @Test
    void slotsOutsideTheRangeAreLeftAlone() {
        SimpleBuffer buffer = bufferWith(12, new ItemStack(Items.IRON_INGOT, 9));
        VirtualStorage index = new VirtualStorage(1_024);

        assertEquals(0, StorageIntake.drain(buffer, 0, 10, index));
        assertEquals(9, buffer.getItem(12).getCount());
    }

    /** Plain backing store so the tests exercise the intake rather than a block entity. */
    private static final class SimpleBuffer implements Container {
        private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        private int changes;

        @Override public int getContainerSize() { return SIZE; }
        @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); }
        @Override public void setChanged() { changes++; }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { items.clear(); }
    }
}
