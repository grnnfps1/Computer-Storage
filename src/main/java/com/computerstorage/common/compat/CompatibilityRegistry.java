package com.computerstorage.common.compat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small registry for optional integration adapters; core runtime has no third-party dependencies. */
public final class CompatibilityRegistry {
    private final Map<String, String> adapters = new LinkedHashMap<>();

    public void register(String modId, String adapterName) {
        if (modId == null || modId.isBlank() || adapterName == null || adapterName.isBlank()) return;
        adapters.put(modId, adapterName);
    }

    public boolean has(String modId) { return adapters.containsKey(modId); }
    public Map<String, String> snapshot() { return Collections.unmodifiableMap(new LinkedHashMap<>(adapters)); }
}
