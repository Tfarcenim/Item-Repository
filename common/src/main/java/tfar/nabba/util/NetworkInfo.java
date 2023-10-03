package tfar.nabba.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tfar.nabba.client.CommonClientUtils;
import tfar.nabba.client.Line;
import tfar.nabba.util.BarrelType;
import tfar.nabba.util.NBTKeys;
import tfar.nabba.util.ShapeMerger;

import java.util.*;

public class NetworkInfo {
    public static BlockPos controller;
    public static Map<BarrelType, List<BlockPos>> barrels = new HashMap<>();

    private static Map<BarrelType, BlockPos> cachedPoses = null;

    private static Map<BarrelType, List<Line>> cachedEdges = null;
    public static List<BlockPos> proxies = new ArrayList<>();

    public static void clear() {
        controller = null;
        barrels.clear();
        proxies.clear();
        cachedEdges = null;
    }

    public static void decode(ItemStack stack) {
        clear();
        if (stack.getTagElement(NBTKeys.NetworkInfo.name()) != null) {
            CompoundTag tag = stack.getTagElement(NBTKeys.NetworkInfo.name());
            int[] cont = tag.getIntArray("controller");
            controller = new BlockPos(cont[0], cont[1], cont[2]);

            CompoundTag tag1 = tag.getCompound("barrels");

            for (String s : tag1.getAllKeys()) {
                ListTag listTag = tag1.getList(s, Tag.TAG_COMPOUND);
                BarrelType type = BarrelType.valueOf(s);
                List<BlockPos> bType = new ArrayList<>();
                for (Tag tag2 : listTag) {
                    int[] barrelPos = ((CompoundTag) tag2).getIntArray("pos");
                    bType.add(new BlockPos(barrelPos[0], barrelPos[1], barrelPos[2]));
                }
                barrels.put(type, bType);
            }

            ListTag listTag = tag.getList("proxies", Tag.TAG_COMPOUND);
            for (Tag tag2 : listTag) {
                int[] proxyPos = ((CompoundTag) tag2).getIntArray("pos");
                proxies.add(new BlockPos(proxyPos[0], proxyPos[1], proxyPos[2]));
            }
        }
        updateEdges();
    }

    public static void updateEdges() {
        if (cachedEdges != null) {
            return;
        }

        cachedEdges = new HashMap<>();
        cachedPoses = new HashMap<>();

        for (BarrelType type : barrels.keySet()) {

            List<BlockPos> shapeBlocks = barrels.get(type);

            if (shapeBlocks.isEmpty()) {
                continue;
            }

            cachedPoses.put(type, shapeBlocks.get(0));

            List<Line> lines = new ArrayList<>();

            Collection<VoxelShape> shapes = new HashSet<>();
            for (AABB aabb : ShapeMerger.merge(shapeBlocks, cachedPoses.get(type))) {
                shapes.add(Shapes.create(aabb));
            }

            orShapes(shapes).forAllEdges((x1, y1, z1, x2, y2, z2) -> lines.add(new Line(x1, y1, z1, x2, y2, z2)));
            cachedEdges.put(type, lines);
        }
    }

    public static void render(PoseStack poseStack, Camera camera) {
        if (controller != null) {
            CommonClientUtils.renderBox(camera, controller, 0xffffffff);
            if (cachedPoses == null || cachedEdges == null || cachedEdges.isEmpty()) {
                return;
            }

            for (BarrelType type : barrels.keySet()) {
                BlockPos cachedPos = cachedPoses.get(type);

                if (cachedPos == null) continue;

                for (Line edge : cachedEdges.get(type)) {
                    CommonClientUtils.renderLineSetup(camera, cachedPos.getX() + edge.x1, cachedPos.getY() + edge.y1, cachedPos.getZ() + edge.z1,
                            cachedPos.getX() + edge.x2, cachedPos.getY() + edge.y2, cachedPos.getZ() + edge.z2, type.color);
                }
                CommonClientUtils.renderLineSetup(camera, cachedPos.getX() + .5, cachedPos.getY() + .5, cachedPos.getZ() + .5,
                        controller.getX() + .5, controller.getY() + .5, controller.getZ() + .5, type.color);
            }

            for (BlockPos pos : proxies) {
                CommonClientUtils.renderBox(camera, pos, 0xffffff00);
                CommonClientUtils.renderLineSetup(camera, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                        controller.getX() + .5, controller.getY() + .5, controller.getZ() + .5, 0xffffff00);
            }
        }
    }

    static VoxelShape orShapes(Collection<VoxelShape> shapes) {
        VoxelShape combinedShape = Shapes.empty();
        for (VoxelShape shape : shapes) {
            combinedShape = Shapes.joinUnoptimized(combinedShape, shape, BooleanOp.OR);
        }
        return combinedShape;
    }
}
