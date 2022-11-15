package tfar.nabba.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.nabba.client.renderer.AntiBarrelRenderer;
import tfar.nabba.client.renderer.BetterBarrelRenderer;
import tfar.nabba.client.screen.AntiBarrelScreen;
import tfar.nabba.client.screen.ControllerKeyScreen;
import tfar.nabba.client.screen.VanityKeyScreen;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.inventory.tooltip.BetterBarrelTooltip;
import tfar.nabba.inventory.tooltip.ClientBetterBarrelTooltip;
import tfar.nabba.net.C2SScrollKeyPacket;

public class Client {

    public static void setup(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.addListener(Client::scroll);
        MenuScreens.register(ModMenuTypes.ANTI_BARREL, AntiBarrelScreen::new);
        MenuScreens.register(ModMenuTypes.VANITY_KEY, VanityKeyScreen::new);
        MenuScreens.register(ModMenuTypes.CONTROLLER_KEY, ControllerKeyScreen::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.BETTER_BARREL, BetterBarrelRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.ANTI_BARREL, AntiBarrelRenderer::new);

        setRenderLayer(ModBlocks.BETTER_BARREL);
        setRenderLayer(ModBlocks.STONE_BETTER_BARREL);
        setRenderLayer(ModBlocks.COPPER_BETTER_BARREL);
        setRenderLayer(ModBlocks.IRON_BETTER_BARREL);
        setRenderLayer(ModBlocks.LAPIS_BETTER_BARREL);
        setRenderLayer(ModBlocks.GOLD_BETTER_BARREL);
        setRenderLayer(ModBlocks.DIAMOND_BETTER_BARREL);
        setRenderLayer(ModBlocks.EMERALD_BETTER_BARREL);
        setRenderLayer(ModBlocks.NETHERITE_BETTER_BARREL);
        setRenderLayer(ModBlocks.CREATIVE_BETTER_BARREL);

        setRenderLayer(ModBlocks.ANTI_BARREL);
        setRenderLayer(ModBlocks.STONE_ANTI_BARREL);
        setRenderLayer(ModBlocks.COPPER_ANTI_BARREL);
        setRenderLayer(ModBlocks.IRON_ANTI_BARREL);
        setRenderLayer(ModBlocks.LAPIS_ANTI_BARREL);
        setRenderLayer(ModBlocks.GOLD_ANTI_BARREL);
        setRenderLayer(ModBlocks.DIAMOND_ANTI_BARREL);
        setRenderLayer(ModBlocks.EMERALD_ANTI_BARREL);
        setRenderLayer(ModBlocks.NETHERITE_ANTI_BARREL);
        setRenderLayer(ModBlocks.CREATIVE_ANTI_BARREL);
    }

    public static void tooltipC(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(BetterBarrelTooltip.class, Client::tooltipImage);
    }

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof BetterBarrelTooltip dankTooltip) {
            return new ClientBetterBarrelTooltip(dankTooltip);
        }
        return null;
    }

    private static void setRenderLayer(Block block) {
        ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
    }

    private static void scroll(InputEvent.MouseScrollingEvent e) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().is(ModItems.KEY_RING) && Minecraft.getInstance().player.isCrouching()) {
            C2SScrollKeyPacket.send(e.getScrollDelta() > 0);
            e.setCanceled(true);
        }
    }
}
