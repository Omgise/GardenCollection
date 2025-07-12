package com.jaquadro.minecraft.gardencontainers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.jaquadro.minecraft.gardencontainers.block.BlockDecorativePot;
import com.jaquadro.minecraft.gardencontainers.block.tile.TileEntityDecorativePot;
import com.jaquadro.minecraft.gardencontainers.core.ClientProxy;
import com.jaquadro.minecraft.gardencore.block.support.Slot2Profile;
import com.jaquadro.minecraft.gardencore.client.renderer.support.ModularBoxRenderer;
import com.jaquadro.minecraft.gardencore.util.RenderHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class DecorativePotRenderer implements ISimpleBlockRenderingHandler {

    private float[] baseColor = new float[3];
    private float[] activeSubstrateColor = new float[3];

    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockDecorativePot)) return;

        renderInventoryBlock((BlockDecorativePot) block, metadata, modelId, renderer);
    }

    private void renderInventoryBlock(BlockDecorativePot block, int metadata, int modelId, RenderBlocks renderer) {
        IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 1, metadata);

        float unit = .0625f;
        boxRenderer.setIcon(icon);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-.5f, -.5f, -.5f);

        boxRenderer.renderBox(
            null,
            block,
            0,
            0,
            0,
            0,
            14 * unit,
            0,
            1,
            1,
            1,
            0,
            ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_YPOS);
        boxRenderer.renderBox(
            null,
            block,
            0,
            0,
            0,
            1 * unit,
            8 * unit,
            1 * unit,
            15 * unit,
            16 * unit,
            15 * unit,
            0,
            ModularBoxRenderer.CUT_YPOS);

        boxRenderer.renderSolidBox(null, block, 0, 0, 0, 3 * unit, 6 * unit, 3 * unit, 13 * unit, 8 * unit, 13 * unit);
        boxRenderer.renderSolidBox(null, block, 0, 0, 0, 5 * unit, 3 * unit, 5 * unit, 11 * unit, 6 * unit, 11 * unit);
        boxRenderer.renderSolidBox(null, block, 0, 0, 0, 2 * unit, 0 * unit, 2 * unit, 14 * unit, 3 * unit, 14 * unit);

        GL11.glTranslatef(.5f, .5f, .5f);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (!(block instanceof BlockDecorativePot)) return false;

        return renderWorldBlock(world, x, y, z, (BlockDecorativePot) block, modelId, renderer);
    }

    private boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockDecorativePot block, int modelId,
        RenderBlocks renderer) {
        int data = world.getBlockMetadata(x, y, z);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        RenderHelper.calculateBaseColor(baseColor, block.colorMultiplier(world, x, y, z));

        float unit = .0625f;

        for (int i = 0; i < 6; i++) boxRenderer.setIcon(renderer.getBlockIconFromSideAndMetadata(block, i, data), i);

        boxRenderer.setColor(baseColor);
        boxRenderer.renderBox(
            world,
            block,
            x,
            y,
            z,
            0,
            14 * unit,
            0,
            1,
            1,
            1,
            0,
            ModularBoxRenderer.CUT_YNEG | ModularBoxRenderer.CUT_YPOS);
        boxRenderer.setScaledColor(baseColor, .9375f);
        boxRenderer.renderBox(
            world,
            block,
            x,
            y,
            z,
            1 * unit,
            8 * unit,
            1 * unit,
            15 * unit,
            16 * unit,
            15 * unit,
            0,
            ModularBoxRenderer.CUT_YPOS);

        boxRenderer.setScaledExteriorColor(baseColor, .875f);
        boxRenderer.renderSolidBox(world, block, x, y, z, 3 * unit, 6 * unit, 3 * unit, 13 * unit, 8 * unit, 13 * unit);
        boxRenderer.setScaledExteriorColor(baseColor, .8125f);
        boxRenderer.renderSolidBox(world, block, x, y, z, 5 * unit, 3 * unit, 5 * unit, 11 * unit, 6 * unit, 11 * unit);
        boxRenderer.setScaledExteriorColor(baseColor, .9375f);
        boxRenderer.setScaledExteriorColor(baseColor, .75f, 1);
        boxRenderer.renderSolidBox(world, block, x, y, z, 2 * unit, 0 * unit, 2 * unit, 14 * unit, 3 * unit, 14 * unit);

        TileEntityDecorativePot te = block.getTileEntity(world, x, y, z);
        ItemStack substrateItem = block.getGardenSubstrate(world, x, y, z, Slot2Profile.SLOT_CENTER);
        if (te != null && substrateItem != null && substrateItem.getItem() instanceof ItemBlock) {
            Block substrate = Block.getBlockFromItem(substrateItem.getItem());
            IIcon substrateIcon = renderer.getBlockIconFromSideAndMetadata(substrate, 1, substrateItem.getItemDamage());

            int color = substrate.colorMultiplier(world, x, y, z);
            if (color == Blocks.grass.colorMultiplier(world, x, y, z))
                color = ColorizerGrass.getGrassColor(te.getBiomeTemperature(), te.getBiomeHumidity());

            RenderHelper.calculateBaseColor(activeSubstrateColor, color);
            RenderHelper.scaleColor(activeSubstrateColor, activeSubstrateColor, .8f);

            RenderHelper.instance.setRenderBounds(.0625f, 0, .0625f, 1f - .0625f, 1f - .0625f, 1f - .0625f);
            RenderHelper.instance.renderFace(
                RenderHelper.YPOS,
                world,
                block,
                x,
                y,
                z,
                substrateIcon,
                activeSubstrateColor[0],
                activeSubstrateColor[1],
                activeSubstrateColor[2]);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.decorativePotRenderID;
    }
}
