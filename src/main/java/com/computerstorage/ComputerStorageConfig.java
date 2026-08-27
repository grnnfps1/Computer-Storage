package com.computerstorage;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ComputerStorageConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Computer Storage common configuration").push("general");
        DEBUG_LOGGING = builder.comment("Enable verbose Computer Storage logging.")
                .define("debugLogging", false);
        builder.pop();
        SPEC = builder.build();
    }

    private ComputerStorageConfig() {}
}
