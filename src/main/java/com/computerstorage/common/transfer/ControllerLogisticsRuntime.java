package com.computerstorage.common.transfer;

import net.minecraftforge.items.IItemHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ControllerLogisticsRuntime {
    private final TransferProgramRepository repository;
    private final TransferTickRunner runner;
    private final Map<String, TransferRuleRuntime> runtimes = new HashMap<>();
    public ControllerLogisticsRuntime(TransferProgramRepository repository, Function<String, IItemHandler> handlerResolver) { this.repository = repository; this.runner = new TransferTickRunner(repository, handlerResolver); }
    public int tick(long gameTime, boolean redstonePowered) {
        int moved = 0;
        for (TransferProgram program : repository.orderedPrograms()) {
            if (!program.enabled()) continue;
            TransferResult result = runner.run(program, gameTime, redstonePowered, runtimes);
            if (result.attempted()) moved += result.moved();
        }
        return moved;
    }
    public void clearRuntimeState() { runtimes.clear(); }
    public void clearRuntime(String id) { if (id != null) runtimes.remove(id); }
}
