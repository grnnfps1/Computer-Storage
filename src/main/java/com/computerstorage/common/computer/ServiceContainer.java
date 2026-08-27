package com.computerstorage.common.computer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceContainer {
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public <T> void register(Class<T> type, T service) {
        if (service == null) throw new IllegalArgumentException("service cannot be null");
        services.put(type, service);
    }

    public <T> T get(Class<T> type) {
        Object value = services.get(type);
        if (value == null) throw new IllegalStateException("Service not registered: " + type.getName());
        return type.cast(value);
    }

    public <T> T getOrNull(Class<T> type) {
        Object value = services.get(type);
        return value == null ? null : type.cast(value);
    }

    public boolean contains(Class<?> type) { return services.containsKey(type); }
}
