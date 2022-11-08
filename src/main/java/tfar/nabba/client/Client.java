package tfar.nabba.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.nabba.init.ModBlockEntityTypes;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModMenuTypes;

public class Client {

    public static void setup(FMLClientSetupEvent e) {
        MenuScreens.register(ModMenuTypes.REPOSITORY,RepositoryScreen::new);
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
}
