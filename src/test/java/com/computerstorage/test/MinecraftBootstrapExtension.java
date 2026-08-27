package com.computerstorage.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Boots Minecraft's static registries for tests that exercise Minecraft runtime types. */
public final class MinecraftBootstrapExtension implements BeforeAllCallback {
    private static boolean bootstrapped;

    @Override
    public synchronized void beforeAll(ExtensionContext context) {
        String className = context.getRequiredTestClass().getName();
        // Pure presentation-model tests must remain independent from the Forge runtime.
        if (className.startsWith("com.computerstorage.client.gui.")) {
            return;
        }
        if (!bootstrapped) {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}
