package net.rebound.client.manager;

import net.rebound.client.mods.Category;
import net.rebound.client.mods.Module;
import net.rebound.client.mods.movement.*;
import net.rebound.client.mods.pvp.AutoCrystal;
import net.rebound.client.mods.pvp.AutoTotem;
import net.rebound.client.mods.render.ESP;
import net.rebound.client.mods.util.Blink;
import net.rebound.client.mods.util.FakePlayer;
import net.rebound.client.mods.util.NoFall;
import net.rebound.client.mods.util.Velocity;
import net.rebound.client.mods.movement.*;

import java.util.ArrayList;


public class ModuleManager {
    public static ArrayList<Module> list = new ArrayList<>();
    public static void init() {
        list.add(new AutoCrystal());
        list.add(new AutoSprint());
        list.add(new ElytraFly());
        list.add(new Speed());
        list.add(new AutoTotem());
        list.add(new CreativeFly());
        list.add(new NoFall());
        list.add(new Velocity());
        list.add(new ElytraBoost());
        list.add(new ESP());
        list.add(new EntitySpeed());
       // list.add(new Strafe());
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
