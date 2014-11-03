package io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded_master;

import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;

import java.util.UUID;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.common.items.ActivationCrystal;
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *  Ritual related code mostly taken from Blood Magic itself
 */
public class BlockWardedMasterStone extends BlockContainer
{

    public BlockWardedMasterStone()
    {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setBlockName("blockWardedMasterStone");
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
        if (tile instanceof TEWardedMasterStone)
        {
            ((TEWardedMasterStone) tile).useOnRitualBroken();
        }

        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TEWardedMasterStone tileEntity = (TEWardedMasterStone) world.getTileEntity(x, y, z);

        if (tileEntity == null)
        {
            return false;
        }
        
        UUID stoneOwner = tileEntity.getBlockOwner();

        if (stoneOwner == null)
        {
            tileEntity.setBlockOwner(player.getPersistentID());
            stoneOwner = tileEntity.getBlockOwner();
        }
        
        ItemStack playerItem = player.getCurrentEquippedItem();

        if (playerItem == null || player.isSneaking())
        {
            return false;
        }

        Item item = playerItem.getItem();

        if (!(item instanceof ActivationCrystal))
        {
            return false;
        }

        if (stoneOwner.equals(player.getPersistentID()))
        {
            ActivationCrystal acItem = (ActivationCrystal) item;
            tileEntity.setOwner(ActivationCrystal.getOwnerName(playerItem));
            tileEntity.activateRitual(world, acItem.getCrystalLevel(playerItem), player);
            world.markBlockForUpdate(x, y, z);
            return true;
        }
        
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TEWardedMasterStone();
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase player, ItemStack is)
    {
        TEWardedMasterStone tileEntity = (TEWardedMasterStone) w.getTileEntity(x, y, z);

        if (player instanceof EntityPlayer && !SpellHelper.isFakePlayer(w, (EntityPlayer) player))
            tileEntity.setBlockOwner(((EntityPlayer) player).getPersistentID());
    }
}
