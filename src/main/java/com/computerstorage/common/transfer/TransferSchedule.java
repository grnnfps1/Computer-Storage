package com.computerstorage.common.transfer;

public record TransferSchedule(long intervalTicks, long offsetTicks) {
    public TransferSchedule {
        if (intervalTicks < 1) throw new IllegalArgumentException("intervalTicks must be positive");
        if (offsetTicks < 0) throw new IllegalArgumentException("offsetTicks cannot be negative");
    }

    public boolean shouldRun(long gameTime) {
        return gameTime >= offsetTicks && (gameTime - offsetTicks) % intervalTicks == 0;
    }

    public static TransferSchedule everyTick() { return new TransferSchedule(1, 0); }
}
