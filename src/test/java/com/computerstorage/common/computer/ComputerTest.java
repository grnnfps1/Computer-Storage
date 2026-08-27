package com.computerstorage.common.computer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComputerTest {
    @Test
    void computerStartsPoweredOff() {
        Computer computer = new Computer();
        assertEquals(ComputerState.OFF, computer.getState());
        assertEquals(0, computer.getUptime());
    }

    @Test
    void bootChangesState() {
        Computer computer = new Computer();
        computer.boot();
        assertEquals(ComputerState.POST, computer.getState());
    }
}
