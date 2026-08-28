package com.computerstorage.client.gui;

import com.computerstorage.common.menu.MotherboardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class MotherboardScreen extends AbstractContainerScreen<MotherboardMenu> {
    /** Panel bounds, kept in sync with the slot rows positioned in MotherboardMenu. */
    static final int HEADER_TOP = 4, HEADER_BOTTOM = 36;
    static final int HARDWARE_TOP = 38, HARDWARE_BOTTOM = 89;
    static final int INTERNAL_TOP = 91, INTERNAL_BOTTOM = 142;
    static final int PLAYER_TOP = 144, PLAYER_BOTTOM = 234;

    private static final int BACKDROP = 0xFF10151B;
    private static final int HEADER = 0xFF202A33;
    private static final int SECTION = 0xFF161D24;
    private static final int SLOT_LIGHT = 0xFF44525F;
    private static final int SLOT_DARK = 0xFF0C1116;
    private static final int SLOT_WELL = 0xFF232E38;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int TEXT_MUTED = 0xFF8FA9BF;
    private static final int TEXT_ENERGY = 0xFFB9E6FF;

    public MotherboardScreen(MotherboardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos, top = topPos, right = leftPos + imageWidth;
        graphics.fill(left, top, right, top + imageHeight, BACKDROP);
        graphics.fill(left + 5, top + HEADER_TOP, right - 5, top + HEADER_BOTTOM, HEADER);
        graphics.fill(left + 5, top + HARDWARE_TOP, right - 5, top + HARDWARE_BOTTOM, SECTION);
        graphics.fill(left + 5, top + INTERNAL_TOP, right - 5, top + INTERNAL_BOTTOM, SECTION);
        graphics.fill(left + 5, top + PLAYER_TOP, right - 5, top + PLAYER_BOTTOM, SECTION);
        for (Slot slot : menu.slots) drawSlotWell(graphics, left + slot.x, top + slot.y);
    }

    /** How much of the SSD-backed index is in use. */
    private Component storageLabel() {
        return Component.literal(menu.storageUsed() + " / " + menu.storageCapacity());
    }

    /**
     * Draws the sunken 18x18 cell behind a slot: one dark pixel along the top and left, one light
     * pixel along the bottom and right, and a 16x16 well under the item itself.
     */
    private void drawSlotWell(GuiGraphics graphics, int x, int y) {
        int x0 = x - 1, y0 = y - 1, x1 = x + 17, y1 = y + 17;
        graphics.fill(x0, y0, x1, y1, SLOT_LIGHT);
        graphics.fill(x0, y0, x1 - 1, y1 - 1, SLOT_DARK);
        graphics.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, SLOT_WELL);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 9, TEXT, false);
        Component energy = Component.literal("FE: " + menu.energy());
        graphics.drawString(font, energy, imageWidth - 8 - font.width(energy), 22, TEXT_ENERGY, false);
        graphics.drawString(font, storageLabel(), 10, 22, TEXT_MUTED, false);
        graphics.drawString(font, Component.literal("HARDWARE"), 10, 41, TEXT_MUTED, false);
        graphics.drawString(font, Component.literal("INTERNAL STORAGE"), 10, 94, TEXT_MUTED, false);
        graphics.drawString(font, playerInventoryTitle, 10, 147, TEXT_MUTED, false);
    }
}
