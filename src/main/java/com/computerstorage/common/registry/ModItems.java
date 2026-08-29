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
import com.computerstorage.common.item.CreativeEnergyCellItem;
import com.computerstorage.common.item.HardwareItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ComputerStorage.MOD_ID);

    public static final RegistryObject<Item> MOTHERBOARD_CONTROLLER = ITEMS.register("motherboard_controller", () -> new BlockItem(ModBlocks.MOTHERBOARD_CONTROLLER.get(), new Item.Properties()));
    public static final RegistryObject<Item> STORAGE_MONITOR = ITEMS.register("storage_monitor", () -> new BlockItem(ModBlocks.STORAGE_MONITOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BOOT_DISK = ITEMS.register("boot_disk", () -> new BootDiskItem(new Item.Properties()));

    /** DEBUG/CREATIVE ONLY: endless FE source, see CreativeEnergyCellBlock. */
    public static final RegistryObject<Item> CREATIVE_ENERGY_CELL = ITEMS.register("creative_energy_cell", () -> new CreativeEnergyCellItem(ModBlocks.CREATIVE_ENERGY_CELL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CPU_BRONZE = ITEMS.register("cpu_bronze", () -> new HardwareItem(new Item.Properties(), () -> new CpuComponent("Bronze CPU", 1, 1.0, 5)));
    public static final RegistryObject<Item> RAM_1GB = ITEMS.register("ram_1gb", () -> new HardwareItem(new Item.Properties(), () -> new RamComponent("1 GB RAM", 1024, 1)));
    public static final RegistryObject<Item> GPU_DIAMOND = ITEMS.register("gpu_diamond", () -> new HardwareItem(new Item.Properties(), () -> new GpuComponent("Diamond GPU", 8)));
    public static final RegistryObject<Item> NIC_ETHERNET = ITEMS.register("nic_ethernet", () -> new HardwareItem(new Item.Properties(), () -> new NicComponent("Ethernet NIC", NicComponent.Mode.ETHERNET, 64)));

    public static final RegistryObject<Item> SSD_1K = registerSsd("ssd_1k", "1K SSD", 1_024);
    public static final RegistryObject<Item> SSD_4K = registerSsd("ssd_4k", "4K SSD", 4_096);
    public static final RegistryObject<Item> SSD_16K = registerSsd("ssd_16k", "16K SSD", 16_384);
    public static final RegistryObject<Item> SSD_64K = registerSsd("ssd_64k", "64K SSD", 65_536);
    public static final RegistryObject<Item> SSD_256K = registerSsd("ssd_256k", "256K SSD", 262_144);

    public static final RegistryObject<Item> PSU_BASIC = ITEMS.register("psu_basic", () -> new HardwareItem(new Item.Properties(), () -> new PowerComponent("Basic PSU", 100_000, 2_000)));
    public static final RegistryObject<Item> COOLER_BASIC = ITEMS.register("cooler_basic", () -> new HardwareItem(new Item.Properties(), () -> new CoolerComponent("Basic Cooler", 1)));

    private static RegistryObject<Item> registerSsd(String id, String name, long capacity) {
        return ITEMS.register(id, () -> new HardwareItem(new Item.Properties(), () -> new SsdComponent(name, capacity)));
    }

    private ModItems() {}
}
