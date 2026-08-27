package com.computerstorage.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** Reusable OS-style status renderer for the Controller screen. */
public final class ComputerStorageHud {
    private ComputerStorageHud() {}

    public static void drawSystemPanel(GuiGraphics graphics, net.minecraft.client.gui.Font font,
                                       int x, int y, int width, int energy, int maxEnergy) {
        graphics.fill(x, y, x + width, y + 46, 0xFF111820);
        graphics.drawString(font, Component.literal("SYSTEM"), x + 7, y + 6, 0xFF8FA9BF, false);
        graphics.drawString(font, Component.literal("POWER"), x + 7, y + 19, 0xFFFFFFFF, false);
        graphics.drawString(font, Component.literal(energy + " / " + maxEnergy + " FE"), x + 62, y + 19, 0xFFB9E6FF, false);
        int barWidth = Math.max(0, width - 14);
        int filled = maxEnergy <= 0 ? 0 : Math.min(barWidth, (int) ((long) energy * barWidth / maxEnergy));
        graphics.fill(x + 7, y + 34, x + 7 + barWidth, y + 39, 0xFF29343D);
        graphics.fill(x + 7, y + 34, x + 7 + filled, y + 39, 0xFF4AA3DF);
    }
}
