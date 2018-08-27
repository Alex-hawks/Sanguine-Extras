package io.github.alex_hawks.SanguineExtras.common;

import java.io.File;

import org.apache.logging.log4j.Logger;

import WayofTime.bloodmagic.api.BloodMagicPlugin;
import WayofTime.bloodmagic.api.IBloodMagicAPI;
import WayofTime.bloodmagic.api.IBloodMagicPlugin;
import WayofTime.bloodmagic.api.IBloodMagicRecipeRegistrar;
import WayofTime.bloodmagic.ritual.RitualRegistry;
import io.github.alex_hawks.SanguineExtras.common.items.baubles.LiquidSummonHandler$;
import io.github.alex_hawks.SanguineExtras.common.items.baubles.StoneSummonHandler$;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.HandlerEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.MsgEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.WRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.AMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.WMRSHandler;
import io.github.alex_hawks.SanguineExtras.common.rituals.advanced.Forge;
import io.github.alex_hawks.SanguineExtras.common.rituals.advanced.Test;
import io.github.alex_hawks.SanguineExtras.common.rituals.basic.ApiaryOverclock;
import io.github.alex_hawks.SanguineExtras.common.rituals.basic.Spawn;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import io.github.alex_hawks.SanguineExtras.common.util.config.Overrides;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(dependencies = "required-after:bloodmagic", modid = Constants.Metadata.MOD_ID, name = Constants.Metadata.NAME)
//modLanguage is the language this file is in
public class SanguineExtras
{
    public static SimpleNetworkWrapper networkWrapper;

    @SidedProxy(clientSide = "io.github.alex_hawks.SanguineExtras.client.ClientProxy", serverSide = "io.github.alex_hawks.SanguineExtras.common.CommonProxy", modId = Constants.Metadata.MOD_ID)
    public static CommonProxy proxy;
    
    @Mod.Instance(Constants.Metadata.MOD_ID)
    public static SanguineExtras INSTANCE;
    
    public static Logger LOG;
    
    private static IBloodMagicAPI BM_API;
    
    @BloodMagicPlugin
    public static class Plugin implements IBloodMagicPlugin
    {
        @Override
        public void register(IBloodMagicAPI api)
        {
            System.out.println("doing stuff");
            BM_API = api;
        }
    
        @Override
        public void registerRecipes(IBloodMagicRecipeRegistrar registrar)
        {
            Recipe.register(registrar);
        }
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e)
    {
        proxy.registerClientStuff();

        LOG = e.getModLog();

        Overrides.initOverrides(new File(e.getModConfigurationDirectory(), Constants.Metadata.MOD_ID));
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent e)
    {
        RitualRegistry.registerRitual(new Spawn(), Base.ritual.enabled.spawn);
        RitualRegistry.registerRitual(new Test(), Base.ritual.enabled.test);
        RitualRegistry.registerRitual(new Forge(), Base.ritual.enabled.forge);
        RitualRegistry.registerRitual(new ApiaryOverclock(), Base.ritual.enabled.bees && Constants.Loaded.FORESTRY);

        MinecraftForge.EVENT_BUS.register(new AMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WMRSHandler());
        MinecraftForge.EVENT_BUS.register(new WRSHandler());

        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.Metadata.MOD_ID);
        networkWrapper.registerMessage(HandlerEntityMotion.class, MsgEntityMotion.class, 1, Side.CLIENT);

        if (Constants.Loaded.MCMP)
        {
//            MultipartRegistry.registerPartFactory(new MultipartFactory(), MultipartStone.NAME().toString());
        }
        if (Constants.Loaded.BAUBLES)
        {
            MinecraftForge.EVENT_BUS.register(LiquidSummonHandler$.MODULE$);
            MinecraftForge.EVENT_BUS.register(StoneSummonHandler$.MODULE$);
        }
    }

    @Mod.EventHandler
    public static void handleIMC(FMLInterModComms.IMCEvent e)
    {
        Overrides.handleIMC(e.getMessages());
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent e)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);

        Overrides.handleDefaults();
        Overrides.applyConfig();
    }
    
    public static IBloodMagicAPI getBmApi()
    {
        return BM_API;
    }
}
