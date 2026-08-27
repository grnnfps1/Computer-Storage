package com.computerstorage.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;

/** Renders the navigation rail and lightweight OS panels without owning game state. */
public final class OsPanelRenderer {
    private OsPanelRenderer() {}

    public static void drawNavigation(GuiGraphics g, Font font, int x, int y, OsTab selected) {
        int rowHeight = 18;
        for (OsTab tab : OsTab.values()) {
            boolean active = tab == selected;
            int top = y + tab.ordinal() * rowHeight;
            g.fill(x, top, x + 82, top + rowHeight - 2, active ? 0xFF263746 : 0xFF171D22);
            g.drawString(font, tab.label(), x + 7, top + 4, active ? 0xFFFFFFFF : 0xFF8FA0AD, false);
        }
    }

    public static void drawEmptyPanel(GuiGraphics g, Font font, int x, int y, int width, int height,
                                      OsTab tab) {
        g.fill(x, y, x + width, y + height, 0xFF10161B);
        g.drawString(font, tab.label(), x + 10, y + 8, 0xFFFFFFFF, false);
        g.drawString(font, "NO DATA", x + 10, y + 28, 0xFF6F7C86, false);
    }
}
