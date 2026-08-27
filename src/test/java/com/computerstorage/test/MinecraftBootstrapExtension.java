package com.computerstorage.test;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Boots the vanilla game registries once per test JVM so tests can use real {@link Items} and
 * {@link ItemStack} instances.
 *
 * <p>Forge patches {@link Bootstrap#bootStrap()} so that its final statement is
 * {@code NetworkHooks.init()}, whose only purpose is to force {@code NetworkConstants} to load
 * before mods are constructed. That call builds the handshake {@code SimpleChannel} and registers
 * an event listener, which needs the no-argument constructor that ModLauncher's EventBus
 * transformer injects into every {@code Event} subclass at class-load time. A plain {@code test}
 * JVM has no transformer, so it always fails with
 * {@code NoSuchMethodException: net.minecraftforge.network.NetworkEvent.<init>()}.
 *
 * <p>Every step unit tests depend on -- {@code BuiltInRegistries.bootStrap()},
 * {@code CreativeModeTabs}, {@code GameData.vanillaSnapshot()} -- has already run by the time that
 * last statement executes. This extension therefore tolerates that one mod-loader-only failure and
 * nothing else: any other throwable is rethrown, and afterwards the registries are verified to be
 * genuinely populated, so a bootstrap that failed for a real reason still fails the test run.
 */
public final class MinecraftBootstrapExtension implements BeforeAllCallback {
    private static volatile boolean bootstrapped;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (bootstrapped) return;
        synchronized (MinecraftBootstrapExtension.class) {
            if (bootstrapped) return;
            SharedConstants.tryDetectVersion();
            try {
                Bootstrap.bootStrap();
            } catch (Throwable failure) {
                if (!isMissingEventBusTransformer(failure)) {
                    throw rethrow(failure);
                }
            }
            verifyRegistriesPopulated();
            bootstrapped = true;
        }
    }

    /**
     * Recognises the single tolerated failure: Forge's trailing {@code NetworkHooks.init()} step
     * aborting because the EventBus transformer never injected an event's no-arg constructor.
     */
    // Package-private so MinecraftBootstrapExtensionTest can prove the guard stays narrow.
    static boolean isMissingEventBusTransformer(Throwable failure) {
        boolean viaNetworkHooks = false;
        Throwable root = failure;
        for (Throwable current = failure; current != null; current = current.getCause()) {
            for (StackTraceElement element : current.getStackTrace()) {
                if ("net.minecraftforge.network.NetworkHooks".equals(element.getClassName())
                        && "init".equals(element.getMethodName())) {
                    viaNetworkHooks = true;
                    break;
                }
            }
            root = current;
        }
        if (!viaNetworkHooks || !(root instanceof NoSuchMethodException)) return false;
        String missing = String.valueOf(root.getMessage());
        return missing.startsWith("net.minecraftforge.") && missing.endsWith(".<init>()");
    }

    /**
     * Fails loudly unless the bootstrap really did produce usable registries, so tolerating the
     * step above can never turn a broken bootstrap into a passing test run.
     */
    private static void verifyRegistriesPopulated() {
        ItemStack probe = new ItemStack(Items.IRON_INGOT);
        if (probe.isEmpty() || probe.getItem() != Items.IRON_INGOT) {
            throw new IllegalStateException("Minecraft bootstrap cannot produce usable ItemStacks");
        }
        // Saving writes the registry name and loading resolves it, so a round trip proves the item
        // registry is populated and queryable in both directions.
        if (ItemStack.of(probe.save(new CompoundTag())).getItem() != Items.IRON_INGOT) {
            throw new IllegalStateException("Minecraft bootstrap left the item registry unusable");
        }
    }

    private static RuntimeException rethrow(Throwable failure) {
        if (failure instanceof Error error) throw error;
        if (failure instanceof RuntimeException runtime) throw runtime;
        return new IllegalStateException("Minecraft bootstrap failed", failure);
    }
}
