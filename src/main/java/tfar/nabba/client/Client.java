package tfar.nabba.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.nabba.api.SearchableFluidHandler;
import tfar.nabba.api.SearchableItemHandler;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.blockentity.AntiBarrelBlockEntity;
import tfar.nabba.blockentity.BarrelInterfaceBlockEntity;
import tfar.nabba.blockentity.ControllerBlockEntity;
import tfar.nabba.client.renderer.AntiBarrelRenderer;
import tfar.nabba.client.renderer.BetterBarrelRenderer;
import tfar.nabba.client.renderer.FluidBarrelRenderer;
import tfar.nabba.client.screen.SearchableFluidScreen;
import tfar.nabba.client.screen.SearchableItemScreen;
import tfar.nabba.client.screen.VanityKeyScreen;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.ClientBetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.ClientFluidBarrelTooltip;
import tfar.nabba.inventory.tooltip.FluidBarrelTooltip;
import tfar.nabba.item.NetworkVisualizerItem;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.net.server.C2SScrollKeyPacket;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.ClientUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

    public static void setup(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.addListener(Client::scroll);
        MinecraftForge.EVENT_BUS.addListener(Client::onTexturePostStitch);
        MenuScreens.register(ModMenuTypes.ANTI_BARREL, (SearchableItemMenu<AntiBarrelBlockEntity.AntiBarrelInventory> pMenu, Inventory pPlayerInventory, Component pTitle) -> new SearchableItemScreen<>(pMenu, pPlayerInventory, pTitle));
        MenuScreens.register(ModMenuTypes.VANITY_KEY, VanityKeyScreen::new);
        MenuScreens.register(ModMenuTypes.ITEM_CONTROLLER_KEY, (SearchableItemMenu<SearchableItemHandler> pMenu, Inventory pPlayerInventory, Component pTitle) -> new SearchableItemScreen<>(pMenu, pPlayerInventory, pTitle));
        MenuScreens.register(ModMenuTypes.FLUID_CONTROLLER_KEY, (SearchableFluidMenu<SearchableFluidHandler> pMenu, Inventory pPlayerInventory, Component pTitle) -> new SearchableFluidScreen<>(pMenu, pPlayerInventory, pTitle));
        MenuScreens.register(ModMenuTypes.BARREL_INTERFACE, (SearchableItemMenu<BarrelInterfaceBlockEntity.BarrelInterfaceItemHandler> pMenu, Inventory pPlayerInventory, Component pTitle) -> new SearchableItemScreen<>(pMenu, pPlayerInventory, pTitle));

        BlockEntityRenderers.register(ModBlockEntityTypes.BETTER_BARREL, BetterBarrelRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.ANTI_BARREL, AntiBarrelRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.FLUID_BARREL, FluidBarrelRenderer::new);

        for (Block block : ModBlocks.getBlocks()) {
            if (block instanceof AbstractBarrelBlock) {
                setCutOutRenderLayer(block);
            }
        }
    }

    private static ItemStack cache = ItemStack.EMPTY;
    public static void renderNetwork(PoseStack pPoseStack, Camera pCamera, Matrix4f pProjectionMatrix) {
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();

        if (stack.getItem() instanceof NetworkVisualizerItem) {

            if (stack != cache) {
                cache = stack;
                NetworkInfo.decode(stack);
            }
            NetworkInfo.render(pCamera);
        }
    }

    public static class NetworkInfo {
        public static BlockPos controller;
        public static Map<BarrelType, List<BlockPos>> barrels = new HashMap<>();
        public static List<BlockPos> proxies = new ArrayList<>();

        public static void clear() {
            controller = null;
            barrels.clear();
            proxies.clear();
        }

        public static void decode(ItemStack stack) {
            clear();
            if (stack.getTagElement(ControllerBlockEntity.NET_INFO) != null) {
                CompoundTag tag = stack.getTagElement(ControllerBlockEntity.NET_INFO);
                int[] cont = tag.getIntArray("controller");
                controller = new BlockPos(cont[0],cont[1],cont[2]);

                CompoundTag tag1 = tag.getCompound("barrels");

                for (String s : tag1.getAllKeys()) {
                    ListTag listTag = tag1.getList(s, Tag.TAG_COMPOUND);
                    BarrelType type = BarrelType.valueOf(s);
                    List<BlockPos> bType = new ArrayList<>();
                    for (Tag tag2 : listTag) {
                        int[] barrelPos = ((CompoundTag)tag2).getIntArray("pos");
                        bType.add(new BlockPos(barrelPos[0],barrelPos[1],barrelPos[2]));
                    }
                    barrels.put(type,bType);
                }

                ListTag listTag = tag.getList("proxies",Tag.TAG_COMPOUND);
                for (Tag tag2 : listTag) {
                    int[] proxyPos = ((CompoundTag)tag2).getIntArray("pos");
                    proxies.add(new BlockPos(proxyPos[0],proxyPos[1],proxyPos[2]));
                }
            }
        }

        public static void render(Camera camera) {
            if (controller != null) {
                ClientUtils.renderBox(camera, controller, 0xffffffff);
                for (BarrelType type : barrels.keySet()) {
                    for (BlockPos pos : barrels.get(type)) {
                        ClientUtils.renderBox(camera, pos, type.color);
                        ClientUtils.renderLineSetup(camera,pos.getX() + .5,pos.getY() + .5,pos.getZ()+.5,
                                controller.getX() + .5,controller.getY() +.5,controller.getZ() + .5, type.color);
                    }
                }

                for (BlockPos pos : proxies) {
                    ClientUtils.renderBox(camera, pos, 0xffffff00);
                    ClientUtils.renderLineSetup(camera,pos.getX() + .5,pos.getY() + .5,pos.getZ()+.5,
                            controller.getX() + .5,controller.getY() +.5,controller.getZ() + .5, 0xffffff00);
                }
            }
        }
    }

    public static void onTexturePostStitch(final TextureStitchEvent.Post event) {
        //noinspection deprecation
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            FluidSpriteCache.invalidateSpriteCache();
        }
    }

    public static void tooltipC(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(BetterBarrelTooltip.class, Client::tooltipImage);
        e.register(FluidBarrelTooltip.class,Client::tooltipImage);
    }

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof BetterBarrelTooltip dankTooltip) {
            return new ClientBetterBarrelTooltip(dankTooltip);
        } else if (data instanceof FluidBarrelTooltip fluidBarrelTooltip) {
            return new ClientFluidBarrelTooltip(fluidBarrelTooltip);
        }
        return null;
    }

    private static void setCutOutRenderLayer(Block block) {
        ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
    }

    private static void scroll(InputEvent.MouseScrollingEvent e) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().is(ModItems.KEY_RING) && Minecraft.getInstance().player.isCrouching()) {
            C2SScrollKeyPacket.send(e.getScrollDelta() > 0);
            e.setCanceled(true);
        }
    }
}
