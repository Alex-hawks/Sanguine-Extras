package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.api.registry.RitualRegistry;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.util.helper.RitualHelper;
import WayofTime.bloodmagic.registry.ModItems;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import WayofTime.bloodmagic.util.ChatUtil;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * Ritual related code mostly taken from Blood Magic itself
 */
public class BlockAdvancedMasterStone extends BlockContainer
{
    public static final double SMALL_NUMBER = 0.00000000000001;


    public BlockAdvancedMasterStone()
    {
        super(Material.iron);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setRegistryName("advancedMasterStone");
        this.setUnlocalizedName("advancedMasterStone");
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
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileMasterRitualStone)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == ModItems.activationCrystal)
            {
                String key = RitualHelper.getValidRitual(world, pos);
                EnumFacing direction = RitualHelper.getDirectionOfRitual(world, pos, key);
                if (!key.isEmpty() && direction != null && RitualHelper.checkValidRitual(world, pos, key, direction))
                {
                    if (((TileMasterRitualStone) tile).activateRitual(player.getHeldItem(), player, RitualRegistry.getRitualForId(key)))
                    {
                        ((TileMasterRitualStone) tile).setDirection(direction);
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
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TEAdvancedMasterStone();
    }

    @Override
    public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity ent)
    {
        TileEntity te = w.getTileEntity(pos);
        if (te instanceof TEAdvancedMasterStone)
            AMRSHandler.onCollide((TEAdvancedMasterStone) te, ent);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return new Vector3(pos).shiftAABB(new AxisAlignedBB(SMALL_NUMBER, SMALL_NUMBER, SMALL_NUMBER, 16 - SMALL_NUMBER, 16 - SMALL_NUMBER, 16 - SMALL_NUMBER));
    }
}
