package com.jaquadro.minecraft.gardentrees.integration;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

import com.jaquadro.minecraft.gardencore.api.SaplingRegistry;
import com.jaquadro.minecraft.gardencore.util.UniqueMetaIdentifier;
import com.jaquadro.minecraft.gardentrees.world.gen.OrnamentalTreeFactory;
import com.jaquadro.minecraft.gardentrees.world.gen.OrnamentalTreeRegistry;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class TwilightForestIntegration {

    public static final String MOD_ID = "TwilightForest";

    public static void init() {
        if (!Loader.isModLoaded(MOD_ID)) return;

        Map<String, int[]> saplingBank1 = new HashMap<String, int[]>();
        saplingBank1.put("small_oak", new int[] { 0, 7, 8, 9 });
        saplingBank1.put("large_oak", new int[] { 4, 5 });
        saplingBank1.put("small_canopy", new int[] { 1, 2, 3, 6 });

        Map<Item, Map<String, int[]>> banks = new HashMap<Item, Map<String, int[]>>();
        banks.put(Item.getItemFromBlock(GameRegistry.findBlock(MOD_ID, "tile.TFSapling")), saplingBank1);

        SaplingRegistry saplingReg = SaplingRegistry.instance();

        for (Map.Entry<Item, Map<String, int[]>> entry : banks.entrySet()) {
            Item sapling = entry.getKey();

            for (Map.Entry<String, int[]> bankEntry : entry.getValue()
                .entrySet()) {
                OrnamentalTreeFactory factory = OrnamentalTreeRegistry.getTree(bankEntry.getKey());
                if (factory == null) continue;

                for (int i : bankEntry.getValue()) {
                    UniqueMetaIdentifier woodBlock = saplingReg.getWoodForSapling(sapling, i);
                    UniqueMetaIdentifier leafBlock = saplingReg.getLeavesForSapling(sapling, i);
                    if (woodBlock == null || leafBlock == null) continue;

                    saplingReg.putExtendedData(
                        sapling,
                        i,
                        "sm_generator",
                        factory.create(woodBlock.getBlock(), woodBlock.meta, leafBlock.getBlock(), leafBlock.meta));
                }
            }
        }
    }
}
