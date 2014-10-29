package io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded_master;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import WayofTime.alchemicalWizardry.common.tileEntity.TEMasterStone;

public class TEWardedMasterStone extends TEMasterStone
{
    private UUID blockOwner;

    public UUID getBlockOwner()
    {
        return blockOwner;
    }

    public void setBlockOwner(UUID owner)
    {
        this.blockOwner = owner;
    }

    public boolean canBreak(EntityPlayer player)
    {
        if (blockOwner == null)
            return true;
        if (player.getPersistentID().equals(blockOwner))
            return true;
        if (this.getOwner().equals(""))
            return true;
        return false;
    }
}
