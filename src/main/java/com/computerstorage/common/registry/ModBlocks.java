package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.block.CreativeEnergyCellBlock;
import com.computerstorage.common.block.MotherboardControllerBlock;
import com.computerstorage.common.block.StorageMonitorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ComputerStorage.MOD_ID);

    public static final RegistryObject<Block> MOTHERBOARD_CONTROLLER = BLOCKS.register(
            "motherboard_controller", () -> new MotherboardControllerBlock(Block.Properties.of().strength(3.5F).requiresCorrectToolForDrops())
    );

    public static final RegistryObject<Block> STORAGE_MONITOR = BLOCKS.register(
            "storage_monitor", () -> new StorageMonitorBlock(Block.Properties.of().strength(2.5F).requiresCorrectToolForDrops())
    );

    /** DEBUG/CREATIVE ONLY: endless FE source for testing machines. See CreativeEnergyCellBlock. */
    public static final RegistryObject<Block> CREATIVE_ENERGY_CELL = BLOCKS.register(
            "creative_energy_cell", () -> new CreativeEnergyCellBlock(Block.Properties.of().strength(0.5F))
    );

    private ModBlocks() {}
}
