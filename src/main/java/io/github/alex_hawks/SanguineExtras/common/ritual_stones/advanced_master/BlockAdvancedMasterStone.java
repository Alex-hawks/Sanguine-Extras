package io.github.alex_hawks.SanguineExtras.common.ritual_stones.advanced_master;

import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.common.items.ActivationCrystal;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *  Ritual related code mostly taken from Blood Magic itself
 */
public class BlockAdvancedMasterStone extends BlockContainer
{

    public BlockAdvancedMasterStone()
    {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setBlockName("blockAdvancedMasterStone");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon("AlchemicalWizardry:MasterStone");   //  Yes, I'm using his textures
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TEAdvancedMasterStone)
        {
            ((TEAdvancedMasterStone) tile).useOnRitualBroken();
        }

        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TEAdvancedMasterStone tileEntity = (TEAdvancedMasterStone) world.getTileEntity(x, y, z);

        if (tileEntity == null)
        {
            return false;
        }

        ItemStack playerItem = player.getCurrentEquippedItem();

        if (playerItem == null || player.isSneaking())
        {
            return false;
            //return tileEntity.callRitualRightClick();
        }

        Item item = playerItem.getItem();

        if (!(item instanceof ActivationCrystal))
        {
            return false;
            //return tileEntity.callRitualRightClick();
        }
        
        ActivationCrystal acItem = (ActivationCrystal) item;
        tileEntity.setOwner(ActivationCrystal.getOwnerName(playerItem));
        tileEntity.activateRitual(world, acItem.getCrystalLevel(playerItem), player);
        world.markBlockForUpdate(x, y, z);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TEAdvancedMasterStone();
    }
    
    @Override
    public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity ent)
    {
    	TileEntity te = w.getTileEntity(x, y, z);
    	if (te instanceof TEAdvancedMasterStone)
    	{
    		AMRSHandler.onColide((TEAdvancedMasterStone) te, ent);
    	}
    	else
    	{
    		System.out.println("Please give the following stacktrace to the creator of the mod");
    		((TEAdvancedMasterStone) te).canUpdate();	// yes. if this code is reached, it will always throw either a NPE or a ClassCastException. It should never be reached though...
    	}
    }
}
