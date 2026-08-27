package com.computerstorage.client.gui;

import com.computerstorage.common.blockentity.MotherboardControllerBlockEntity;
import com.computerstorage.common.menu.MotherboardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MotherboardScreen extends AbstractContainerScreen<MotherboardMenu> {
    public MotherboardScreen(MotherboardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 190;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        graphics.fill(left, top, left + imageWidth, top + imageHeight, 0xFF10151B);
        graphics.fill(left + 5, top + 5, left + imageWidth - 5, top + 28, 0xFF202A33);
        graphics.fill(left + 5, top + 31, left + imageWidth - 5, top + 101, 0xFF161D24);
        graphics.fill(left + 5, top + 104, left + imageWidth - 5, top + imageHeight - 5, 0xFF161D24);

        graphics.drawString(font, Component.literal("COMPUTER STORAGE"), left + 10, top + 11, 0xFFFFFFFF, false);
        graphics.drawString(font, Component.literal("HARDWARE"), left + 8, top + 34, 0xFF8FA9BF, false);
        graphics.drawString(font, Component.literal("INTERNAL STORAGE"), left + 8, top + 104, 0xFF8FA9BF, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, Component.literal("FE: " + energy()), 112, 11, 0xFFB9E6FF, false);
    }

    private int energy() {
        return ((MotherboardControllerBlockEntity) menu.getController()).energyStored();
    }
}
