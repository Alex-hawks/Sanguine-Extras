package io.github.alex_hawks.SanguineExtras.common;

import io.github.alex_hawks.SanguineExtras.common.network.chat_handler.HandlerDisplayChat;
import io.github.alex_hawks.SanguineExtras.common.network.chat_handler.MsgDisplayChat;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.advanced_master.AMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded.WRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded_master.WMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.rituals.basic.Spawn;
import io.github.alex_hawks.SanguineExtras.common.rituals.interactable.TestInteractableRitual;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(dependencies = "after:AWWayofTime", modid = Constants.MetaData.MOD_ID, name = Constants.MetaData.NAME, useMetadata = false, version = Constants.MetaData.VERSION)
public class SanguineExtras
{
	public static SimpleNetworkWrapper networkWrapper;
	
    private static Configuration config;
    public static int pathfindIterations;
    public static int rebuildSigilCost;
    public static boolean trappableBossMobs;
    public static boolean spawnableBossMobs;
    public static int spawnLpPerHealth;
    public static int spawnMaxEntities;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent e)
    {
        config = new Configuration(e.getSuggestedConfigurationFile());
        readConfig();
    }

    @EventHandler
    public static void init(FMLInitializationEvent e)
    {
        ModItems.initItems();
        ModBlocks.initBlocks();
        Rituals.registerRitual("SE001Spawner", 1, 250000, new Spawn(), "Ritual of Re-creation");
        Rituals.registerRitual("SE002TEST", 1, 0, new TestInteractableRitual(), "Superior Ritual of Testing");
        
        MinecraftForge.EVENT_BUS.register(new AMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WRSHandler());
        
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MetaData.MOD_ID);
        networkWrapper.registerMessage(HandlerDisplayChat.class, MsgDisplayChat.class, 0, Side.CLIENT);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent e)
    {
        
    }
    
    public static void readConfig()
    {
        config.load();
        readConfig(config);
    }

    public static void readConfig(Configuration config2)
    {
        pathfindIterations = config2.getInt("Sigil Of Rebuilding pathfinding iterations", Configuration.CATEGORY_GENERAL, 5, 1, 50, "The number of times that the sigil will iterate when used to replace more than one block at a time. Beware: this can add up quickly...");
        rebuildSigilCost = config2.getInt("Sigil Of Rebuilding LP Cost", Configuration.CATEGORY_GENERAL, 25, 0, Integer.MAX_VALUE, "The LP cost of replacing one block using the Sigil Of Rebuilding");
        trappableBossMobs = config2.getBoolean("Trappable Boss Mobs", Configuration.CATEGORY_GENERAL, true, "Set to \"true\" if you want to be able to trap boss mobs at 10 times the LP cost");
        spawnableBossMobs = config2.getBoolean("Spawnable Boss Mobs", Configuration.CATEGORY_GENERAL, false, "Set to \"true\" if you want to be able to spawn boss mobs at 10 times the LP cost");
        spawnLpPerHealth = config2.getInt("Base Spawner LP Cost Per Health", Configuration.CATEGORY_GENERAL, 150, 118, 15000, "This is the lowest that the cost can go, if you don't use reagents, it drains double to spawn one mob, and this is per half heart that the mob has at max, plus what it is missing as well");
        spawnMaxEntities = config2.getInt("Max Entities in Spawner", Configuration.CATEGORY_GENERAL, 20, 1, 50, "The maximum number of entities inside the spawner's area of effect, before it gives up on spawning more. It only counts what it is currenly spawning. Divide by 10 if the mob in question is a boss");       
        if (config2.hasChanged())
            config2.save();
    }
}
