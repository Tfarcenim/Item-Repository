package tfar.nabba;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.blockentity.FluidBarrelBlockEntity;
import tfar.nabba.client.Client;
import tfar.nabba.command.ModCommands;
import tfar.nabba.datagen.ModDatagen;
import tfar.nabba.init.*;
import tfar.nabba.inventory.FakeSlotSynchronizer;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.mixin.MinecraftServerAccess;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.world.AntiBarrelSubData;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NABBA.MODID)
public class NABBA {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "nabba";

    public static NABBA instance;
    public MinecraftServer server;

    public NABBA() {
        // Register the setup method for modloading
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::registerObj);
        bus.addListener(ModDatagen::start);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(Client::setup);
            bus.addListener(Client::tooltipC);
        }
        addGameEvents();
        instance = this;
    }

    private void addGameEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
        MinecraftForge.EVENT_BUS.addListener(this::leftClick);
        MinecraftForge.EVENT_BUS.addListener(this::setupSync);
    }

    public void onServerStarting(ServerStartingEvent event) {
        instance.server = event.getServer();
        LevelStorageSource.LevelStorageAccess storageSource = ((MinecraftServerAccess) instance.server).getStorageSource();
        File file = storageSource.getDimensionPath(instance.server.getLevel(Level.OVERWORLD).dimension())
                .resolve("data/nabba").toFile();
        file.mkdirs();
    }

    public void onServerStopped(ServerStoppedEvent event) {
        instance.server = null;
    }

    public AntiBarrelSubData getData(UUID uuid) {
        if (server != null) {
            return getData(uuid,server);
        }
        throw new RuntimeException("Tried to get data on the client?");
    }
    public AntiBarrelSubData getData(UUID uuid,MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(AntiBarrelSubData::loadStatic,AntiBarrelSubData::new,
                        MODID+"/"+uuid.toString());
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    private void commands(RegisterCommandsEvent e) {
        ModCommands.register(e.getDispatcher());
    }

    private void registerObj(RegisterEvent e) {
        superRegister(e, ModBlocks.class,Registry.BLOCK_REGISTRY, Block.class);
        superRegister(e, ModItems.class,Registry.ITEM_REGISTRY, Item.class);
        superRegister(e, ModBlockEntityTypes.class,Registry.BLOCK_ENTITY_TYPE_REGISTRY, BlockEntityType.class);
        superRegister(e, ModMenuTypes.class,Registry.MENU_REGISTRY, MenuType.class);
        superRegister(e, ModRecipeSerializers.class,Registry.RECIPE_SERIALIZER_REGISTRY, RecipeSerializer.class);
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
        Level level = e.getLevel();
        BlockPos pos = e.getPos();
        Player player = e.getEntity();
        boolean crouch = e.getEntity().isCrouching();

        //cancel block breaking and vend if not crouching
        if (!crouch) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BetterBarrelBlockEntity betterBarrelBlockEntity) {
                if (!level.isClientSide) {
                ItemStack stack = betterBarrelBlockEntity.tryRemoveItem();
                ItemHandlerHelper.giveItemToPlayer(e.getEntity(), stack);
            }
                e.setCanceled(true);
            } else if (blockEntity instanceof FluidBarrelBlockEntity fluidBarrelBlockEntity) {
                if (!level.isClientSide) {
                    FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(e.getItemStack(), fluidBarrelBlockEntity.getFluidHandler(),
                            new InvWrapper(player.getInventory()), Integer.MAX_VALUE, player, true);
                    if (fluidActionResult.isSuccess()) {
                        player.setItemInHand(e.getHand(), fluidActionResult.getResult());
                    }
                }
                e.setCanceled(true);
            }
        }
    }

    private void setupSync(PlayerContainerEvent.Open e) {
        AbstractContainerMenu menu = e.getContainer();
        if (menu instanceof SearchableMenu searchableItemMenu && e.getEntity() instanceof ServerPlayer player) {
            searchableItemMenu.setFakeSlotSynchronizer(new FakeSlotSynchronizer(player));
        }
    }
}