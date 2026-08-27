package com.computerstorage.common.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class ComputerEventBus {
    private final List<Consumer<ComputerEvent>> listeners = new CopyOnWriteArrayList<>();
    public void subscribe(Consumer<ComputerEvent> listener) { listeners.add(listener); }
    public void publish(ComputerEvent event) { listeners.forEach(listener -> listener.accept(event)); }
}
