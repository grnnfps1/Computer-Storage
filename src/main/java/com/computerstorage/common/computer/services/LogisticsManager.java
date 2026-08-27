package com.computerstorage.common.computer.services;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.IComputerService;
import com.computerstorage.common.transfer.ControllerLogisticsRuntime;
import com.computerstorage.common.transfer.TransferProgramRepository;
import com.computerstorage.common.transfer.WorldTransferEndpointRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/** Owns the single authoritative programmable item-routing runtime for a Computer. */
public final class LogisticsManager implements IComputerService {
    private final WorldTransferEndpointRegistry endpoints = new WorldTransferEndpointRegistry();
    private ControllerLogisticsRuntime runtime;
    private ServerLevel level;
    private int lastMoved;

    @Override
    public void tick(Computer computer) {
        if (level == null) {
            lastMoved = 0;
            return;
        }
        if (runtime == null) {
            runtime = new ControllerLogisticsRuntime(
                    new TransferProgramRepository(computer.transferPrograms()),
                    id -> endpoints.resolve(id, level));
        }
        lastMoved = runtime.tick(level.getGameTime(), false);
    }

    public void bindLevel(ServerLevel level) {
        this.level = level;
        this.runtime = null;
    }

    public boolean registerEndpoint(String id, BlockPos pos, Direction side) {
        return level != null && endpoints.register(id, level, pos, side);
    }

    public boolean unregisterEndpoint(String id) { return endpoints.unregister(id); }
    public WorldTransferEndpointRegistry endpoints() { return endpoints; }
    public int lastMoved() { return lastMoved; }
}
