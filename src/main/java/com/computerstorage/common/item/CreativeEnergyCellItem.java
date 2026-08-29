package com.computerstorage.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Carries the debug warning, so the cell cannot be mistaken for survival content in hand. */
public final class CreativeEnergyCellItem extends BlockItem {
    public CreativeEnergyCellItem(Block block, Properties properties) { super(block, properties); }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                                TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.computerstorage.creative_energy_cell.debug")
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.computerstorage.creative_energy_cell.usage")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
