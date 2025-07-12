package com.jaquadro.minecraft.gardenstuff.integration.lantern;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import com.jaquadro.minecraft.gardenapi.api.component.StandardLanternSource;
import com.jaquadro.minecraft.gardencore.util.RenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumcraftCandleSource extends StandardLanternSource {

    private Block blockCandle;

    public ThaumcraftCandleSource(Block blockCandle) {
        super(
            new LanternSourceInfo("thaumcraftCandle", Item.getItemFromBlock(blockCandle), blockCandle.getLightValue()));
        this.blockCandle = blockCandle;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticle(World world, int x, int y, int z, Random rand, int meta) {
        world.spawnParticle("flame", x + .5f, y + .7f, z + .5f, 0.0D, 0.0D, 0.0D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(RenderBlocks renderer, int x, int y, int z, int meta, int pass) {
        renderer.renderBlockAllFaces(blockCandle, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderItem(RenderBlocks renderer, IItemRenderer.ItemRenderType renderType, int meta) {
        RenderHelper renderHelper = RenderHelper.instance;

        renderHelper.setRenderBounds(0.375, 0, 0.375, 0.625, 0.5, 0.625);
        renderHelper.renderFace(RenderHelper.ZNEG, null, blockCandle, blockCandle.getIcon(2, meta), meta);
        renderHelper.renderFace(RenderHelper.ZPOS, null, blockCandle, blockCandle.getIcon(3, meta), meta);
        renderHelper.renderFace(RenderHelper.XNEG, null, blockCandle, blockCandle.getIcon(4, meta), meta);
        renderHelper.renderFace(RenderHelper.XPOS, null, blockCandle, blockCandle.getIcon(5, meta), meta);
        renderHelper.renderFace(RenderHelper.YPOS, null, blockCandle, blockCandle.getIcon(1, meta), meta);

        renderHelper.setRenderBounds(0.46875, 0, 0.46875, 0.5325, 1, 0.53125);
        renderHelper.renderFace(RenderHelper.ZNEG, null, Blocks.torch, Blocks.torch.getIcon(2, 0), meta);
        renderHelper.renderFace(RenderHelper.ZPOS, null, Blocks.torch, Blocks.torch.getIcon(3, 0), meta);
        renderHelper.renderFace(RenderHelper.XNEG, null, Blocks.torch, Blocks.torch.getIcon(4, 0), meta);
        renderHelper.renderFace(RenderHelper.XPOS, null, Blocks.torch, Blocks.torch.getIcon(5, 0), meta);
        renderHelper.setRenderBounds(0.46875, 0, 0.46875, 0.5325, 0.625, 0.5325);
        renderHelper.renderFace(RenderHelper.YPOS, null, Blocks.torch, Blocks.torch.getIcon(1, 0), meta);
    }
}
