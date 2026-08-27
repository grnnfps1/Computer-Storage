package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.block.MotherboardControllerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ComputerStorage.MOD_ID);

    public static final RegistryObject<Block> MOTHERBOARD_CONTROLLER = BLOCKS.register(
            "motherboard_controller", () -> new MotherboardControllerBlock(Block.Properties.of().strength(3.5F).requiresCorrectToolForDrops())
    );

    private ModBlocks() {}
}
