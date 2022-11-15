package tfar.nabba.datagen.providers.assets;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.nabba.NABBA;
import tfar.nabba.block.AbstractBarrelBlock;
import tfar.nabba.init.ModBlocks;
import tfar.nabba.init.ModItems;
import tfar.nabba.item.BarrelFrameUpgradeItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator,  ExistingFileHelper existingFileHelper) {
        super(generator, NABBA.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        makeOneLayerItem(ModItems.BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/storage_upgrade"));
        makeOneLayerItem(ModItems.x4_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x4_storage_upgrade"));
        makeOneLayerItem(ModItems.x16_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x16_storage_upgrade"));
        makeOneLayerItem(ModItems.x64_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x64_storage_upgrade"));
        makeOneLayerItem(ModItems.x256_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x256_storage_upgrade"));
        makeOneLayerItem(ModItems.x1024_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x1024_storage_upgrade"));
        makeOneLayerItem(ModItems.INFINITE_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/infinite_storage_upgrade"));

        makeOneLayerItem(ModItems.ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/storage_upgrade"));
        makeOneLayerItem(ModItems.x4_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/x4_storage_upgrade"));
        makeOneLayerItem(ModItems.x16_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/x16_storage_upgrade"));
        makeOneLayerItem(ModItems.x64_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/x64_storage_upgrade"));
        makeOneLayerItem(ModItems.x256_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/x256_storage_upgrade"));
        makeOneLayerItem(ModItems.x1024_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/x1024_storage_upgrade"));
        makeOneLayerItem(ModItems.INFINITE_ANTI_BARREL_STORAGE_UPGRADE,modLoc("anti/infinite_storage_upgrade"));
/*
        makeOneLayerItem(ModItems.BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/storage_upgrade"));
        makeOneLayerItem(ModItems.x4_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x4_storage_upgrade"));
        makeOneLayerItem(ModItems.x16_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x16_storage_upgrade"));
        makeOneLayerItem(ModItems.x64_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x64_storage_upgrade"));
        makeOneLayerItem(ModItems.x256_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x256_storage_upgrade"));
        makeOneLayerItem(ModItems.x1024_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/x1024_storage_upgrade"));
        makeOneLayerItem(ModItems.INFINITE_BETTER_BARREL_STORAGE_UPGRADE,modLoc("better/infinite_storage_upgrade"));*/

        makeOneLayerItem(ModItems.INFINITE_VENDING_UPGRADE);

        makeOneLayerItem(ModItems.VOID_UPGRADE);

        makeOneLayerItem(ModItems.PICKUP_1x1_UPGRADE);
        makeOneLayerItem(ModItems.PICKUP_3x3_UPGRADE);
        makeOneLayerItem(ModItems.PICKUP_9x9_UPGRADE);

        makeOneLayerItem(ModItems.HIDE_KEY);
        makeOneLayerItem(ModItems.LOCK_KEY);
        makeOneLayerItem(ModItems.VANITY_KEY);
        makeOneLayerItem(ModItems.KEY_RING);
        makeOneLayerItem(ModItems.CONTROLLER_KEY);

        for (Block block : Registry.BLOCK) {
            if (block instanceof AbstractBarrelBlock) {
                makeSimpleBlockItem(block.asItem());
            }
        }

        makeSimpleBlockItem(ModBlocks.CONTROLLER.asItem());

        for (Item item : Registry.ITEM) {
            if (item instanceof BarrelFrameUpgradeItem) {
                registerUpgrade(item);
            }
        }
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

    //wood_to_iron_frame_upgrade
    protected void registerUpgrade(Item item) {
        String name = Registry.ITEM.getKey(item).getPath();
        registerUpgrade(name);
    }
    protected void registerUpgrade(String name) {
        String[] strings = name.split("_");
        getBuilder(name)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0","item/frame_upgrade/from_"+strings[0])
                .texture("layer1","item/frame_upgrade/to_"+strings[2]);
    }

}
