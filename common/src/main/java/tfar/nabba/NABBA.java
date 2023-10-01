package tfar.nabba;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;
import tfar.nabba.platform.Services;

import javax.annotation.Nullable;
import java.io.File;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class NABBA {

    public static final String MODID = "nabba";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static MinecraftServer server;


    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {

        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        if (Services.PLATFORM.isModLoaded("examplemod")) {
            Constants.LOG.info("Hello to examplemod");
        }

        Services.PLATFORM.registerGameObjects();
    }


    public static void onServerStarting(MinecraftServer server) {
        NABBA.server = server;
        LevelStorageSource.LevelStorageAccess storageSource = server.storageSource;
        File file = storageSource.getDimensionPath(server.getLevel(Level.OVERWORLD).dimension())
                .resolve("data/nabba").toFile();
        file.mkdirs();
    }

    public static void onServerStop() {
        server = null;
    }
}