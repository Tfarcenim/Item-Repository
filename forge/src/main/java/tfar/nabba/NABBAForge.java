package tfar.nabba;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.client.Client;
import tfar.nabba.command.ModCommands;
import tfar.nabba.datagen.ModDatagen;
import tfar.nabba.init.*;
import tfar.nabba.inventory.FakeSlotSynchronizer;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.world.AntiBarrelSubData;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

import static tfar.nabba.NABBA.server;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NABBA.MODID)
public class NABBAForge {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public NABBAForge() {
        // Register the setup method for modloading
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(ModDatagen::start);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Client::setup);
            bus.addListener(Client::tooltipC);
        }
        addGameEvents();
        NABBA.init();
    }

    private void addGameEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
        MinecraftForge.EVENT_BUS.addListener(this::breakSpeed);
        MinecraftForge.EVENT_BUS.addListener(this::setupSync);
    }

    public void onServerStarting(ServerStartingEvent event) {
        NABBA.onServerStarting(event.getServer());
    }

    public void onServerStopped(ServerStoppedEvent event) {
        NABBA.onServerStop();
    }

    public static AntiBarrelSubData getData(UUID uuid) {
        if (server != null) {
            return getData(uuid,server);
        }
        throw new RuntimeException("Tried to get data on the client?");
    }
    public static AntiBarrelSubData getData(UUID uuid,MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(AntiBarrelSubData::loadStatic,AntiBarrelSubData::new,
                        NABBA.MODID+"/"+uuid.toString());
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    private void commands(RegisterCommandsEvent e) {
        ModCommands.register(e.getDispatcher());
    }

    public static void registerObj(RegisterEvent e) {
        superRegister(e, ModBlocks.class, Registries.BLOCK, Block.class);
        superRegister(e, ModItems.class,Registries.ITEM, Item.class);
        superRegister(e, ModBlockEntityTypes.class,Registries.BLOCK_ENTITY_TYPE, BlockEntityType.class);
        superRegister(e, ModMenuTypes.class,Registries.MENU, MenuType.class);
        superRegister(e, ModRecipeSerializers.class,Registries.RECIPE_SERIALIZER, RecipeSerializer.class);
    }

    public static <T> void superRegister(RegisterEvent e, Class<?> clazz, ResourceKey<? extends  Registry<T>> resourceKey, Class<?> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    e.register(resourceKey,new ResourceLocation(NABBA.MODID,field.getName().toLowerCase(Locale.ROOT)),() -> (T)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private static final int DELAY = 5;
    private void blockBreak(PlayerInteractEvent.LeftClickBlock e) {
        Level level = e.getLevel();
        BlockPos pos = e.getPos();
        Player player = e.getEntity();
        boolean crouch = player.isCrouching();
        BlockState state = level.getBlockState(pos);

        //cancel block breaking and vend if not crouching
        if (state.getBlock() instanceof SingleSlotBarrelBlock) {
            if (!crouch) {
                //attack is not called in creative, the block is simply broken
                if (!level.isClientSide && player.getAbilities().instabuild) {
                    state.attack(level, pos, player);
                    e.setCanceled(true);
                }
            }
        }
    }

    private void breakSpeed(PlayerEvent.BreakSpeed e) {
        Player player = e.getEntity();
        boolean crouch = player.isCrouching();
        BlockState state = e.getState();
        //cancel block breaking if not crouching
        if (state.getBlock() instanceof SingleSlotBarrelBlock) {
            if (!crouch) {
                e.setCanceled(true);
            }
        }
    }

    private void setupSync(PlayerContainerEvent.Open e) {
        NABBA.onContainerOpen(e.getContainer(),e.getEntity());
    }


   // public static final ClientConfig CLIENT;
   // public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ServerCfg SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
      //  final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
       // CLIENT_SPEC = specPair.getRight();
       // CLIENT = specPair.getLeft();
        final Pair<ServerCfg, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerCfg::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    public static class ServerCfg {
        public static ForgeConfigSpec.IntValue better_barrel_base_storage;
        public static ForgeConfigSpec.IntValue fluid_barrel_base_storage;
        public static ForgeConfigSpec.IntValue anti_barrel_base_storage;
        public static ForgeConfigSpec.IntValue barrel_interface_storage;

        public ServerCfg(ForgeConfigSpec.Builder builder) {
            builder.push("server");
            better_barrel_base_storage = builder.
                    comment("Base storage of better barrel in stacks")
                    .defineInRange("better_barrel_base_storage", 64, 1, Integer.MAX_VALUE);
            fluid_barrel_base_storage = builder.
                    comment("Base storage of fluid barrel in stacks")
                    .defineInRange("fluid_barrel_base_storage", 16, 1, Integer.MAX_VALUE);
            anti_barrel_base_storage = builder.
                    comment("Base storage of anti barrel in item count")
                    .defineInRange("anti_barrel_base_storage", 256, 1, Integer.MAX_VALUE);
            barrel_interface_storage = builder.
                    comment("Number of barrels the barrel interface can hold")
                    .defineInRange("barrel_interface_storage", 4096, 1, 65536);
        }
    }
}