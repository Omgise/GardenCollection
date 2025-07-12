package com.jaquadro.minecraft.gardencore.api.plant;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;

import com.jaquadro.minecraft.gardencore.api.PlantRegistry;

public class PlantItem {

    private ItemStack plantSourceItem;
    private Block plantBlock;
    private int plantMeta;
    private IPlantInfo plantInfo;

    private PlantItem(ItemStack plantSourceItem, Block plantBlock, int plantMeta) {
        this.plantSourceItem = plantSourceItem;
        this.plantBlock = plantBlock;
        this.plantMeta = plantMeta;
        this.plantInfo = PlantRegistry.instance()
            .getPlantInfoOrDefault(plantBlock, plantMeta);
    }

    public static PlantItem getForItem(IBlockAccess blockAccess, ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == null) return null;

        IPlantable plantable = PlantRegistry.getPlantable(itemStack);
        if (plantable == null) return getForItem(itemStack);

        Block block = plantable.getPlant(blockAccess, 0, -1, 0);
        if (block == null) return getForItem(itemStack);

        int meta = plantable.getPlantMetadata(blockAccess, 0, -1, 0);
        if (meta == 0) meta = itemStack.getItemDamage();

        return new PlantItem(itemStack, block, meta);
    }

    public static PlantItem getForItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == null) return null;

        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == null || !(block instanceof IPlantable)) return null;

        return new PlantItem(itemStack, block, itemStack.getItemDamage());
    }

    public ItemStack getPlantSourceItem() {
        return plantSourceItem;
    }

    public Block getPlantBlock() {
        return plantBlock;
    }

    public int getPlantMeta() {
        return plantMeta;
    }

    public IPlantInfo getPlantInfo() {
        return plantInfo;
    }

    public PlantType getPlantTypeClass() {
        return plantInfo.getPlantTypeClass(plantBlock, plantMeta);
    }

    public PlantSize getPlantSizeClass() {
        return plantInfo.getPlantSizeClass(plantBlock, plantMeta);
    }

    public int getPlantMaxHeight() {
        return plantInfo.getPlantMaxHeight(plantBlock, plantMeta);
    }

    public int getPlantHeight() {
        return plantInfo.getPlantHeight(plantBlock, plantMeta);
    }

    public int getPlantSectionMeta(int section) {
        return plantInfo.getPlantSectionMeta(plantBlock, plantMeta, section);
    }
}
