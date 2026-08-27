package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ComputerStorage.MOD_ID);

    public static final RegistryObject<BlockEntityType<MotherboardControllerBlockEntity>> MOTHERBOARD_CONTROLLER =
            BLOCK_ENTITIES.register("motherboard_controller", () ->
                    BlockEntityType.Builder.of(MotherboardControllerBlockEntity::new,
                            ModBlocks.MOTHERBOARD_CONTROLLER.get()).build(null));

    private ModBlockEntities() {}
}
