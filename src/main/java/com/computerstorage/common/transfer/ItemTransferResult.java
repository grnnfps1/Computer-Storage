package com.computerstorage.common.transfer;

/** Result of a single transfer attempt. */
public record ItemTransferResult(int moved, boolean sourceAccepted, boolean destinationAccepted) {
    public static ItemTransferResult none() {
        return new ItemTransferResult(0, false, false);
    }
}
