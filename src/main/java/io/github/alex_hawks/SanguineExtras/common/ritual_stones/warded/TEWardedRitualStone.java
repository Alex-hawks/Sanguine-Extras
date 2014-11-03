package io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded;

import java.util.UUID;

import WayofTime.alchemicalWizardry.api.rituals.IRitualStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TEWardedRitualStone extends TileEntity implements IRitualStone
{
    private UUID blockOwner;
    
    public UUID getBlockOwner()
    {
        return blockOwner;
    }

    public void setBlockOwner(UUID blockOwner)
    {
        this.blockOwner = blockOwner;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setLong("OwnerMost", blockOwner.getMostSignificantBits());
        tag.setLong("OwnerLeast", blockOwner.getLeastSignificantBits());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.blockOwner = new UUID(tag.getLong("OwnerMost"), tag.getLong("OwnerLeast"));
    }
    
    public boolean canBreak(EntityPlayer player)
    {
        if (blockOwner == null)
            return true;
        if (player.getPersistentID().equals(blockOwner))
            return true;
        return false;
    }
}
