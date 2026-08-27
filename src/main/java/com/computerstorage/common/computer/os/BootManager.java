package com.computerstorage.common.computer.os;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.computer.bios.BiosResult;

/** Controls the deterministic BIOS -> bootloader -> OS transition. */
public final class BootManager {
    public boolean runPostAndBoot(Computer computer) {
        if (computer.biosPost() != BiosResult.OK) return false;
        computer.enterBootloader();
        if (!computer.operatingSystem().isInstalled()) return false;
        computer.enterRunning();
        return true;
    }
}
