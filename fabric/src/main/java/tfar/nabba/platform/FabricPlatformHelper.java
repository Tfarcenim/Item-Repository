package tfar.nabba.platform;

import net.fabricmc.loader.api.FabricLoader;
import tfar.nabba.NABBAFabric;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void registerGameObjects() {
        NABBAFabric.registerObj();
    }

    @Override
    public void sendScrollKeyRingPacket(boolean right) {
        PacketHandler.sendToServer(PacketHandler.scroll_keyring,buf -> buf.writeBoolean(right));
    }
}
