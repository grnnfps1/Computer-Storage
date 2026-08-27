package com.computerstorage.common.network;

import com.computerstorage.common.transfer.TransferCondition;
import com.computerstorage.common.transfer.TransferFilter;
import com.computerstorage.common.transfer.TransferProgram;
import com.computerstorage.common.transfer.TransferSchedule;
import net.minecraft.network.FriendlyByteBuf;

/** Client request to create one logistics program. The server remains authoritative. */
public record CreateTransferProgramPacket(
        String id,
        String sourceId,
        String destinationId,
        TransferFilter.Mode filterMode,
        String filterItemId,
        int priority,
        int maxItemsPerOperation,
        int minSourceAmount,
        int maxDestinationAmount,
        TransferCondition condition,
        long intervalTicks,
        long offsetTicks,
        boolean enabled) implements NetworkMessage {
    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_ITEM_ID_LENGTH = 128;
    @Override public int protocolVersion() { return NetworkConstants.PROTOCOL_VERSION; }
    public void encode(FriendlyByteBuf buf) {
        NetworkPackets.writeString(buf, id, MAX_ID_LENGTH);
        NetworkPackets.writeString(buf, sourceId, MAX_ID_LENGTH);
        NetworkPackets.writeString(buf, destinationId, MAX_ID_LENGTH);
        buf.writeEnum(filterMode);
        NetworkPackets.writeString(buf, filterItemId == null ? "" : filterItemId, MAX_ITEM_ID_LENGTH);
        buf.writeInt(priority); buf.writeInt(maxItemsPerOperation); buf.writeInt(minSourceAmount); buf.writeInt(maxDestinationAmount);
        buf.writeEnum(condition); buf.writeLong(intervalTicks); buf.writeLong(offsetTicks); buf.writeBoolean(enabled);
    }
    public static CreateTransferProgramPacket decode(FriendlyByteBuf buf) {
        return new CreateTransferProgramPacket(NetworkPackets.readString(buf, MAX_ID_LENGTH), NetworkPackets.readString(buf, MAX_ID_LENGTH),
                NetworkPackets.readString(buf, MAX_ID_LENGTH), buf.readEnum(TransferFilter.Mode.class), NetworkPackets.readString(buf, MAX_ITEM_ID_LENGTH),
                buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readEnum(TransferCondition.class), buf.readLong(), buf.readLong(), buf.readBoolean());
    }
    public TransferProgram toProgram() {
        return new TransferProgram(id, sourceId, destinationId, new TransferFilter(filterMode, filterItemId), priority,
                maxItemsPerOperation, minSourceAmount, maxDestinationAmount, condition, new TransferSchedule(intervalTicks, offsetTicks), enabled);
    }
}
