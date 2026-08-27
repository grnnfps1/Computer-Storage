package com.computerstorage.common.energy;

/** Deterministic generator model; the Minecraft block can drive it once registered. */
public final class Generator {
    private final GeneratorProfile profile;
    private long generated;

    public Generator(GeneratorProfile profile) { this.profile = profile; }
    public GeneratorProfile profile() { return profile; }
    public long generated() { return generated; }
    public int generate(EnergyBuffer buffer) {
        int accepted = buffer.receive(profile.generationPerTick());
        generated += accepted;
        return accepted;
    }
}
