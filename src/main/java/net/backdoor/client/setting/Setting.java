package net.backdoor.client.setting;

import net.backdoor.client.devutil.IVisible;

import java.util.function.Consumer;

public class Setting<T> {
    public final String name, description;
    private final IVisible visible;

    protected final T defaultValue;
    protected T value;

    public final Consumer<Setting<T>> onModuleActivated;
    private final Consumer<T> onChanged;

    public double lengthValue;

    public boolean lastWasVisible;

    public Setting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.onChanged = onChanged;
        this.onModuleActivated = onModuleActivated;
        this.visible = visible;

        this.lengthValue = 1.0;
        this.value = defaultValue;

    }

    public T getValue() {
        return value;
    }
    public void setValue(T value) {this.value = value;}

    public String getName() {
        return name;
    }

    public boolean isValueTrue() {
        if (this.value.getClass().equals(Boolean.class)) {
            if ((boolean) this.value) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }



}
