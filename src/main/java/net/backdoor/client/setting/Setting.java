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

    public boolean lastWasVisible;

    public Setting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.onChanged = onChanged;
        this.onModuleActivated = onModuleActivated;
        this.visible = visible;

    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


}
