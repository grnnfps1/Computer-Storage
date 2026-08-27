package com.computerstorage.common.hardware;

public enum HardwareSlot {
    CPU(HardwareType.CPU, 1),
    RAM1(HardwareType.RAM, 1), RAM2(HardwareType.RAM, 2), RAM3(HardwareType.RAM, 3), RAM4(HardwareType.RAM, 4),
    GPU(HardwareType.GPU, 1), NIC(HardwareType.NIC, 1), POWER(HardwareType.POWER, 1), COOLER(HardwareType.COOLER, 1),
    SSD1(HardwareType.SSD, 1), SSD2(HardwareType.SSD, 2), SSD3(HardwareType.SSD, 3), SSD4(HardwareType.SSD, 4);

    private final HardwareType type;
    private final int index;
    HardwareSlot(HardwareType type, int index) { this.type = type; this.index = index; }
    public HardwareType type() { return type; }
    public int index() { return index; }
}
