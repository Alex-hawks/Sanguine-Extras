package io.github.alex_hawks.SanguineExtras.client;

import io.github.alex_hawks.SanguineExtras.client.constructs.GuiChest;
import io.github.alex_hawks.SanguineExtras.client.constructs.RenderChest;
import io.github.alex_hawks.SanguineExtras.client.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.CommonProxy;
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.BlockWardedRitualStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static io.github.alex_hawks.SanguineExtras.common.ModBlocks.*;
import static io.github.alex_hawks.SanguineExtras.common.ModItems.*;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerClientStuff()
    {
        MinecraftForge.EVENT_BUS.register(new UtilsBuilding());
//        MinecraftForge.EVENT_BUS.register(new RitualDivinerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileChest.class, new RenderChest());

        registerRender();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case 0:
                return new GuiChest(player, (TileChest) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    public void registerRender()
    {
        register(SigilBuilding);
        registerOneTexture(SigilDestruction);
        register(SigilRebuild);
        register(SigilInterdiction);
        addVariant(SigilInterdiction, 1);
        register(SigilMobNet);


        register(Item.getItemFromBlock(WardedRitualStone));
        addVariant(Item.getItemFromBlock(WardedRitualStone), BlockWardedRitualStone.names);
        register(Item.getItemFromBlock(WardedMRS));

        System.out.println("Done Registering Sanguine Extras Item Renderers");
    }


    private void register(Item item)
    {
        register(item, 0);
    }

    private void register(Item item, int meta)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private void addVariant(Item item, int meta)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(item.getRegistryName() + meta, "inventory"));
    }

    private void addVariant(Item item, String...variants)
    {
        for(String variant : variants)
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    private void registerOneTexture(final Item item)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(item.getRegistryName(), "inventory");
            }
        });
    }

    private void registerMetaTexture(final Item item)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(item.getRegistryName(), "inventory");
            }
        });
    }
}