package com.computerstorage.common.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilityRegistryTest {
    @Test
    void optionalAdaptersAreIsolated() {
        CompatibilityRegistry registry = new CompatibilityRegistry();
        registry.register("create", "CreateAdapter");
        assertTrue(registry.has("create"));
        assertFalse(registry.has("missing_mod"));
        assertEquals("CreateAdapter", registry.snapshot().get("create"));
    }
}
