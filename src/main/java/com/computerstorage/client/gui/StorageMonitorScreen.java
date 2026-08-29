package com.computerstorage.client.gui;

import com.computerstorage.common.menu.StorageMonitorMenu;
import com.computerstorage.common.network.NetworkChannel;
import com.computerstorage.common.network.WithdrawFromIndexPacket;
import com.computerstorage.common.storage.IndexQuery;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Terminal onto the storage index, laid out as a wide console: filter rail on the left, searchable
 * item grid in the middle, the player's own inventory bottom right.
 *
 * <p>The listing is not made of slots — the index holds far more distinct items than a container
 * could represent — so it is drawn as a virtual grid over the synced snapshot and clicks turn into
 * withdrawal packets. The player's inventory, by contrast, keeps real slots, so moving items around
 * stays vanilla behaviour rather than something reimplemented here.
 *
 * <p>The crafting panel is a drawn placeholder for a future phase: it holds nothing, accepts no
 * clicks, and says so on screen, so the layout never promises a function that does not exist.
 */
public final class StorageMonitorScreen extends AbstractContainerScreen<StorageMonitorMenu> {
    private static final int WIDTH = 354;
    private static final int HEIGHT = 246;

    private static final int SIDEBAR_LEFT = 6;
    private static final int SIDEBAR_RIGHT = 92;
    private static final int MAIN_LEFT = 96;
    private static final int MAIN_RIGHT = 348;

    private static final int HEADER_TOP = 6;
    private static final int HEADER_BOTTOM = 28;
    private static final int SEARCH_LEFT = 242;
    private static final int SEARCH_TOP = 11;
    private static final int SEARCH_WIDTH = 100;

    private static final int COLUMNS = 13;
    private static final int ROWS = 5;
    private static final int CELL = 18;
    private static final int GRID_LEFT = 105;
    private static final int GRID_TOP = 34;
    private static final int PER_PAGE = COLUMNS * ROWS;

    private static final int PAGER_TOP = 128;
    private static final int PAGER_HEIGHT = 14;
    private static final int ARROW_WIDTH = 16;
    private static final int PREV_OFFSET = -46;
    private static final int NEXT_OFFSET = 30;

    private static final int BOTTOM_TOP = 148;
    private static final int BOTTOM_BOTTOM = 242;
    private static final int CRAFT_RIGHT = 176;
    private static final int INVENTORY_PANEL_LEFT = 180;

    private static final int FILTER_TOP = 26;
    private static final int FILTER_ROW = 14;

    /** Only categories the mod actually registers items for. Everything else lands in OTHER. */
    private enum Category {
        ALL("All", path -> true),
        CPU("CPU", path -> path.startsWith("cpu")),
        RAM("RAM", path -> path.startsWith("ram")),
        GPU("GPU", path -> path.startsWith("gpu")),
        SSD("SSD", path -> path.startsWith("ssd")),
        PSU("Power (PSU)", path -> path.startsWith("psu")),
        COOLER("Cooler", path -> path.startsWith("cooler")),
        NIC("Network", path -> path.startsWith("nic")),
        OTHER("Other", path -> !Category.isHardware(path));

        private final String label;
        private final Predicate<String> matches;

        Category(String label, Predicate<String> matches) {
            this.label = label;
            this.matches = matches;
        }

        private static boolean isHardware(String path) {
            return path.startsWith("cpu") || path.startsWith("ram") || path.startsWith("gpu")
                    || path.startsWith("ssd") || path.startsWith("psu") || path.startsWith("cooler")
                    || path.startsWith("nic");
        }
    }

    private EditBox search;
    private Category category = Category.ALL;
    private int page;

    public StorageMonitorScreen(StorageMonitorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        search = new EditBox(font, leftPos + SEARCH_LEFT + 4, topPos + SEARCH_TOP + 3,
                SEARCH_WIDTH - 10, 10, Component.translatable("gui.computerstorage.search"));
        search.setBordered(false);
        search.setTextColor(GuiTheme.TEXT);
        search.setResponder(text -> page = 0);
        addRenderableWidget(search);
        setInitialFocus(search);
    }

    /** The listing after the category rail and the search box have both had their say. */
    private List<ItemStack> visible() {
        List<ItemStack> byCategory = new ArrayList<>();
        for (ItemStack stack : menu.listing()) {
            if (category.matches.test(pathOf(stack))) byCategory.add(stack);
        }
        return IndexQuery.filter(byCategory, search == null ? "" : search.getValue());
    }

    private static String pathOf(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    private int pageCount(List<ItemStack> entries) {
        return Math.max(1, (entries.size() + PER_PAGE - 1) / PER_PAGE);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        List<ItemStack> entries = visible();
        page = Math.max(0, Math.min(page, pageCount(entries) - 1));

        GuiTheme.window(graphics, left, top, imageWidth, imageHeight);
        renderSidebar(graphics, left, top, mouseX, mouseY, entries);
        renderHeader(graphics, left, top);
        renderGridWells(graphics, left, top, mouseX, mouseY);
        renderPager(graphics, left, top, mouseX, mouseY, entries);
        renderCraftingPlaceholder(graphics, left, top);
        renderInventoryPanel(graphics, left, top, mouseX, mouseY);
    }

    private void renderSidebar(GuiGraphics graphics, int left, int top, int mouseX, int mouseY,
                               List<ItemStack> entries) {
        GuiTheme.panel(graphics, left + SIDEBAR_LEFT, top + HEADER_TOP,
                left + SIDEBAR_RIGHT, top + BOTTOM_BOTTOM);
        graphics.drawString(font, "FILTERS", left + SIDEBAR_LEFT + 6, top + HEADER_TOP + 6,
                GuiTheme.ACCENT, false);

        Category[] all = Category.values();
        for (int i = 0; i < all.length; i++) {
            int rowTop = top + FILTER_TOP + i * FILTER_ROW;
            boolean selected = all[i] == category;
            boolean hovered = GuiTheme.over(mouseX, mouseY, left + SIDEBAR_LEFT + 4, rowTop,
                    left + SIDEBAR_RIGHT - 4, rowTop + FILTER_ROW - 2);
            GuiTheme.chip(graphics, left + SIDEBAR_LEFT + 4, rowTop,
                    left + SIDEBAR_RIGHT - 4, rowTop + FILTER_ROW - 2, selected, hovered);
            graphics.drawString(font, all[i].label, left + SIDEBAR_LEFT + 9, rowTop + 2,
                    selected ? GuiTheme.TEXT : GuiTheme.TEXT_MUTED, false);
        }

        boolean running = menu.computerRunning();
        int statusTop = top + FILTER_TOP + all.length * FILTER_ROW + 8;
        GuiTheme.lamp(graphics, left + SIDEBAR_LEFT + 8, statusTop + 2, running);
        graphics.drawString(font, running ? "ONLINE" : "OFFLINE", left + SIDEBAR_LEFT + 16, statusTop,
                running ? GuiTheme.OK : GuiTheme.WARN, false);
        graphics.drawString(font, entries.size() + " entries", left + SIDEBAR_LEFT + 8, statusTop + 12,
                GuiTheme.TEXT_DIM, false);
    }

    private void renderHeader(GuiGraphics graphics, int left, int top) {
        GuiTheme.header(graphics, left + MAIN_LEFT, top + HEADER_TOP, left + MAIN_RIGHT, top + HEADER_BOTTOM);
        graphics.drawString(font, "COMPUTER STORAGE TERMINAL", left + MAIN_LEFT + 6, top + HEADER_TOP + 7,
                GuiTheme.ACCENT, false);
        GuiTheme.chip(graphics, left + SEARCH_LEFT, top + SEARCH_TOP,
                left + SEARCH_LEFT + SEARCH_WIDTH, top + SEARCH_TOP + 14, false, false);
        if (search != null && search.getValue().isEmpty()) {
            graphics.drawString(font, Component.translatable("gui.computerstorage.search"),
                    left + SEARCH_LEFT + 4, top + SEARCH_TOP + 3, GuiTheme.TEXT_DIM, false);
        }
    }

    private void renderGridWells(GuiGraphics graphics, int left, int top, int mouseX, int mouseY) {
        GuiTheme.panel(graphics, left + MAIN_LEFT, top + HEADER_BOTTOM + 2,
                left + MAIN_RIGHT, top + BOTTOM_TOP - 4);
        for (int row = 0; row < ROWS; row++) {
            int rowTop = top + GRID_TOP + row * CELL;
            // A whisper of tint on alternate rows so the eye can track across a wide grid.
            if (row % 2 == 1) {
                graphics.fill(left + GRID_LEFT - 1, rowTop,
                        left + GRID_LEFT - 1 + COLUMNS * CELL, rowTop + CELL, GuiTheme.ROW_TINT);
            }
            for (int col = 0; col < COLUMNS; col++) {
                int x = left + GRID_LEFT + col * CELL + 1;
                int y = rowTop + 1;
                GuiTheme.slotWell(graphics, x, y, false, GuiTheme.overCell(mouseX, mouseY, x, y));
            }
        }
    }

    private void renderPager(GuiGraphics graphics, int left, int top, int mouseX, int mouseY,
                             List<ItemStack> entries) {
        int pages = pageCount(entries);
        if (pages <= 1) return;
        int centre = left + GRID_LEFT + COLUMNS * CELL / 2;
        String label = (page + 1) + " / " + pages;
        graphics.drawString(font, label, centre - font.width(label) / 2, top + PAGER_TOP + 3,
                GuiTheme.TEXT, false);
        drawArrow(graphics, centre + PREV_OFFSET, top + PAGER_TOP, "<", page > 0, mouseX, mouseY);
        drawArrow(graphics, centre + NEXT_OFFSET, top + PAGER_TOP, ">", page < pages - 1, mouseX, mouseY);
    }

    private void drawArrow(GuiGraphics graphics, int left, int top, String glyph, boolean enabled,
                           int mouseX, int mouseY) {
        boolean hovered = enabled
                && GuiTheme.over(mouseX, mouseY, left, top, left + ARROW_WIDTH, top + PAGER_HEIGHT);
        GuiTheme.chip(graphics, left, top, left + ARROW_WIDTH, top + PAGER_HEIGHT, false, hovered);
        graphics.drawString(font, glyph, left + ARROW_WIDTH / 2 - font.width(glyph) / 2, top + 3,
                enabled ? GuiTheme.TEXT : GuiTheme.TEXT_DIM, false);
    }

    /**
     * Drawn, inert, and labelled as such. There is no crafting logic behind it, so it holds no
     * slots and swallows no clicks.
     */
    private void renderCraftingPlaceholder(GuiGraphics graphics, int left, int top) {
        GuiTheme.ghostPanel(graphics, left + MAIN_LEFT, top + BOTTOM_TOP, left + CRAFT_RIGHT,
                top + BOTTOM_BOTTOM);
        graphics.drawString(font, "CRAFTING", left + MAIN_LEFT + 6, top + BOTTOM_TOP + 6,
                GuiTheme.TEXT_DIM, false);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                GuiTheme.ghostWell(graphics, left + MAIN_LEFT + 10 + col * CELL,
                        top + BOTTOM_TOP + 22 + row * CELL);
            }
        }
        graphics.drawString(font, "Future phase:", left + MAIN_LEFT + 6, top + BOTTOM_BOTTOM - 22,
                GuiTheme.TEXT_DIM, false);
        graphics.drawString(font, "not implemented", left + MAIN_LEFT + 6, top + BOTTOM_BOTTOM - 12,
                GuiTheme.TEXT_DIM, false);
    }

    private void renderInventoryPanel(GuiGraphics graphics, int left, int top, int mouseX, int mouseY) {
        GuiTheme.panel(graphics, left + INVENTORY_PANEL_LEFT, top + BOTTOM_TOP, left + MAIN_RIGHT,
                top + BOTTOM_BOTTOM);
        graphics.drawString(font, playerInventoryTitle, left + INVENTORY_PANEL_LEFT + 4,
                top + BOTTOM_TOP + 5, GuiTheme.TEXT_MUTED, false);
        for (Slot slot : menu.slots) {
            int x = left + slot.x;
            int y = top + slot.y;
            GuiTheme.slotWell(graphics, x, y, false, GuiTheme.overCell(mouseX, mouseY, x, y));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderEntries(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderEntries(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!menu.computerRunning()) {
            Component offline = Component.translatable("gui.computerstorage.computer_offline");
            graphics.drawString(font, offline,
                    leftPos + GRID_LEFT + (COLUMNS * CELL - font.width(offline)) / 2,
                    topPos + GRID_TOP + ROWS * CELL / 2 - 4, GuiTheme.WARN, false);
            return;
        }
        List<ItemStack> entries = visible();
        int first = page * PER_PAGE;
        for (int cell = 0; cell < PER_PAGE; cell++) {
            int index = first + cell;
            if (index >= entries.size()) break;
            ItemStack stack = entries.get(index);
            int x = leftPos + GRID_LEFT + (cell % COLUMNS) * CELL + 1;
            int y = topPos + GRID_TOP + (cell / COLUMNS) * CELL + 1;
            graphics.renderItem(stack, x, y);
            graphics.renderItemDecorations(font, stack.copyWithCount(1), x, y, compact(stack.getCount()));
        }
        ItemStack hovered = entryAt(mouseX, mouseY);
        if (hovered != null) graphics.renderTooltip(font, hovered, mouseX, mouseY);
    }

    /** Counts run far past 99, so the badge shows 1.2K rather than an unreadable number. */
    private static String compact(int count) {
        if (count < 1_000) return String.valueOf(count);
        if (count < 1_000_000) return (count / 100 / 10.0) + "K";
        return (count / 100_000 / 10.0) + "M";
    }

    private ItemStack entryAt(int mouseX, int mouseY) {
        List<ItemStack> entries = visible();
        int relX = mouseX - (leftPos + GRID_LEFT);
        int relY = mouseY - (topPos + GRID_TOP);
        if (relX < 0 || relY < 0) return null;
        int col = relX / CELL;
        int row = relY / CELL;
        if (col >= COLUMNS || row >= ROWS) return null;
        int index = page * PER_PAGE + row * COLUMNS + col;
        return index >= 0 && index < entries.size() ? entries.get(index) : null;
    }

    /** All chrome is drawn in renderBg with absolute coordinates, so nothing is left to do here. */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (int) mouseX;
        int y = (int) mouseY;
        if (clickedCategory(x, y) || clickedPager(x, y)) return true;

        ItemStack entry = entryAt(x, y);
        if (entry != null && menu.computerRunning()) {
            NetworkChannel.CHANNEL.sendToServer(
                    new WithdrawFromIndexPacket(entry.copyWithCount(1), hasShiftDown()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickedCategory(int mouseX, int mouseY) {
        Category[] all = Category.values();
        for (int i = 0; i < all.length; i++) {
            int rowTop = topPos + FILTER_TOP + i * FILTER_ROW;
            if (GuiTheme.over(mouseX, mouseY, leftPos + SIDEBAR_LEFT + 4, rowTop,
                    leftPos + SIDEBAR_RIGHT - 4, rowTop + FILTER_ROW - 2)) {
                category = all[i];
                page = 0;
                return true;
            }
        }
        return false;
    }

    private boolean clickedPager(int mouseX, int mouseY) {
        int pages = pageCount(visible());
        if (pages <= 1) return false;
        int centre = leftPos + GRID_LEFT + COLUMNS * CELL / 2;
        int top = topPos + PAGER_TOP;
        if (GuiTheme.over(mouseX, mouseY, centre + PREV_OFFSET, top,
                centre + PREV_OFFSET + ARROW_WIDTH, top + PAGER_HEIGHT)) {
            page = Math.max(0, page - 1);
            return true;
        }
        if (GuiTheme.over(mouseX, mouseY, centre + NEXT_OFFSET, top,
                centre + NEXT_OFFSET + ARROW_WIDTH, top + PAGER_HEIGHT)) {
            page = Math.min(pages - 1, page + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int pages = pageCount(visible());
        page = Math.max(0, Math.min(pages - 1, page - (int) Math.signum(delta)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Let the search box swallow typing, including the inventory key, but never Escape.
        if (search != null && search.isFocused() && keyCode != 256) {
            return search.keyPressed(keyCode, scanCode, modifiers) || search.canConsumeInput()
                    || super.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
