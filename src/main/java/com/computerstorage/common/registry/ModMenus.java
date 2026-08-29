package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.menu.MotherboardMenu;
import com.computerstorage.common.menu.StorageMonitorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ComputerStorage.MOD_ID);

    public static final RegistryObject<MenuType<MotherboardMenu>> MOTHERBOARD =
            MENUS.register("motherboard", () -> IForgeMenuType.create(MotherboardMenu::new));

    public static final RegistryObject<MenuType<StorageMonitorMenu>> STORAGE_MONITOR =
            MENUS.register("storage_monitor", () -> IForgeMenuType.create(StorageMonitorMenu::new));

    private ModMenus() {}
}
