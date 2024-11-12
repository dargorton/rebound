package net.backdoor.client.manager;

import net.backdoor.client.mods.Category;
import net.backdoor.client.mods.Module;
import net.backdoor.client.mods.movement.*;
import net.backdoor.client.mods.pvp.AutoCrystal;
import net.backdoor.client.mods.pvp.AutoTotem;
import net.backdoor.client.mods.render.ESP;
import net.backdoor.client.mods.util.Blink;
import net.backdoor.client.mods.util.FakePlayer;
import net.backdoor.client.mods.util.NoFall;
import net.backdoor.client.mods.util.Velocity;
import java.util.ArrayList;


public class ModuleManager {
    public static ArrayList<Module> list = new ArrayList<>();
    public static void init() {
        list.add(new AutoCrystal());
        list.add(new AutoSprint());
        list.add(new ElytraFly());
        list.add(new Speed());
        list.add(new AutoTotem());
        list.add(new AutoTotem());
        list.add(new CreativeFly());
        list.add(new NoFall());
        list.add(new Velocity());
        list.add(new ElytraBoost());
        list.add(new ESP());
        list.add(new EntitySpeed());
        list.add(new Strafe());//until settings implemented; just rawdogging.
        list.add(new FakePlayer());
        list.add(new ElytraBounce());
        list.add(new Blink());

    }



    public static Module getModule(Module m) {
        for (Module module: list) {
            if (module.getClass() == m.getClass())
                return module;
        }
        return null;
    }


    public static ArrayList<Module> getModulesFromCategory(Category c) {
        ArrayList<Module> finalList = new ArrayList<>();


        for (Module module: list) {
            if (module.getCategory() == c) {
                finalList.add(module);
            }
        }

        return finalList;
    }
}
