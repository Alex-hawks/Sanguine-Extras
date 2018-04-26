package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.block.IBMBlock;
import WayofTime.bloodmagic.item.ItemActivationCrystal;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualRegistry;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import WayofTime.bloodmagic.util.ChatUtil;
import WayofTime.bloodmagic.util.helper.RitualHelper;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Ritual related code mostly taken from Blood Magic itself
 */
public class BlockAdvancedMasterStone extends Block implements IBMBlock
{
    private static final double SMALL_NUMBER = 0.00000000000001;
    private static ItemBlock thisItem = null;

    public BlockAdvancedMasterStone()
    {
        super(Material.IRON);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setRegistryName("advanced_master_stone");
        this.setUnlocalizedName("advanced_master_stone");
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEAdvancedMasterStone();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);

        boolean r = world.isRemote;

        if (tile instanceof TEAdvancedMasterStone)
        {
            TEAdvancedMasterStone mrs = (TEAdvancedMasterStone) tile;
            Ritual ritual = mrs.getCurrentRitual();

            if (heldItem.getItem() instanceof ItemActivationCrystal)
            {
                Ritual rit = RitualRegistry.getRitualForId(RitualHelper.getValidRitual(world, pos));
                if (rit != null)
                {
                    if (rit != mrs.getCurrentRitual())
                    {
                        EnumFacing direction = RitualHelper.getDirectionOfRitual(world, pos, rit.getName());

                        if (direction != null)
                        {
                            if (mrs.activateRitual(heldItem, player, rit))
                            {
                                mrs.setDirection(direction);
                                return true;
                            } else if (mrs.getCurrentRitual() == null)
                                return false;
                        }
                    }
                    else if (!world.isRemote)
                        ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.ritual.same");
                }
                else if (!world.isRemote)
                    ChatUtil.sendNoSpamUnloc(player, "chat.bloodmagic.ritual.notValid");
            }
            else if(!(ritual instanceof AdvancedRitual))
            {
                return false;
            }
            return ((AdvancedRitual) ritual).onRightClick(mrs, player, hand, side, hitX, hitY, hitZ);
        }

        return false;
    }

    @Override
    public void onFallenUpon(World w, BlockPos pos, Entity ent, float fallDistance)
    {
        TileEntity te = w.getTileEntity(pos);
        if (te instanceof TEAdvancedMasterStone)
        {
            TEAdvancedMasterStone mrs = (TEAdvancedMasterStone) te;

            Ritual ritual = mrs.getCurrentRitual();

            if (ritual instanceof AdvancedRitual)
            {
                ((AdvancedRitual) ritual).onFallUpon(mrs, ent, fallDistance);
            }
        }

        // to properly negate motion when the entity lands, like is done in super, but also make sure that the ritual didn't do anything like bounce the player to the moon
        if(ent.motionY <= 0)
            ent.motionY = 0;
    }

//    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
//    {
//        return iBlockPos(pos).shiftAABB(new AxisAlignedBB(SMALL_NUMBER, SMALL_NUMBER, SMALL_NUMBER, 16 - SMALL_NUMBER, 16 - SMALL_NUMBER, 16 - SMALL_NUMBER));
//    }

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
