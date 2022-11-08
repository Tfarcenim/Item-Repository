package tfar.nabba.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModMenuTypes;

public class Client {

    public static void setup(FMLClientSetupEvent e) {
        MenuScreens.register(ModMenuTypes.REPOSITORY,RepositoryScreen::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.BETTER_BARREL,BetterBarrelRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BETTER_BARREL, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STONE_BETTER_BARREL, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COPPER_BETTER_BARREL, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_BETTER_BARREL, RenderType.cutoutMipped());

    }
}
