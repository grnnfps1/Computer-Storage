package com.computerstorage.common.transfer;

import net.minecraftforge.items.IItemHandler;

import java.util.Map;
import java.util.function.Function;

/** Bridges persisted programs to world inventories. The world layer supplies handlers by node id. */
public final class TransferTickRunner {
    private final TransferProgramRepository repository;
    private final Function<String, IItemHandler> handlerResolver;
    private final TransferProgramExecutor executor = new TransferProgramExecutor();

    public TransferTickRunner(TransferProgramRepository repository,
                              Function<String, IItemHandler> handlerResolver) {
        this.repository = repository;
        this.handlerResolver = handlerResolver;
    }

    public TransferResult run(TransferProgram program, long gameTime, boolean redstonePowered,
                              Map<String, TransferRuleRuntime> runtimes) {
        TransferRuleRuntime runtime = runtimes.computeIfAbsent(program.id(),
                ignored -> new TransferRuleRuntime(program.schedule(), program.condition()));
        if (!runtime.due(gameTime, redstonePowered)) return TransferResult.skipped(program.id());

        IItemHandler source = handlerResolver.apply(program.sourceId());
        IItemHandler destination = handlerResolver.apply(program.destinationId());
        int moved = executor.execute(program, source, destination);
        runtime.markRun(gameTime);
        return TransferResult.attempted(program.id(), moved);
    }
}
