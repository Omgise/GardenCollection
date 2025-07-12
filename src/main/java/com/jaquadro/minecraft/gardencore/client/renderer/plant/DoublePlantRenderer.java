package com.jaquadro.minecraft.gardencore.client.renderer.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.jaquadro.minecraft.gardencore.api.IPlantMetaResolver;
import com.jaquadro.minecraft.gardencore.api.IPlantRenderer;
import com.jaquadro.minecraft.gardencore.api.PlantRegistry;
import com.jaquadro.minecraft.gardencore.util.RenderHelper;

public class DoublePlantRenderer implements IPlantRenderer {

    @Override
    public void render(IBlockAccess world, int x, int y, int z, RenderBlocks renderer, Block block, int meta,
        int height, AxisAlignedBB[] bounds) {
        if (!(block instanceof BlockDoublePlant)) return;

        BlockDoublePlant doublePlant = (BlockDoublePlant) block;

        IPlantMetaResolver resolver = PlantRegistry.instance()
            .getPlantMetaResolver(block, meta);
        if (resolver != null) meta = resolver.getPlantSectionMeta(block, meta, height);

        IIcon iicon = getIcon(block, world, meta);

        if (height == 1) {
            for (AxisAlignedBB bound : bounds) {
                RenderHelper.instance
                    .setRenderBounds(bound.minX, bound.minY, bound.minZ, bound.maxX, bound.maxY, bound.maxZ);
                RenderHelper.instance.drawCrossedSquaresBounded(iicon, x, y, z, 1);
            }
        } else {
            AxisAlignedBB bound = bounds[0];
            for (AxisAlignedBB slice : bounds) {
                if (slice.maxY > bound.maxY) bound = slice;
            }

            RenderHelper.instance.setRenderBounds(bound.minX, 0, bound.minZ, bound.maxX, 1, bound.maxZ);
            RenderHelper.instance.drawCrossedSquaresBounded(iicon, x, y, z, 1);
        }
    }

    public IIcon getIcon(Block block, IBlockAccess blockAccess, int meta) {
        boolean isTopHalf = BlockDoublePlant.func_149887_c(meta);
        int baseMeta = BlockDoublePlant.func_149890_d(meta);

        return Blocks.double_plant.func_149888_a(isTopHalf, baseMeta);
    }
}
