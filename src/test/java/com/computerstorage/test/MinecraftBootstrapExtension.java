package com.computerstorage.test;

import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Boots Minecraft's static registries before JUnit tests touch ItemStack/Items. */
public final class MinecraftBootstrapExtension implements BeforeAllCallback {
    private static boolean bootstrapped;

    @Override
    public synchronized void beforeAll(ExtensionContext context) {
        if (!bootstrapped) {
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}
