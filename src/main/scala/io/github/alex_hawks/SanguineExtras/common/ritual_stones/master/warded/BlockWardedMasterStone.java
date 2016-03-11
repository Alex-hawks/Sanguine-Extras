package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;

import WayofTime.bloodmagic.api.registry.RitualRegistry;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.util.helper.RitualHelper;
import WayofTime.bloodmagic.registry.ModItems;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import WayofTime.bloodmagic.util.ChatUtil;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Ritual related code mostly taken from Blood Magic itself
 */
public class BlockWardedMasterStone extends BlockContainer
{

    public BlockWardedMasterStone()
    {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setRegistryName("wardedMasterStone");
        this.setUnlocalizedName("wardedMasterStone");
    }

    @Override
    public int getRenderType()
    {
        return 3;
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

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!(world.getTileEntity(pos) instanceof TEWardedMasterStone))
            return true;

        TEWardedMasterStone tile = (TEWardedMasterStone) world.getTileEntity(pos);

        UUID stoneOwner = tile.getBlockOwner();

        if (stoneOwner == null)
        {
            tile.setBlockOwner(player.getPersistentID());
            stoneOwner = tile.getBlockOwner();
        }

        if (tile instanceof TileMasterRitualStone && stoneOwner.equals(player.getPersistentID()))
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == ModItems.activationCrystal)
            {
                String key = RitualHelper.getValidRitual(world, pos);
                EnumFacing direction = RitualHelper.getDirectionOfRitual(world, pos, key);
                if (!key.isEmpty() && direction != null && RitualHelper.checkValidRitual(world, pos, key, direction))
                {
                    if (tile.activateRitual(player.getHeldItem(), player, RitualRegistry.getRitualForId(key)))
                    {
                        tile.setDirection(direction);
                    }
                } else
                {
                    ChatUtil.sendNoSpamUnloc(player, "chat.BloodMagic.ritual.notValid");
                }
            }
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TEWardedMasterStone();
    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
    {
        TEWardedMasterStone tileEntity = (TEWardedMasterStone) w.getTileEntity(pos);

        if (player instanceof EntityPlayer && !PlayerUtils.isFakePlayer((EntityPlayer) player))
            tileEntity.setBlockOwner(player.getPersistentID());
    }
}
