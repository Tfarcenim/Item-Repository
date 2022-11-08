package tfar.nabba.datagen.providers;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.nabba.NABBA;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator,  ExistingFileHelper existingFileHelper) {
        super(generator, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        makeOneLayerItem(ModItems.STORAGE_UPGRADE);
        makeOneLayerItem(ModItems.x4_STORAGE_UPGRADE);

        makeSimpleBlockItem(ModBlocks.BETTER_BARREL.asItem());
        makeSimpleBlockItem(ModBlocks.COPPER_BETTER_BARREL.asItem());
        makeSimpleBlockItem(ModBlocks.STONE_BETTER_BARREL.asItem());
        makeSimpleBlockItem(ModBlocks.IRON_BETTER_BARREL.asItem());

    }


    protected void makeSimpleBlockItem(Item item, ResourceLocation loc) {
        getBuilder(Registry.ITEM.getKey(item).toString())
                .parent(getExistingFile(loc));
    }

    protected void makeSimpleBlockItem(Item item) {
        makeSimpleBlockItem(item, new ResourceLocation(NABBA.MODID, "block/" + Registry.ITEM.getKey(item).getPath()));
    }


    protected void makeOneLayerItem(Item item, ResourceLocation texture) {
        String path = Registry.ITEM.getKey(item).getPath();
        if (existingFileHelper.exists(new ResourceLocation(texture.getNamespace(), "item/" + texture.getPath())
                , PackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(path).parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", new ResourceLocation(texture.getNamespace(), "item/" + texture.getPath()));
        } else {
            System.out.println("no texture for " + item + " found, skipping");
        }
    }

    protected void makeOneLayerItem(Item item) {
        ResourceLocation texture = Registry.ITEM.getKey(item);
        makeOneLayerItem(item, texture);
    }
}
