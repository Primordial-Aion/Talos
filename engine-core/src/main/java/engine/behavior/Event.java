package engine.behavior;

import java.util.function.Consumer;

public class Event<T> {
    private final String name;
    private java.util.List<Consumer<T>> listeners;
    
    public Event(String name) {
        this.name = name;
        this.listeners = new java.util.ArrayList<>();
    }
    
    public void subscribe(Consumer<T> listener) {
        listeners.add(listener);
    }
    
    public void unsubscribe(Consumer<T> listener) {
        listeners.remove(listener);
    }
    
    public void emit(T data) {
        for (Consumer<T> listener : listeners) {
            listener.accept(data);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public int getListenerCount() {
        return listeners.size();
    }
    
    public void clear() {
        listeners.clear();
    }
}