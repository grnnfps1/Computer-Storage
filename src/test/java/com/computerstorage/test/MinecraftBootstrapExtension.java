package com.computerstorage.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Boots Minecraft registries only for tests that actually exercise Minecraft runtime types. */
public final class MinecraftBootstrapExtension implements BeforeAllCallback {
    private static boolean bootstrapped;

    @Override
    public synchronized void beforeAll(ExtensionContext context) {
        String className = context.getRequiredTestClass().getName();
        // Pure Java/domain tests must never initialize the Minecraft/Forge runtime.
        if (className.startsWith("com.computerstorage.client.gui.")
                || className.startsWith("com.computerstorage.common.compat.")) {
            return;
        }
        if (!bootstrapped) {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}
