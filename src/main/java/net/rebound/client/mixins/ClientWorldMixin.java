package net.rebound.client.mixins;

import net.rebound.client.manager.ModuleManager;
import net.rebound.client.mods.Module;
import net.rebound.client.mods.pvp.AutoCrystal;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onAddEntity(Entity entity, CallbackInfo ci) {
        // Crystal Aura
        if (entity.getType() == EntityType.END_CRYSTAL) {

            Module m = ModuleManager.getModule(new AutoCrystal());
            assert m != null;
            ((AutoCrystal)m).onCrystalAdded((EndCrystalEntity) entity);
        }
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void onRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        ClientWorld world = (ClientWorld) (Object) this;
        Entity entity = world.getEntityById(entityId);

        if (entity == null) return;

        if (entity.getType() == EntityType.END_CRYSTAL) {
            Module m = ModuleManager.getModule(new AutoCrystal());
            assert m != null;
            ((AutoCrystal)m).onCrystalRemoved((EndCrystalEntity) entity);
        }
    }
}