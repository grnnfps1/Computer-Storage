package com.computerstorage.client.gui;

import com.computerstorage.common.network.CreateTransferProgramPacket;
import com.computerstorage.common.network.NetworkChannel;
import com.computerstorage.common.transfer.TransferCondition;
import com.computerstorage.common.transfer.TransferFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Minimal functional editor for creating a logistics transfer program. */
public final class ProgramEditorScreen extends Screen {
    private final Screen parent;
    private final String sourceId;
    private final String destinationId;
    private EditBox idBox;
    private EditBox itemBox;
    private EditBox amountBox;
    private EditBox priorityBox;
    private EditBox intervalBox;

    public ProgramEditorScreen(Screen parent, String sourceId, String destinationId) {
        super(Component.literal("Create Transfer Program"));
        this.parent = parent;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
    }

    @Override protected void init() {
        int x = width / 2 - 100;
        idBox = addRenderableWidget(new EditBox(font, x, 42, 200, 20, Component.literal("Program ID")));
        idBox.setValue("transfer_1");
        itemBox = addRenderableWidget(new EditBox(font, x, 72, 200, 20, Component.literal("Item ID")));
        itemBox.setValue("minecraft:iron_ingot");
        amountBox = addRenderableWidget(new EditBox(font, x, 102, 95, 20, Component.literal("Amount")));
        amountBox.setValue("64");
        priorityBox = addRenderableWidget(new EditBox(font, x + 105, 102, 95, 20, Component.literal("Priority")));
        priorityBox.setValue("100");
        intervalBox = addRenderableWidget(new EditBox(font, x, 132, 200, 20, Component.literal("Interval (ticks)")));
        intervalBox.setValue("20");
        addRenderableWidget(Button.builder(Component.literal("SAVE PROGRAM"), b -> save())
                .bounds(x, 164, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("CANCEL"), b -> minecraft.setScreen(parent))
                .bounds(x, 188, 200, 20).build());
    }

    private void save() {
        int amount = parse(amountBox.getValue(), 64);
        int priority = parse(priorityBox.getValue(), 100);
        long interval = Math.max(1L, parseLong(intervalBox.getValue(), 20L));
        var packet = new CreateTransferProgramPacket(idBox.getValue(), sourceId, destinationId,
                TransferFilter.Mode.WHITELIST, itemBox.getValue(), priority, amount, 1, 4096,
                TransferCondition.ALWAYS, interval, 0, true);
        NetworkChannel.CHANNEL.sendToServer(packet);
        minecraft.setScreen(parent);
    }

    private static int parse(String value, int fallback) { try { return Integer.parseInt(value); } catch (NumberFormatException e) { return fallback; } }
    private static long parseLong(String value, long fallback) { try { return Long.parseLong(value); } catch (NumberFormatException e) { return fallback; } }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        int x = width / 2 - 100;
        graphics.drawCenteredString(font, title, width / 2, 18, 0xFFFFFFFF);
        graphics.drawString(font, "SOURCE: " + sourceId, x, 30, 0xFFB9E6FF, false);
        graphics.drawString(font, "DESTINATION: " + destinationId, x, 218, 0xFFB9E6FF, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
