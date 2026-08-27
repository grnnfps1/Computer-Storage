package com.computerstorage.common.transfer;

/** Runtime state for a programmable transfer rule. */
public final class TransferRuleRuntime {
    private final TransferSchedule schedule;
    private TransferCondition condition;
    private long lastRun = Long.MIN_VALUE;

    public TransferRuleRuntime(TransferSchedule schedule, TransferCondition condition) {
        this.schedule = schedule;
        this.condition = condition;
    }

    public TransferSchedule schedule() { return schedule; }
    public TransferCondition condition() { return condition; }
    public void condition(TransferCondition condition) { this.condition = condition; }
    public long lastRun() { return lastRun; }

    public boolean due(long gameTime, boolean redstonePowered) {
        if (!schedule.shouldRun(gameTime)) return false;
        if (condition == TransferCondition.REDSTONE_HIGH && !redstonePowered) return false;
        if (condition == TransferCondition.REDSTONE_LOW && redstonePowered) return false;
        return gameTime != lastRun;
    }

    public boolean markRun(long gameTime) {
        if (gameTime == lastRun) return false;
        lastRun = gameTime;
        return true;
    }
}
