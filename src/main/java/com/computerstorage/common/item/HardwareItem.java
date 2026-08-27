package com.computerstorage.common.item;

import com.computerstorage.common.hardware.IHardwareComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/** Physical hardware item. Its component is created only when the item is installed in a Computer. */
public final class HardwareItem extends Item {
    private final Supplier<IHardwareComponent> componentFactory;

    public HardwareItem(Properties properties, Supplier<IHardwareComponent> componentFactory) {
        super(properties);
        this.componentFactory = componentFactory;
    }

    public IHardwareComponent createComponent() {
        return componentFactory.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                                java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        IHardwareComponent component = componentFactory.get();
        tooltip.add(Component.literal(component.getType().name() + " • " + component.getName()));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
