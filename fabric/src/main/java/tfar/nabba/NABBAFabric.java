package tfar.nabba;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.nabba.block.SingleSlotBarrelBlock;
import tfar.nabba.blockentity.BetterBarrelBlockEntity;
import tfar.nabba.init.*;
import tfar.nabba.inventory.FakeSlotSynchronizer;
import tfar.nabba.menu.SearchableMenu;
import tfar.nabba.net.PacketHandler;
import tfar.nabba.world.AntiBarrelSubData;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

import static tfar.nabba.NABBA.MODID;


public class NABBAFabric implements ModInitializer {
    // Directly reference a slf4j logger



    @Override
    public void onInitialize() {

        PacketHandler.registerMessages();
        addGameEvents();
        ItemStorage.SIDED.registerForBlockEntity(BetterBarrelBlockEntity::getStorage,ModBlockEntityTypes.BETTER_BARREL);
        FluidStorage.SIDED.registerForBlockEntity((myTank, direction) -> myTank.getFluidStorage(), ModBlockEntityTypes.FLUID_BARREL);

        Constants.LOG.info("Hello Fabric world!");
        NABBA.init();
    }

    private void addGameEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(NABBA::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> NABBA.onServerStop());

        PlayerBlockBreakEvents.BEFORE.register(this::blockBreak);

   //     MinecraftForge.EVENT_BUS.addListener(this::setupSync);
    }

    public static AntiBarrelSubData getData(UUID uuid,MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(AntiBarrelSubData::loadStatic,AntiBarrelSubData::new,
                        MODID+"/"+uuid.toString());
    }

    public static void registerObj() {
        superRegister(ModBlocks.class, BuiltInRegistries.BLOCK, Block.class);
        superRegister(ModItems.class,BuiltInRegistries.ITEM, Item.class);
        superRegister(ModBlockEntityTypes.class,BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockEntityType.class);
        superRegister(ModMenuTypes.class,BuiltInRegistries.MENU, MenuType.class);
        superRegister(ModRecipeSerializers.class,BuiltInRegistries.RECIPE_SERIALIZER, RecipeSerializer.class);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,new ResourceLocation(MODID,"tab"),ModItems.tab);
    }

    public static <T> void superRegister(Class<?> clazz, Registry<T> resourceKey, Class<?> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    Registry.register(resourceKey,new ResourceLocation(MODID,field.getName().toLowerCase(Locale.ROOT)),(T)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private static final int DELAY = 5;

    //returning false stops the block from breaking
    private boolean blockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        boolean crouch = player.isCrouching();

        //cancel block breaking and vend if not crouching
        if (state.getBlock() instanceof SingleSlotBarrelBlock) {
            if (!crouch) {
                //attack is not called in creative, the block is simply broken
                if (!level.isClientSide && player.getAbilities().instabuild) {
                    state.attack(level, pos, player);
                    return false;
                }
            }
        }
        return true;
    }

   /* private void breakSpeed(PlayerEvent.BreakSpeed e) {
        Player player = e.getEntity();
        boolean crouch = player.isCrouching();
        BlockState state = e.getState();
        //cancel block breaking if not crouching
        if (state.getBlock() instanceof SingleSlotBarrelBlock) {
            if (!crouch) {
                e.setCanceled(true);
            }
        }
    }*/

   /* private void setupSync(PlayerContainerEvent.Open e) {
        AbstractContainerMenu menu = e.getContainer();
        if (menu instanceof SearchableMenu<?> searchableItemMenu && e.getEntity() instanceof ServerPlayer player) {
            searchableItemMenu.setFakeSlotSynchronizer(new FakeSlotSynchronizer(player));
        }
    }*/


    public static class ServerCfg {
        public static int better_barrel_base_storage = 64;
        public static int fluid_barrel_base_storage = 16;
        public static int anti_barrel_base_storage = 256;
        public static int barrel_interface_storage = 4096;

       /* public ServerCfg(ForgeConfigSpec.Builder builder) {
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
        }*/
    }
}