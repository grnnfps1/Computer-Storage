package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ComputerStorage.MOD_ID);

    public static final RegistryObject<Item> MOTHERBOARD_CONTROLLER = ITEMS.register("motherboard_controller",
            () -> new BlockItem(ModBlocks.MOTHERBOARD_CONTROLLER.get(), new Item.Properties()));

    private ModItems() {}
}
