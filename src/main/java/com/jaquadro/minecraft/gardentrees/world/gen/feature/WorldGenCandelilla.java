package com.jaquadro.minecraft.gardentrees.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenCandelilla extends WorldGenerator implements IWorldGenerator {

    Block plantBlock;

    public WorldGenCandelilla(Block block) {
        plantBlock = block;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        do {
            Block block = world.getBlock(x, y, z);
            if (!(block.isLeaves(world, x, y, z) || block.isAir(world, x, y, z))) break;
            y--;
        } while (y > 0);

        int range = 5;
        for (int l = 0; l < 32; ++l) {
            int x1 = x + rand.nextInt(range) - rand.nextInt(range);
            int y1 = y + rand.nextInt(4) - rand.nextInt(4);
            int z1 = z + rand.nextInt(range) - rand.nextInt(range);
            int level = 1 + rand.nextInt(7);

            if (world.isAirBlock(x1, y1, z1) && (!world.provider.hasNoSky || y1 < 255)
                && plantBlock.canBlockStay(world, x1, y1, z1)) world.setBlock(x1, y1, z1, plantBlock, level, 2);
        }

        return true;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        int x = chunkX * 16;
        int z = chunkZ * 16;

        BiomeGenBase biome = world.getWorldChunkManager()
            .getBiomeGenAt(x, z);
        if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.COLD)
            || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.NETHER)
            || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WET)
            || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WASTELAND)
            || BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SNOWY)) return;

        if (!BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY)) return;

        if (random.nextInt(10) > 0) return;

        generate(world, random, x, world.getActualHeight() - 1, z);
    }
}
