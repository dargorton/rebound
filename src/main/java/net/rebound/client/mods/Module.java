package net.rebound.client.mods;

import net.rebound.client.setting.Setting;

import java.util.ArrayList;

public abstract class Module {

    public String name;

    public Category category;

    public boolean enabled;

    public ArrayList<Setting<?>> settings;

    public Module(String name, Category category,  ArrayList<Setting<?>> settings) {
        this.name = name;
        this.category = category;
        this.settings = settings;
        this.enabled = false;
    }


    public boolean getEnabled() {
        return this.enabled;
    }

    public void setSettings(ArrayList<Setting<?>> settings) {
        this.settings = settings;
    }

    public ArrayList<Setting<?>> getSettings() {return this.settings;}

    public String getName() {
        return this.name;
    }

    public Category getCategory() {return category;}

    public Setting getSettingFromName(String name) {
        for (Setting setting :settings) {
            if (setting.name.equals(name)) {
                return setting;
            }
        }
        return null;
    }

    public abstract void toggle();
}
