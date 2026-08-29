package com.computerstorage.client.gui;

import com.computerstorage.common.menu.MotherboardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import com.computerstorage.common.hardware.HardwareSlotRules;
import net.minecraft.world.inventory.Slot;

public final class MotherboardScreen extends AbstractContainerScreen<MotherboardMenu> {
    /** Panel bounds, kept in sync with the slot rows positioned in MotherboardMenu. */
    static final int HEADER_TOP = 4, HEADER_BOTTOM = 36;
    static final int HARDWARE_TOP = 38, HARDWARE_BOTTOM = 89;
    static final int INTERNAL_TOP = 91, INTERNAL_BOTTOM = 142;
    static final int PLAYER_TOP = 144, PLAYER_BOTTOM = 234;

    public MotherboardScreen(MotherboardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos, top = topPos, right = leftPos + imageWidth;
        GuiTheme.window(graphics, left, top, imageWidth, imageHeight);
        GuiTheme.header(graphics, left + 5, top + HEADER_TOP, right - 5, top + HEADER_BOTTOM);
        GuiTheme.panel(graphics, left + 5, top + HARDWARE_TOP, right - 5, top + HARDWARE_BOTTOM);
        GuiTheme.panel(graphics, left + 5, top + INTERNAL_TOP, right - 5, top + INTERNAL_BOTTOM);
        GuiTheme.panel(graphics, left + 5, top + PLAYER_TOP, right - 5, top + PLAYER_BOTTOM);

        for (int index = 0; index < menu.slots.size(); index++) {
            Slot slot = menu.slots.get(index);
            int x = left + slot.x, y = top + slot.y;
            GuiTheme.slotWell(graphics, x, y, index < HardwareSlotRules.HARDWARE_SLOTS,
                    GuiTheme.overCell(mouseX, mouseY, x, y));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        GuiTheme.lamp(graphics, 9, 11, menu.energy() > 0);
        graphics.drawString(font, title, 17, 9, GuiTheme.TEXT, false);

        drawReading(graphics, "FE", String.valueOf(menu.energy()), 22);
        graphics.drawString(font, Component.literal(menu.storageUsed() + " / " + menu.storageCapacity()),
                10, 22, GuiTheme.TEXT_DIM, false);

        section(graphics, "HARDWARE", 41);
        section(graphics, "INTERNAL STORAGE", 94);
        section(graphics, playerInventoryTitle.getString(), 147);
    }

    /** Section label with its accent rail and a hairline running to the panel edge. */
    private void section(GuiGraphics graphics, String label, int y) {
        GuiTheme.labelRail(graphics, 8, y);
        graphics.drawString(font, Component.literal(label), 14, y, GuiTheme.TEXT_MUTED, false);
        int textEnd = 14 + font.width(label) + 4;
        graphics.hLine(textEnd, imageWidth - 9, y + 4, GuiTheme.EDGE_LIGHT);
    }

    /** Right-aligned readout: dim label, accent value. */
    private void drawReading(GuiGraphics graphics, String label, String value, int y) {
        int valueWidth = font.width(value);
        int valueX = imageWidth - 8 - valueWidth;
        graphics.drawString(font, Component.literal(value), valueX, y, GuiTheme.ACCENT, false);
        graphics.drawString(font, Component.literal(label), valueX - 4 - font.width(label), y,
                GuiTheme.TEXT_DIM, false);
    }
}
