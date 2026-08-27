package com.computerstorage.common.storage;

public enum StorageTier {
    SSD_1K(1_024),
    SSD_4K(4_096),
    SSD_16K(16_384),
    SSD_64K(65_536),
    SSD_256K(262_144);

    private final long capacity;
    StorageTier(long capacity) { this.capacity = capacity; }
    public long capacity() { return capacity; }
}
