package com.jaquadro.minecraft.gardenstuff;

import com.jaquadro.minecraft.gardenstuff.core.*;
import com.jaquadro.minecraft.gardenstuff.core.handlers.GuiHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(
    modid = GardenStuff.MOD_ID,
    name = GardenStuff.MOD_NAME,
    version = GardenStuff.MOD_VERSION,
    dependencies = "required-after:GardenCore")
public class GardenStuff {

    public static final String MOD_ID = "GardenStuff";
    public static final String MOD_NAME = "Garden Stuff";
    public static final String MOD_VERSION = Tags.VERSION;
    static final String SOURCE_PATH = "com.jaquadro.minecraft.gardenstuff.";

    @Mod.Instance(MOD_ID)
    public static GardenStuff instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.ServerProxy")
    public static CommonProxy proxy;

    public static final ModIntegration integration = new ModIntegration();
    public static final ModBlocks blocks = new ModBlocks();
    public static final ModItems items = new ModItems();
    public static final ModRecipes recipes = new ModRecipes();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        blocks.init();
        items.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        integration.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        recipes.init();
        integration.postInit();
    }

}
