package tfar.nabba.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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
import tfar.nabba.menu.SearchableFluidMenu;
import tfar.nabba.menu.SearchableItemMenu;
import tfar.nabba.net.server.C2SScrollKeyPacket;

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
