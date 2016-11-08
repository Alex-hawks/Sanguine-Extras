package io.github.alex_hawks.SanguineExtras.common;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.api.registry.RitualRegistry;
import io.github.alex_hawks.SanguineExtras.api.sigil.Interdiction;
import io.github.alex_hawks.SanguineExtras.common.items.baubles.LiquidSummonHandler$;
import io.github.alex_hawks.SanguineExtras.common.items.baubles.StoneSummonHandler$;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.HandlerEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.MsgEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MultipartStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.WRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.AMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.WMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.rituals.advanced.Forge;
import io.github.alex_hawks.SanguineExtras.common.rituals.advanced.Test;
import io.github.alex_hawks.SanguineExtras.common.rituals.basic.Spawn;
import io.github.alex_hawks.SanguineExtras.common.util.MultipartFactory;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.PushHandlerTamable;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.entity.IEntityOwnable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(dependencies = "after:BloodMagic", modid = Constants.MetaData.MOD_ID, name = Constants.MetaData.NAME, useMetadata = false, modLanguage = "java")
//modLanguage is the language this file is in
public class SanguineExtras
{
    public static SimpleNetworkWrapper networkWrapper;

    @SidedProxy(clientSide = "io.github.alex_hawks.SanguineExtras.client.ClientProxy", serverSide = "io.github.alex_hawks.SanguineExtras.common.CommonProxy", modId = Constants.MetaData.MOD_ID)
    public static CommonProxy proxy;

    @Mod.Instance(Constants.MetaData.MOD_ID)
    public static SanguineExtras INSTANCE;

    private static Configuration config;
    public static int pathfindIterations;
    public static int rebuildSigilCost;
    public static boolean trappableBossMobs;
    public static boolean spawnableBossMobs;
    public static int spawnLpPerHealth;
    public static int spawnMaxEntities;
    public static float interdictionRange;
    public static boolean opsCanBreakWardedBlocks;


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e)
    {
        config = new Configuration(e.getSuggestedConfigurationFile());
        readConfig();

        Items.initItems();
        Blocks.initBlocks();

        proxy.registerClientStuff();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent e)
    {
        RitualRegistry.registerRitual(new Spawn(), Spawn.name, spawnLpPerHealth >= 0 && spawnMaxEntities > 0);
        RitualRegistry.registerRitual(new Test(), Test.name, BloodMagic.isDev());
        RitualRegistry.registerRitual(new Forge(), Forge.name(), true);

        MinecraftForge.EVENT_BUS.register(new AMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WRSHandler());

        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MetaData.MOD_ID);
        networkWrapper.registerMessage(HandlerEntityMotion.class, MsgEntityMotion.class, 1, Side.CLIENT);

        if (Loader.isModLoaded(Constants.MetaData.MCMP_ID))
        {
            MultipartRegistry.registerPartFactory(new MultipartFactory(), MultipartStone.NAME().toString());
        }
        if (Loader.isModLoaded(Constants.MetaData.BAUBLES_ID))
        {
            MinecraftForge.EVENT_BUS.register(LiquidSummonHandler$.MODULE$);
            MinecraftForge.EVENT_BUS.register(StoneSummonHandler$.MODULE$);
        }
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent e)
    {
        Interdiction.addToPushConditional(IEntityOwnable.class, new PushHandlerTamable());
        //Interdiction.addToPushConditional(EnergyBlastProjectile.class, new PushHandlerEnergyBlastProjectile());
        Recipe.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);
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
        spawnLpPerHealth = config2.getInt("Base Spawner LP Cost Per Health", Configuration.CATEGORY_GENERAL, 150, 118, 15000, "This is the lowest that the cost can go. If you don't use reagents, it drains double to spawn one mob, and this is per half heart that the mob has at max, plus what health it is missing as well");
        spawnMaxEntities = config2.getInt("Max Entities in Spawner", Configuration.CATEGORY_GENERAL, 20, 0, 50, "The maximum number of entities inside the spawner's area of effect, before it gives up on spawning more. It only counts what it is currenly spawning. Divide by 10 if the mob in question is a boss. Set to 0 to disable the ritual.");
        interdictionRange = config2.getFloat("Interdiction Range", Configuration.CATEGORY_GENERAL, 5.0f, 0.5f, 10.0f, "Entities will be pushed away from you if they are closer than this many blocks, calculated using pythagorean theorem");
        opsCanBreakWardedBlocks = config2.getBoolean("Ops can break warded blocks", Configuration.CATEGORY_GENERAL, false, "set this to true if you want ops to be able to break the warded blocks when most others can't.");

        if (config2.hasChanged())
            config2.save();
    }
}
