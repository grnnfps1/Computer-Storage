package com.computerstorage.client.gui;

import com.computerstorage.client.network.ClientNetworkState;
import com.computerstorage.common.network.SyncNetworkStatePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;

/** Client-side rendering of the authoritative network snapshot. */
public final class NetworkPanelView {
    private NetworkPanelView() {}

    public static void draw(GuiGraphics graphics, Font font, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF10161B);
        graphics.drawString(font, "NETWORK", x + 8, y + 7, 0xFFFFFFFF, false);
        var endpoints = ClientNetworkState.endpoints();
        graphics.drawString(font, "ENDPOINTS: " + endpoints.size(), x + 8, y + 20, 0xFF8FA9BF, false);
        int row = 34;
        for (SyncNetworkStatePacket.EndpointData endpoint : endpoints) {
            if (row > height - 12) break;
            graphics.drawString(font, "● " + endpoint.id(), x + 8, y + row, 0xFFB9E6FF, false);
            String side = endpoint.side() == null ? "ANY" : endpoint.side().getSerializedName().toUpperCase();
            graphics.drawString(font, side + "  " + endpoint.pos().toShortString(), x + 18, y + row + 11, 0xFF6F7C86, false);
            row += 28;
        }
    }
}
