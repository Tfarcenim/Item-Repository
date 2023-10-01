package tfar.nabba.platform;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.ItemHandlerHelper;
import tfar.nabba.NABBAForge;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.net.server.C2SScrollKeyPacket;
import tfar.nabba.platform.services.IPlatformHelper;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public boolean canItemStacksStack(ItemStack stackA, ItemStack stackB) {
        return ItemHandlerHelper.canItemStacksStack(stackA,stackB);
    }

    @Override
    public void registerGameObjects() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NABBAForge::registerObj);
    }

    @Override
    public void sendScrollKeyRingPacket(boolean right) {
        C2SScrollKeyPacket.send(right);
    }
}