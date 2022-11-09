package tfar.nabba.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.nabba.client.screen.AntiBarrelScreen;
import tfar.nabba.client.screen.VanityKeyScreen;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.init.ModMenuTypes;
import tfar.nabba.menu.VanityKeyMenu;
import tfar.nabba.net.C2SScrollKeyPacket;

public class Client {

    public static void setup(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.addListener(Client::scroll);
        MenuScreens.register(ModMenuTypes.ANTI_BARREL, AntiBarrelScreen::new);
        MenuScreens.register(ModMenuTypes.VANITY_KEY, VanityKeyScreen::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.BETTER_BARREL,BetterBarrelRenderer::new);
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
