package io.github.alex_hawks.SanguineExtras.client;

import io.github.alex_hawks.SanguineExtras.client.constructs.GuiChest;
import io.github.alex_hawks.SanguineExtras.client.constructs.RenderChest;
import io.github.alex_hawks.SanguineExtras.client.handler.RitualDivinerRender;
import io.github.alex_hawks.SanguineExtras.client.ritual_stones.marker.micro.RenderRitualStoneCube;
import io.github.alex_hawks.SanguineExtras.client.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.CommonProxy;
import io.github.alex_hawks.SanguineExtras.common.ModItems;
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerClientStuff()
    {
        MinecraftForge.EVENT_BUS.register(new UtilsBuilding());
        MinecraftForge.EVENT_BUS.register(new RitualDivinerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChest.class, new RenderChest());
        if (Loader.isModLoaded("ForgeMultipart"))
        {
            MinecraftForgeClient.registerItemRenderer(ModItems.MicroRitualStone, new RenderRitualStoneCube());
            MinecraftForgeClient.registerItemRenderer(ModItems.StableRitualStone, new RenderRitualStoneCube(0.5));
            MinecraftForgeClient.registerItemRenderer(ModItems.MicroStone, new RenderRitualStoneCube());
        }
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID) 
        {
            case 0: return new GuiChest(player.inventory, (TileChest) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
