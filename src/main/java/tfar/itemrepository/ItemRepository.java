package tfar.itemrepository;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Environment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import tfar.itemrepository.init.ModBlockEntityTypes;
import tfar.itemrepository.init.ModBlocks;
import tfar.itemrepository.init.ModItems;
import tfar.itemrepository.init.ModMenuTypes;
import tfar.itemrepository.world.RepositorySavedData;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemRepository.MODID)
public class ItemRepository {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "itemrepository";

    public static ItemRepository instance;
    public RepositorySavedData data;

    public ItemRepository() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::onBlocksRegistry);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Client::setup);
        }
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        instance = this;
    }

    public void onServerStarted(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        instance.data = server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(RepositorySavedData::loadStatic, RepositorySavedData::new,MODID);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    public void onBlocksRegistry(final RegisterEvent e) {
        e.register(Registry.BLOCK_REGISTRY,new ResourceLocation(MODID,"repository"),() -> ModBlocks.REPOSITORY);
        e.register(Registry.ITEM_REGISTRY,new ResourceLocation(MODID,"repository"),() -> ModItems.REPOSITORY);
        e.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY,new ResourceLocation(MODID,"repository"),() -> ModBlockEntityTypes.REPOSITORY);
        e.register(Registry.MENU_REGISTRY,new ResourceLocation(MODID,"repository"),() -> ModMenuTypes.REPOSITORY);
    }
}