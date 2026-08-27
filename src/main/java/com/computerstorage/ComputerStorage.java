package com.computerstorage;

import com.computerstorage.common.computer.Computer;
import com.computerstorage.common.network.NetworkChannel;
import com.computerstorage.common.registry.ModBlockEntities;
import com.computerstorage.common.registry.ModBlocks;
import com.computerstorage.common.registry.ModItems;
import com.computerstorage.common.registry.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(ComputerStorage.MOD_ID)
public final class ComputerStorage {
    public static final String MOD_ID = "computerstorage";
    public static final String MOD_NAME = "Computer Storage";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ComputerStorage() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ComputerStorageConfig.SPEC);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) ->
                event.enqueueWork(NetworkChannel::register));
        LOGGER.info("{} foundation bootstrapped", MOD_NAME);
    }

    public static Computer createComputer() {
        return new Computer();
    }
}
