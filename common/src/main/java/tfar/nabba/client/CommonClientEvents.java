package tfar.nabba.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.platform.Services;

public class CommonClientEvents {

    //return true to cancel
    public static boolean onScroll(Minecraft minecraft, double delta) {
        if (minecraft.player != null && minecraft.player.isCrouching() && isKeyRing(minecraft.player.getMainHandItem())) {
            Services.PLATFORM.sendScrollKeyRingPacket(delta > 0);
            return true;
        }
        return false;
    }

    public static boolean isKeyRing(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals("key_ring");
    }
}
