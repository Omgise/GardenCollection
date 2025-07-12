package com.jaquadro.minecraft.gardentrees.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import com.jaquadro.minecraft.gardentrees.GardenTrees;
import com.jaquadro.minecraft.gardentrees.core.ClientProxy;
import com.jaquadro.minecraft.gardentrees.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockIvy extends Block implements IShearable {

    @SideOnly(Side.CLIENT)
    private IIcon[] textures;

    public BlockIvy(String name) {
        super(Material.vine);

        setBlockName(name);
        setTickRandomly(true);
        setStepSound(Block.soundTypeGrass);
        setCreativeTab(ModCreativeTabs.tabGardenTrees);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public int getRenderType() {
        return ClientProxy.ivyRenderID;
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
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        float unit = .0625f;
        int meta = blockAccess.getBlockMetadata(x, y, z);

        float xMin = 1;
        float yMin = 1;
        float zMin = 1;
        float xMax = 0;
        float yMax = 0;
        float zMax = 0;

        if ((meta & 2) != 0) {
            xMin = 0;
            yMin = 0;
            zMin = 0;
            xMax = Math.max(xMax, unit);
            yMax = 1;
            zMax = 1;
        }

        if ((meta & 8) != 0) {
            xMin = Math.min(xMin, 1 - unit);
            yMin = 0;
            zMin = 0;
            xMax = 1;
            yMax = 1;
            zMax = 1;
        }

        if ((meta & 4) != 0) {
            xMin = 0;
            yMin = 0;
            zMin = 0;
            xMax = 1;
            yMax = 1;
            zMax = Math.max(zMax, unit);
        }

        if ((meta & 1) != 0) {
            xMin = 0;
            yMin = 0;
            zMin = Math.min(zMin, 1 - unit);
            xMax = 1;
            yMax = 1;
            zMax = 1;
        }

        setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        int opSide = side - (side % 2);
        switch (side) {
            case 2:
                return canPlaceOnBlock(world, x, y, z + 1, opSide);
            case 3:
                return canPlaceOnBlock(world, x, y, z - 1, opSide);
            case 4:
                return canPlaceOnBlock(world, x + 1, y, z, opSide);
            case 5:
                return canPlaceOnBlock(world, x - 1, y, z, opSide);
            default:
                return false;
        }
    }

    private boolean canPlaceOnBlock(World world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        return block.getMaterial()
            .blocksMovement() && block.isSideSolid(world, x, y, z, ForgeDirection.getOrientation(side));
    }

    @Override
    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @Override
    public int getRenderColor(int meta) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        return blockAccess.getBiomeGenForCoords(x, z)
            .getBiomeFoliageColor(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote && !isBlockStateValid(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote && world.rand.nextInt(2) == 0) {
            int limit = 9;

            worldSearch: for (int dx = x - 4; dx <= x + 4; dx++) {
                for (int dz = z - 4; dz <= z + 4; dz++) {
                    for (int dy = y - 1; dy <= y + 1; dy++) {
                        if (world.getBlock(dx, dy, dz) == this) {
                            limit--;

                            if (limit <= 0) break worldSearch;
                        }
                    }
                }
            }

            int meta = world.getBlockMetadata(x, y, z);
            int dir = world.rand.nextInt(6);
            int facingDir = Direction.facingToDirection[dir];

            if (dir == 1 && y < 255 && world.isAirBlock(x, y + 1, z)) {
                if (limit <= 0) return;

                int chance = world.rand.nextInt(16) & meta;
                if (chance > 0) {
                    for (int i = 0; i <= 3; i++) {
                        int opSide = Direction.directionToFacing[Direction.rotateOpposite[i]];
                        if (!canPlaceOnBlock(world, x + Direction.offsetX[i], y + 1, z + Direction.offsetZ[i], opSide))
                            chance &= ~(1 << i);
                    }

                    if (chance > 0) world.setBlock(x, y + 1, z, this, chance, 2);
                }
            } else {
                if (dir >= 2 && dir <= 5 && (meta & 1 << facingDir) == 0) {
                    if (limit <= 4) return;

                    Block block = world.getBlock(x + Direction.offsetX[facingDir], y, z + Direction.offsetZ[facingDir]);
                    int opSide = Direction.directionToFacing[Direction.rotateOpposite[facingDir]];

                    if (block.getMaterial() == Material.air) {
                        int dirRight = Direction.rotateRight[facingDir];
                        int dirLeft = Direction.rotateLeft[facingDir];

                        int opSideRight = Direction.directionToFacing[Direction.rotateOpposite[dirRight]];
                        int opSideLeft = Direction.directionToFacing[Direction.rotateOpposite[dirLeft]];

                        if ((meta & 1 << dirRight) != 0 && canPlaceOnBlock(
                            world,
                            x + Direction.offsetX[facingDir] + Direction.offsetX[dirRight],
                            y,
                            z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirRight],
                            opSideRight))
                            world.setBlock(
                                x + Direction.offsetX[facingDir],
                                y,
                                z + Direction.offsetZ[facingDir],
                                this,
                                1 << dirRight,
                                2);
                        else if ((meta & 1 << dirLeft) != 0 && canPlaceOnBlock(
                            world,
                            x + Direction.offsetX[facingDir] + Direction.offsetX[dirLeft],
                            y,
                            z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirLeft],
                            opSideLeft))
                            world.setBlock(
                                x + Direction.offsetX[facingDir],
                                y,
                                z + Direction.offsetZ[facingDir],
                                this,
                                1 << dirLeft,
                                2);
                        else if ((meta & 1 << dirRight) != 0
                            && world.isAirBlock(
                                x + Direction.offsetX[facingDir] + Direction.offsetX[dirRight],
                                y,
                                z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirRight])
                            && canPlaceOnBlock(
                                world,
                                x + Direction.offsetX[dirRight],
                                y,
                                z + Direction.offsetZ[dirRight],
                                opSide))
                            world.setBlock(
                                x + Direction.offsetX[facingDir] + Direction.offsetX[dirRight],
                                y,
                                z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirRight],
                                this,
                                1 << (facingDir + 2 & 3),
                                2);
                        else if ((meta & 1 << dirLeft) != 0
                            && world.isAirBlock(
                                x + Direction.offsetX[facingDir] + Direction.offsetX[dirLeft],
                                y,
                                z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirLeft])
                            && canPlaceOnBlock(
                                world,
                                x + Direction.offsetX[dirLeft],
                                y,
                                z + Direction.offsetZ[dirLeft],
                                opSide))
                            world.setBlock(
                                x + Direction.offsetX[facingDir] + Direction.offsetX[dirLeft],
                                y,
                                z + Direction.offsetZ[facingDir] + Direction.offsetZ[dirLeft],
                                this,
                                1 << (facingDir + 2 & 3),
                                2);
                    } else if (canPlaceOnBlock(
                        world,
                        x + Direction.offsetX[facingDir],
                        y,
                        z + Direction.offsetZ[facingDir],
                        opSide)) world.setBlockMetadataWithNotify(x, y, z, meta | 1 << facingDir, 2);
                }
            }
        }
    }

    private boolean isBlockStateValid(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int mask = meta;

        if (meta > 0) {
            for (int i = 0; i <= 3; i++) {
                int bit = 1 << i;
                int opSide = Direction.directionToFacing[Direction.rotateOpposite[i]];
                if ((meta & bit) != 0
                    && !canPlaceOnBlock(world, x + Direction.offsetX[i], y, z + Direction.offsetZ[i], opSide)
                    && (world.getBlock(x, y + 1, z) != this || (world.getBlockMetadata(x, y + 1, z) & bit) == 0))
                    mask &= ~bit;
            }
        }

        if (mask == 0) return false;

        if (mask != meta) world.setBlockMetadataWithNotify(x, y, z, mask, 2);

        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        int bit = 0;
        switch (side) {
            case 2:
                bit = 1;
                break;
            case 3:
                bit = 4;
                break;
            case 4:
                bit = 8;
                break;
            case 5:
                bit = 2;
                break;
        }

        return bit != 0 ? bit : meta;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1));
        return ret;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return textures[0];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFull() {
        return textures[7];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        boolean nTop = blockAccess.getBlock(x, y + 1, z) == this;
        boolean nLeft = false;
        boolean nRight = false;

        int meta = blockAccess.getBlockMetadata(x, y, z);
        switch (side) {
            case 2:
                nLeft = (meta & 8) != 0
                    || (blockAccess.getBlock(x + 1, y, z) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z) & 1) != 0)
                    || (blockAccess.getBlock(x + 1, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z + 1) & 2) != 0);
                nRight = (meta & 2) != 0
                    || (blockAccess.getBlock(x - 1, y, z) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z) & 1) != 0)
                    || (blockAccess.getBlock(x - 1, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z + 1) & 8) != 0);
                break;
            case 3:
                nLeft = (meta & 2) != 0
                    || (blockAccess.getBlock(x - 1, y, z) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z) & 4) != 0)
                    || (blockAccess.getBlock(x - 1, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z - 1) & 8) != 0);
                nRight = (meta & 8) != 0
                    || (blockAccess.getBlock(x + 1, y, z) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z) & 4) != 0)
                    || (blockAccess.getBlock(x + 1, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z - 1) & 2) != 0);
                break;
            case 4:
                nLeft = (meta & 4) != 0
                    || (blockAccess.getBlock(x, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x, y, z - 1) & 8) != 0)
                    || (blockAccess.getBlock(x + 1, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z - 1) & 1) != 0);
                nRight = (meta & 1) != 0
                    || (blockAccess.getBlock(x, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x, y, z + 1) & 8) != 0)
                    || (blockAccess.getBlock(x + 1, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x + 1, y, z + 1) & 4) != 0);
                break;
            case 5:
                nLeft = (meta & 1) != 0
                    || (blockAccess.getBlock(x, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x, y, z + 1) & 2) != 0)
                    || (blockAccess.getBlock(x - 1, y, z + 1) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z + 1) & 4) != 0);
                nRight = (meta & 4) != 0
                    || (blockAccess.getBlock(x, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x, y, z - 1) & 2) != 0)
                    || (blockAccess.getBlock(x - 1, y, z - 1) == this
                        && (blockAccess.getBlockMetadata(x - 1, y, z - 1) & 1) != 0);
                break;
        }

        if (nTop) {
            if (nLeft && nRight) return textures[4];
            else if (nLeft) return textures[3];
            else if (nRight) return textures[2];
            else return textures[1];
        } else {
            if (nLeft && nRight) return textures[7];
            else if (nLeft) return textures[6];
            else if (nRight) return textures[5];
            else return textures[0];
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        textures = new IIcon[8];
        for (int i = 0; i < textures.length; i++) textures[i] = register.registerIcon(GardenTrees.MOD_ID + ":ivy" + i);
    }
}
