package tfar.nabba;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import tfar.nabba.block.BetterBarrelBlock;
import tfar.nabba.client.Client;
import tfar.nabba.command.RepositoryCommands;
import tfar.nabba.datagen.ModDatagen;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.net.C2SScrollKeyPacket;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.world.RepositorySavedData;

import java.lang.reflect.Field;
import java.util.Locale;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NABBA.MODID)
public class NABBA {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "nabba";

    public static NABBA instance;
    public RepositorySavedData data;
    public MinecraftServer server;

    public NABBA() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::registerObj);
        bus.addListener(ModDatagen::start);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Client::setup);
        }
        addGameEvents();
        instance = this;
    }

    private void addGameEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::leftClick);
    }

    public void onServerStarted(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        instance.server = server;
        instance.data = server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(RepositorySavedData::loadStatic, RepositorySavedData::new,MODID);
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    private void commands(RegisterCommandsEvent e) {
        RepositoryCommands.register(e.getDispatcher());
    }

    private void registerObj(RegisterEvent e) {
        superRegister(e, ModBlocks.class,Registry.BLOCK_REGISTRY, Block.class);
        superRegister(e, ModItems.class,Registry.ITEM_REGISTRY, Item.class);
        superRegister(e, ModBlockEntityTypes.class,Registry.BLOCK_ENTITY_TYPE_REGISTRY, BlockEntityType.class);
        superRegister(e, ModMenuTypes.class,Registry.MENU_REGISTRY, MenuType.class);
//        superRegister(e, ModSoundEvents.class,Registry.SOUND_EVENT_REGISTRY, SoundEvent.class);
    }

    public static <T> void superRegister(RegisterEvent e, Class<?> clazz, ResourceKey<? extends  Registry<T>> resourceKey, Class<?> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    e.register(resourceKey,new ResourceLocation(MODID,field.getName().toLowerCase(Locale.ROOT)),() -> (T)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }


    private void leftClick(PlayerInteractEvent.LeftClickBlock e) {
        BlockState state = e.getEntity().level.getBlockState(e.getPos());

        boolean crouch = e.getEntity().isCrouching();
        
        if (state.getBlock() instanceof BetterBarrelBlock && !crouch && e.getEntity().getMainHandItem().getItem() instanceof AxeItem) {
            e.setCanceled(true);
        }
    }


}