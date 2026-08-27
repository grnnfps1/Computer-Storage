package com.computerstorage;

import com.computerstorage.common.computer.Computer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(ComputerStorage.MOD_ID)
public final class ComputerStorage {
    public static final String MOD_ID = "computerstorage";
    public static final String MOD_NAME = "Computer Storage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ComputerStorage() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ComputerStorageConfig.SPEC);
        LOGGER.info("{} foundation bootstrapped", MOD_NAME);
    }

    public static Computer createComputer() {
        return new Computer();
    }
}
