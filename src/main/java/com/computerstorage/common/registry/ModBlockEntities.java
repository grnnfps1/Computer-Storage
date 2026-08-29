package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.blockentity.CreativeEnergyCellBlockEntity;
import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import com.computerstorage.common.blockentity.StorageMonitorBlockEntity;
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

    public static final RegistryObject<BlockEntityType<StorageMonitorBlockEntity>> STORAGE_MONITOR =
            BLOCK_ENTITIES.register("storage_monitor", () ->
                    BlockEntityType.Builder.of(StorageMonitorBlockEntity::new,
                            ModBlocks.STORAGE_MONITOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CreativeEnergyCellBlockEntity>> CREATIVE_ENERGY_CELL =
            BLOCK_ENTITIES.register("creative_energy_cell", () ->
                    BlockEntityType.Builder.of(CreativeEnergyCellBlockEntity::new,
                            ModBlocks.CREATIVE_ENERGY_CELL.get()).build(null));

    private ModBlockEntities() {}
}
