package com.computerstorage.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class MinecraftBootstrapExtension implements BeforeAllCallback {
    private static volatile boolean bootstrapped;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (bootstrapped) return;
        synchronized (MinecraftBootstrapExtension.class) {
            if (bootstrapped) return;
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            bootstrapped = true;
        }
    }
}
