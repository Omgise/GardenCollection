package com.jaquadro.minecraft.gardentrees;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import com.jaquadro.minecraft.gardencore.api.StrippedThinLogRegistry;
import com.jaquadro.minecraft.gardenstuff.Tags;
import com.jaquadro.minecraft.gardentrees.config.ConfigManager;
import com.jaquadro.minecraft.gardentrees.core.*;
import com.jaquadro.minecraft.gardentrees.core.handlers.ForgeEventHandler;
import com.jaquadro.minecraft.gardentrees.core.handlers.FuelHandler;
import com.jaquadro.minecraft.gardentrees.world.gen.feature.WorldGenCandelilla;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
    modid = GardenTrees.MOD_ID,
    name = GardenTrees.MOD_NAME,
    version = GardenTrees.MOD_VERSION,
    dependencies = "required-after:GardenCore")
public class GardenTrees {

    public static final String MOD_ID = "GardenTrees";
    public static final String MOD_NAME = "Garden Trees";
    public static final String MOD_VERSION = Tags.VERSION;
    static final String SOURCE_PATH = "com.jaquadro.minecraft.gardentrees.";

    public static final ModIntegration integration = new ModIntegration();
    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();
    public static final StrippedThinLogRegistry stripping = new StrippedThinLogRegistry();

    public static ConfigManager config;

    @Mod.Instance(MOD_ID)
    public static GardenTrees instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new ConfigManager(new File(event.getModConfigurationDirectory(), "GardenStuff/" + MOD_ID + ".cfg"));

        blocks.init();
        items.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
        integration.init();
        if (Loader.isModLoaded("etfuturum")) {
            stripping.init();
        }

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new ForgeEventHandler());

        GameRegistry.registerFuelHandler(new FuelHandler());

        if (config.generateCandelilla)
            GameRegistry.registerWorldGenerator(new WorldGenCandelilla(ModBlocks.candelilla), 10);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        config.postInit();
        integration.postInit();
        recipes.init();
    }
}
