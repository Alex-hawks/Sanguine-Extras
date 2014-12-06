package io.github.alex_hawks.SanguineExtras.client;

import io.github.alex_hawks.SanguineExtras.client.ritual_stones.marker.micro.RenderRitualStoneCube;
import io.github.alex_hawks.SanguineExtras.client.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.CommonProxy;
import io.github.alex_hawks.SanguineExtras.common.ModItems;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerClientStuff()
    {
        MinecraftForge.EVENT_BUS.register(new UtilsBuilding());
        FMLCommonHandler.instance().bus().register(new UtilsBuilding());
        if (Loader.isModLoaded("ForgeMultipart"))
        {
            MinecraftForgeClient.registerItemRenderer(ModItems.MicroRitualStone, new RenderRitualStoneCube());
            MinecraftForgeClient.registerItemRenderer(ModItems.StableRitualStone, new RenderRitualStoneCube(0.5));
            MinecraftForgeClient.registerItemRenderer(ModItems.MicroStone, new RenderRitualStoneCube());
        }
    }
}
