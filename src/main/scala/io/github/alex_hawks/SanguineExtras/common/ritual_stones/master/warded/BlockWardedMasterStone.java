package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;

import WayofTime.bloodmagic.block.IBMBlock;
import WayofTime.bloodmagic.item.ItemActivationCrystal;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualRegistry;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import WayofTime.bloodmagic.util.ChatUtil;
import WayofTime.bloodmagic.util.helper.RitualHelper;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Ritual related code mostly taken from Blood Magic itself
 */
public class BlockWardedMasterStone extends Block implements IBMBlock
{
    private static ItemBlock thisItem = null;

    public BlockWardedMasterStone()
    {
        super(Material.IRON);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setRegistryName("warded_master_stone");
        this.setUnlocalizedName("warded_master_stone");
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        TileEntity tile = world.getTileEntity(pos);

        if (getMetaFromState(state) == 0 && tile instanceof TileMasterRitualStone)
            ((TileMasterRitualStone) tile).stopRitual(Ritual.BreakType.BREAK_MRS);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
    {
        TileEntity tile = world.getTileEntity(pos);
        IBlockState state = world.getBlockState(pos);

        if (getMetaFromState(state) == 0 && tile instanceof TileMasterRitualStone)
            ((TileMasterRitualStone) tile).stopRitual(Ritual.BreakType.EXPLOSION);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        final ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (!(tileEntity instanceof TEWardedMasterStone))
            return true;

        TEWardedMasterStone tile = (TEWardedMasterStone) tileEntity;

        UUID stoneOwner = tile.getBlockOwner();

        if (stoneOwner == null)
        {
            tile.setBlockOwner(player.getPersistentID());
            stoneOwner = tile.getBlockOwner();
        }

        if (stoneOwner.equals(player.getPersistentID()))
        {
            if (heldItem.getItem() instanceof ItemActivationCrystal)
            {
                String key = RitualHelper.getValidRitual(world, pos);
                EnumFacing direction = RitualHelper.getDirectionOfRitual(world, pos, key);
                if (!key.isEmpty() && direction != null && RitualHelper.checkValidRitual(world, pos, key, direction))
                {
                    if (tile.activateRitual(heldItem, player, RitualRegistry.getRitualForId(key)))
                    {
                        tile.setDirection(direction);
                    }
                }
                else
                {
                    ChatUtil.sendNoSpamUnloc(player, "chat.BloodMagic.ritual.notValid");
                }
            }
        }

        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEWardedMasterStone();
    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
    {
        TEWardedMasterStone te = (TEWardedMasterStone) w.getTileEntity(pos);

        if (player instanceof EntityPlayer && PlayerUtils.isRealPlayer((EntityPlayer) player) && te != null)
            te.setBlockOwner(player.getPersistentID());
    }

    @Override
    public ItemBlock getItem()
    {
        if (thisItem == null)
        {
            synchronized (this)
            {
                if (thisItem == null)
                {
                    thisItem = new ItemBlock(this);
                }
            }
        }
        return thisItem;
    }
}
