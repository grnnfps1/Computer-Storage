package com.computerstorage.client;

import com.computerstorage.ComputerStorage;
import com.computerstorage.client.gui.MotherboardScreen;
import com.computerstorage.common.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;

@Mod.EventBusSubscriber(modid = ComputerStorage.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MOTHERBOARD.get(), MotherboardScreen::new);
    }
}
