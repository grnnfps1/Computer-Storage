package com.computerstorage.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards {@link MinecraftBootstrapExtension#isMissingEventBusTransformer(Throwable)} so the one
 * tolerated bootstrap failure can never widen into swallowing real ones.
 */
class MinecraftBootstrapExtensionTest {

    private static Throwable atNetworkHooksInit(Throwable throwable) {
        throwable.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("net.minecraftforge.network.NetworkHooks", "init", "NetworkHooks.java", 52),
                new StackTraceElement("net.minecraft.server.Bootstrap", "bootStrap", "Bootstrap.java", 62),
        });
        return throwable;
    }

    private static Throwable elsewhere(Throwable throwable) {
        throwable.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("net.minecraft.core.MappedRegistry", "<init>", "MappedRegistry.java", 85),
                new StackTraceElement("net.minecraft.server.Bootstrap", "bootStrap", "Bootstrap.java", 43),
        });
        return throwable;
    }

    @Test
    void toleratesTheMissingEventBusTransformer() {
        Throwable cause = atNetworkHooksInit(
                new NoSuchMethodException("net.minecraftforge.network.NetworkEvent.<init>()"));
        Throwable failure = atNetworkHooksInit(new ExceptionInInitializerError(cause));
        assertTrue(MinecraftBootstrapExtension.isMissingEventBusTransformer(failure));
    }

    @Test
    void rejectsUnrelatedBootstrapFailures() {
        assertFalse(MinecraftBootstrapExtension.isMissingEventBusTransformer(
                elsewhere(new IllegalArgumentException("Not bootstrapped"))));
        assertFalse(MinecraftBootstrapExtension.isMissingEventBusTransformer(
                elsewhere(new OutOfMemoryError("registry"))));
    }

    @Test
    void rejectsMissingMethodOutsideNetworkHooks() {
        assertFalse(MinecraftBootstrapExtension.isMissingEventBusTransformer(
                elsewhere(new NoSuchMethodException("net.minecraftforge.network.NetworkEvent.<init>()"))));
    }

    @Test
    void rejectsNonForgeMissingConstructorAtNetworkHooks() {
        assertFalse(MinecraftBootstrapExtension.isMissingEventBusTransformer(
                atNetworkHooksInit(new NoSuchMethodException("com.example.Whatever.<init>()"))));
    }

    @Test
    void rejectsMissingMemberThatIsNotAConstructor() {
        assertFalse(MinecraftBootstrapExtension.isMissingEventBusTransformer(
                atNetworkHooksInit(new NoSuchMethodException("net.minecraftforge.network.NetworkEvent.getSomething()"))));
    }
}
