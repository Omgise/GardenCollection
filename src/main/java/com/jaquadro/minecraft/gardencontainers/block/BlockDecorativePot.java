package com.jaquadro.minecraft.gardencontainers.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.jaquadro.minecraft.gardencontainers.block.tile.TileEntityDecorativePot;
import com.jaquadro.minecraft.gardencontainers.core.ClientProxy;
import com.jaquadro.minecraft.gardencore.api.plant.PlantItem;
import com.jaquadro.minecraft.gardencore.api.plant.PlantSize;
import com.jaquadro.minecraft.gardencore.api.plant.PlantType;
import com.jaquadro.minecraft.gardencore.block.BlockGardenContainer;
import com.jaquadro.minecraft.gardencore.block.support.*;
import com.jaquadro.minecraft.gardencore.core.ModBlocks;
import com.jaquadro.minecraft.gardencore.core.ModCreativeTabs;

public class BlockDecorativePot extends BlockGardenContainer {

    private class LocalSlotProfile extends Slot2Profile {

        public LocalSlotProfile(Slot[] slots) {
            super(slots);
        }

        @Override
        public float getPlantOffsetY(IBlockAccess blockAccess, int x, int y, int z, int slot) {
            return -.0625f;
        }
    }

    public BlockDecorativePot(String blockName) {
        super(blockName, Material.rock);

        setCreativeTab(ModCreativeTabs.tabGardenCore);
        setHardness(.5f);
        setStepSound(Block.soundTypeStone);

        connectionProfile = new BasicConnectionProfile();
        slotShareProfile = new SlotShare0Profile();

        PlantType[] commonType = new PlantType[] { PlantType.GROUND, PlantType.AQUATIC, PlantType.AQUATIC_EMERGENT };
        PlantSize[] allSize = new PlantSize[] { PlantSize.FULL, PlantSize.LARGE, PlantSize.MEDIUM, PlantSize.SMALL };

        slotProfile = new LocalSlotProfile(
            new BasicSlotProfile.Slot[] { new BasicSlotProfile.Slot(Slot2Profile.SLOT_CENTER, commonType, allSize),
                new BasicSlotProfile.Slot(
                    Slot2Profile.SLOT_COVER,
                    new PlantType[] { PlantType.GROUND_COVER },
                    allSize), });
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    protected boolean isValidSubstrate(World world, int x, int y, int z, int slot, ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == null) return false;

        if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.netherrack) return true;

        return super.isValidSubstrate(world, x, y, z, slot, itemStack);
    }

    @Override
    public int getDefaultSlot() {
        return Slot2Profile.SLOT_CENTER;
    }

    @Override
    protected int getSlot(World world, int x, int y, int z, EntityPlayer player, float hitX, float hitY, float hitZ) {
        return Slot2Profile.SLOT_CENTER;
    }

    @Override
    protected int getEmptySlotForPlant(World world, int x, int y, int z, EntityPlayer player, PlantItem plant) {
        if (plant.getPlantTypeClass() == PlantType.GROUND_COVER) return Slot2Profile.SLOT_COVER;

        return Slot2Profile.SLOT_CENTER;
    }

    @Override
    public int getRenderType() {
        return ClientProxy.decorativePotRenderID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public TileEntityDecorativePot getTileEntity(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return (te != null && te instanceof TileEntityDecorativePot) ? (TileEntityDecorativePot) te : null;
    }

    @Override
    public TileEntityDecorativePot createNewTileEntity(World var1, int var2) {
        return new TileEntityDecorativePot();
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list,
        Entity colliding) {
        setBlockBounds(0, .5f, 0, 1, 1, 1);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, colliding);

        setBlockBounds(.1875f, 0, .1875f, 1f - .1875f, .5f, 1f - .1875f);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, colliding);

        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected boolean applyItemToGarden(World world, int x, int y, int z, EntityPlayer player, ItemStack itemStack,
        float hitX, float hitY, float hitZ, boolean hitValid) {
        ItemStack item = (itemStack == null) ? player.inventory.getCurrentItem() : itemStack;

        if (item != null && item.getItem() == Items.flint_and_steel) {
            ItemStack substrate = getGardenSubstrate(world, x, y, z, Slot2Profile.SLOT_CENTER);
            if (substrate != null && substrate.getItem() == Item.getItemFromBlock(Blocks.netherrack)) {
                if (world.isAirBlock(x, y + 1, z)) {
                    world.playSoundEffect(x + .5, y + .5, z + .5, "fire.ignite", 1, world.rand.nextFloat() * .4f + .8f);
                    world.setBlock(x, y + 1, z, ModBlocks.smallFire);

                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                }

                item.damageItem(1, player);
                return true;
            }
        }

        return super.applyItemToGarden(world, x, y, z, player, itemStack, hitX, hitY, hitZ, hitValid);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int data) {
        if (isSconceLit(world, x, y, z)) {
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        }

        super.breakBlock(world, x, y, z, block, data);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);

        if (block == ModBlocks.smallFire && world.getBlock(x, y + 1, z) != ModBlocks.smallFire) {
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        }
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return (side == 1 && isSconceLit(world, x, y, z)) ? 15 : 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return isSconceLit(world, x, y, z) ? 15 : 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    private boolean isSconceLit(IBlockAccess world, int x, int y, int z) {
        ItemStack substrate = getGardenSubstrate(world, x, y, z, Slot2Profile.SLOT_CENTER);
        if (substrate != null && substrate.getItem() == Item.getItemFromBlock(Blocks.netherrack))
            return world.getBlock(x, y + 1, z) == ModBlocks.smallFire;

        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List blockList) {
        for (int i = 0; i < 3; i++) blockList.add(new ItemStack(item, 1, i));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 2) side = 1;
        return Blocks.quartz_block.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (world.getBlockMetadata(x, y, z) == 2) side = 1;
        return Blocks.quartz_block.getIcon(world, x, y, z, side);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {}
}
