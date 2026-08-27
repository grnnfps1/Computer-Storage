package com.computerstorage.common.network;

import com.computerstorage.common.transfer.TransferCondition;
import com.computerstorage.common.transfer.TransferFilter;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTransferProgramPacketTest {
    @Test
    void roundTripsProgramRequest() {
        var original = new CreateTransferProgramPacket(
                "iron_route", "input", "output", TransferFilter.Mode.WHITELIST,
                "minecraft:iron_ingot", 100, 64, 16, 256,
                TransferCondition.ALWAYS, 20, 0, true);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);
        assertEquals(original, CreateTransferProgramPacket.decode(buf));
    }

    @Test
    void convertsToDomainProgram() {
        var packet = new CreateTransferProgramPacket(
                "iron_route", "input", "output", TransferFilter.Mode.WHITELIST,
                "minecraft:iron_ingot", 100, 64, 16, 256,
                TransferCondition.ALWAYS, 20, 0, true);
        assertEquals("iron_route", packet.toProgram().id());
        assertEquals("input", packet.toProgram().sourceId());
        assertTrue(packet.toProgram().filter().accepts(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT)));
    }
}
