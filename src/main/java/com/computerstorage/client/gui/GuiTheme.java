package com.computerstorage.client.gui;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Shared look for the mod's screens, drawn entirely with fills and gradients — the mod ships no
 * GUI textures, so every panel, bevel and slot well is geometry.
 */
public final class GuiTheme {
    // Surfaces
    public static final int BACKDROP_TOP = 0xFF0E141B;
    public static final int BACKDROP_BOTTOM = 0xFF090D12;
    public static final int PANEL = 0xFF141C25;
    public static final int HEADER_TOP = 0xFF1C2836;
    public static final int HEADER_BOTTOM = 0xFF141E29;

    // Edges
    public static final int EDGE_LIGHT = 0xFF3C4E60;
    public static final int EDGE_DARK = 0xFF060A0E;

    // Accent and status
    public static final int ACCENT = 0xFF4FC3D9;
    public static final int ACCENT_DIM = 0xFF2A6C7A;
    public static final int OK = 0xFF6FBF8A;
    public static final int WARN = 0xFFE0A24A;

    // Slots
    public static final int WELL = 0xFF1B2530;
    public static final int WELL_SOCKET = 0xFF1B2A33;
    public static final int SLOT_HI = 0xFF44525F;
    public static final int SLOT_LO = 0xFF080C10;
    public static final int HOVER = 0x40FFFFFF;
    public static final int ROW_TINT = 0x06FFFFFF;

    // Text
    public static final int TEXT = 0xFFE8F1F5;
    public static final int TEXT_MUTED = 0xFF7E93A5;
    public static final int TEXT_DIM = 0xFF56697A;

    private GuiTheme() {}

    /** The window itself: gradient ground inside a two-tone bevel. */
    public static void window(GuiGraphics graphics, int left, int top, int width, int height) {
        int right = left + width, bottom = top + height;
        graphics.fillGradient(left, top, right, bottom, BACKDROP_TOP, BACKDROP_BOTTOM);
        graphics.renderOutline(left, top, width, height, EDGE_LIGHT);
        graphics.renderOutline(left + 1, top + 1, width - 2, height - 2, EDGE_DARK);
    }

    /** Header band, closed off by an accent hairline so it reads as a separate register. */
    public static void header(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fillGradient(left, top, right, bottom, HEADER_TOP, HEADER_BOTTOM);
        graphics.hLine(left, right - 1, bottom, ACCENT_DIM);
    }

    /** Section panel, bevelled inward so the slots inside it read as sitting on top. */
    public static void panel(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, bottom, PANEL);
        graphics.hLine(left, right - 1, top, EDGE_DARK);
        graphics.vLine(left, top, bottom - 1, EDGE_DARK);
        graphics.hLine(left, right - 1, bottom - 1, EDGE_LIGHT);
        graphics.vLine(right - 1, top, bottom - 1, EDGE_LIGHT);
    }

    /** A 2px accent rail beside a section label, marking where a section starts. */
    public static void labelRail(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 2, y + 8, ACCENT);
    }

    /** Small status lamp: the machine's state, readable at a glance. */
    public static void lamp(GuiGraphics graphics, int x, int y, boolean on) {
        graphics.fill(x - 1, y - 1, x + 4, y + 4, EDGE_DARK);
        graphics.fill(x, y, x + 3, y + 3, on ? OK : WARN);
    }

    /**
     * Sunken 18x18 cell: one dark pixel along the top and left, one light along the bottom and
     * right, and a well under the item. Typed sockets get a faint accent tint so they read as
     * different from the generic buffer without needing a label.
     */
    public static void slotWell(GuiGraphics graphics, int x, int y, boolean socket, boolean hovered) {
        int x0 = x - 1, y0 = y - 1, x1 = x + 17, y1 = y + 17;
        graphics.fill(x0, y0, x1, y1, SLOT_HI);
        graphics.fill(x0, y0, x1 - 1, y1 - 1, SLOT_LO);
        graphics.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, socket ? WELL_SOCKET : WELL);
        if (hovered) graphics.fill(x0 + 1, y0 + 1, x1 - 1, y1 - 1, HOVER);
    }

    /** A selectable chip: sidebar filter rows, pager arrows, anything that reacts to a click. */
    public static void chip(GuiGraphics graphics, int left, int top, int right, int bottom,
                            boolean selected, boolean hovered) {
        graphics.fill(left, top, right, bottom, selected ? ACCENT_DIM : PANEL);
        graphics.renderOutline(left, top, right - left, bottom - top, selected ? ACCENT : EDGE_DARK);
        if (hovered && !selected) graphics.fill(left + 1, top + 1, right - 1, bottom - 1, HOVER);
    }

    /** A panel for something not built yet: same geometry, drained of contrast so it reads inert. */
    public static void ghostPanel(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, bottom, PANEL);
        graphics.renderOutline(left, top, right - left, bottom - top, EDGE_DARK);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1, 0x30000000);
    }

    /** An inert 18x18 well, for placeholder grids that take no items. */
    public static void ghostWell(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, EDGE_DARK);
        graphics.fill(x, y, x + 16, y + 16, 0xFF161D26);
    }

    /** Whether the pointer is inside an arbitrary rectangle. */
    public static boolean over(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    /** Whether the pointer is over an 18x18 cell anchored at the item's top-left corner. */
    public static boolean overCell(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x - 1 && mouseX < x + 17 && mouseY >= y - 1 && mouseY < y + 17;
    }
}
