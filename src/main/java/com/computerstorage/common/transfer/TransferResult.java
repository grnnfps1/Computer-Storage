package com.computerstorage.common.transfer;

public record TransferResult(String programId, int moved, boolean attempted) {
    public static TransferResult skipped(String id) { return new TransferResult(id, 0, false); }
    public static TransferResult attempted(String id, int moved) { return new TransferResult(id, moved, true); }
}
