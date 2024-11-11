package net.backdoor.client.mods.pvp;

import net.backdoor.client.mods.Category;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.backdoor.client.mods.Module;
import net.backdoor.client.devutil.FindItemResult;
import net.backdoor.client.devutil.InvUtil;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class AutoTotem extends Module {


    public AutoTotem() {
        super("AutoTotem", Category.PVP, new ArrayList<>());
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private boolean isTotemInOffhand(PlayerEntity player) {
        ItemStack offhandItem = player.getEquippedStack(EquipmentSlot.OFFHAND);
        return offhandItem.getItem() == Items.TOTEM_OF_UNDYING;
    }

    //old

    /*private int findTotemInInventory(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    private void replaceTotemInOffhand(PlayerEntity player, int totemSlot) {
        ItemStack totemStack = player.getInventory().getStack(totemSlot);
        ItemStack currentOffhandItem = player.getEquippedStack(EquipmentSlot.OFFHAND);

        player.getInventory().setStack(totemSlot, currentOffhandItem);
        player.equipStack(EquipmentSlot.OFFHAND, totemStack);
    }*/

    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        PlayerEntity player = client.player;

        FindItemResult result = InvUtil.find(Items.TOTEM_OF_UNDYING);

        if (enabled) {
            if (!isTotemInOffhand(player)) {
                InvUtil.move().from(result.slot()).toOffhand();
            } else if (player.getOffHandStack().isEmpty()) {
                InvUtil.move().from(result.slot()).toOffhand();
            }
        }
    }

    @Override
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
