package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.common.block.RitualStone;

public class BlockWardedRitualStone extends RitualStone implements ITileEntityProvider
{

    public BlockWardedRitualStone()
    {
        super();
        this.setBlockName("blockWardedRitualStone");
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta)
    {
        return new TEWardedRitualStone();
    }
    
}
