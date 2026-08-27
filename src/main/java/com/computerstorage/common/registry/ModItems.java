package com.computerstorage.common.registry;

import com.computerstorage.ComputerStorage;
import com.computerstorage.common.hardware.cooling.CoolerComponent;
import com.computerstorage.common.hardware.cpu.CpuComponent;
import com.computerstorage.common.hardware.gpu.GpuComponent;
import com.computerstorage.common.hardware.nic.NicComponent;
import com.computerstorage.common.hardware.power.PowerComponent;
import com.computerstorage.common.hardware.ram.RamComponent;
import com.computerstorage.common.hardware.storage.SsdComponent;
import com.computerstorage.common.item.BootDiskItem;
import com.computerstorage.common.item.HardwareItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ComputerStorage.MOD_ID);

    public static final RegistryObject<Item> MOTHERBOARD_CONTROLLER = ITEMS.register("motherboard_controller",
            () -> new BlockItem(ModBlocks.MOTHERBOARD_CONTROLLER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BOOT_DISK = ITEMS.register("boot_disk",
            () -> new BootDiskItem(new Item.Properties()));
    public static final RegistryObject<Item> CPU_BRONZE = ITEMS.register("cpu_bronze",
            () -> new HardwareItem(new Item.Properties(), () -> new CpuComponent("Bronze CPU", 1, 1.0, 5)));
    public static final RegistryObject<Item> RAM_1GB = ITEMS.register("ram_1gb",
            () -> new HardwareItem(new Item.Properties(), () -> new RamComponent("1 GB RAM", 1024, 1)));
    public static final RegistryObject<Item> GPU_DIAMOND = ITEMS.register("gpu_diamond",
            () -> new HardwareItem(new Item.Properties(), () -> new GpuComponent("Diamond GPU", 8)));
    public static final RegistryObject<Item> NIC_ETHERNET = ITEMS.register("nic_ethernet",
            () -> new HardwareItem(new Item.Properties(), () -> new NicComponent("Ethernet NIC", NicComponent.Mode.ETHERNET, 64)));
    public static final RegistryObject<Item> SSD_1K = ITEMS.register("ssd_1k",
            () -> new HardwareItem(new Item.Properties(), () -> new SsdComponent("1K SSD", 1024)));
    public static final RegistryObject<Item> PSU_BASIC = ITEMS.register("psu_basic",
            () -> new HardwareItem(new Item.Properties(), () -> new PowerComponent("Basic PSU", 100_000, 2_000)));
    public static final RegistryObject<Item> COOLER_BASIC = ITEMS.register("cooler_basic",
            () -> new HardwareItem(new Item.Properties(), () -> new CoolerComponent("Basic Cooler", 1)));

    private ModItems() {}
}
