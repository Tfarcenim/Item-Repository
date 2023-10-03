package tfar.nabba.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.ClientBetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.ClientFluidBarrelTooltip;
import tfar.nabba.inventory.tooltip.FluidBarrelTooltip;
import tfar.nabba.item.NetworkVisualizerItem;
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.net.client.ClientPacketHandler;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.NetworkInfo;
import tfar.nabba.util.ShapeMerger;

import java.util.*;

public class Client implements ClientModInitializer {

    //public static void setup(FMLClientSetupEvent e) {
    //    MinecraftForge.EVENT_BUS.addListener(Client::onTexturePostStitch);
     //   MinecraftForge.EVENT_BUS.addListener(Client::worldLast);

   // }

  //  public static void worldLast(RenderLevelLastEvent e) {
     //   renderNetwork(e.getPoseStack(),Minecraft.getInstance().gameRenderer.getMainCamera());
   // }

    private static ItemStack cache = ItemStack.EMPTY;
    public static void renderNetwork(PoseStack pPoseStack, Camera pCamera) {
        ItemStack stack = Minecraft.getInstance().player.getMainHandItem();

        if (stack.getItem() instanceof NetworkVisualizerItem) {
            if (!ItemStack.isSameItemSameTags(stack,cache)) {
                cache = stack.copy();
                NetworkInfo.decode(stack);
            }
            NetworkInfo.render(pPoseStack,pCamera);
        }
    }

    @Override
    public void onInitializeClient() {
        ClientPacketHandler.registerPackets();
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
            TooltipComponentCallback.EVENT.register(Client::tooltipImage);

    }

   // public static void onTexturePostStitch(final TextureStitchEvent.Post event) {
        //noinspection deprecation
  //      if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
  //          FluidSpriteCache.invalidateSpriteCache();
  //      }
    //}

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof BetterBarrelTooltip betterBarrelTooltip) {
            return new ClientBetterBarrelTooltip(betterBarrelTooltip);
        } else if (data instanceof FluidBarrelTooltip fluidBarrelTooltip) {
            return new ClientFluidBarrelTooltip(fluidBarrelTooltip);
        }
        return null;
    }

    private static void setCutOutRenderLayer(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
    }
}
