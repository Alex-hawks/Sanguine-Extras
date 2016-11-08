package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.api.ritual.IRitualStone;
import WayofTime.bloodmagic.block.BlockRitualStone;
import WayofTime.bloodmagic.block.base.BlockStringContainer;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public class BlockWardedRitualStone extends BlockStringContainer implements IRitualStone
{
    public static final String[] names = BlockRitualStone.names;

    public BlockWardedRitualStone()
    {
        super(Material.IRON, names);
        this.setRegistryName(Constants.MetaData.MOD_ID.toLowerCase(Locale.ROOT), "wardedRitualStone");
        setCreativeTab(SanguineExtrasCreativeTab.Instance);

        setUnlocalizedName(Constants.MetaData.MOD_ID + ".ritualStone.");
        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(-1.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta)
    {
        return new TEWardedRitualStone();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    public boolean isRuneType(World world, BlockPos pos, EnumRuneType runeType)
    {
        return runeType.toString().equals(names[getMetaFromState(world.getBlockState(pos))]);
    }

    @Override
    public void setRuneType(World world, BlockPos pos, EnumRuneType runeType)
    {
        int meta = runeType.ordinal();
        IBlockState newState = this.getStateFromMeta(meta);
        world.setBlockState(pos, newState);
    }
}
