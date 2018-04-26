package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import WayofTime.bloodmagic.block.base.BlockEnum;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.ritual.EnumRuneType;
import WayofTime.bloodmagic.ritual.IRitualStone;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWardedRitualStone extends BlockEnum<EnumRuneType> implements IRitualStone
{

    public BlockWardedRitualStone()
    {
        super(Material.IRON, EnumRuneType.class);
        this.setRegistryName(Constants.Metadata.MOD_ID, "warded_ritual_stone");
        setCreativeTab(SanguineExtrasCreativeTab.Instance);

        setUnlocalizedName(Constants.Metadata.MOD_ID + ".warded_ritual_stone.");
        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(-1.0F);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEWardedRitualStone();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    public boolean isRuneType(World world, BlockPos pos, EnumRuneType runeType) {
        return runeType == this.getTypes()[getMetaFromState(world.getBlockState(pos))];
    }

    @Override
    public void setRuneType(World world, BlockPos pos, EnumRuneType runeType) {
        int meta = runeType.ordinal();
        IBlockState newState = RegistrarBloodMagicBlocks.RITUAL_STONE.getStateFromMeta(meta);
        world.setBlockState(pos, newState);
    }
}
