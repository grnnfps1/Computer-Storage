package com.computerstorage.common.computer;

public interface IComputerService {
    void tick(Computer computer);
    default void serverTick(Computer computer) { tick(computer); }
    default void clientTick(Computer computer) { }
}
