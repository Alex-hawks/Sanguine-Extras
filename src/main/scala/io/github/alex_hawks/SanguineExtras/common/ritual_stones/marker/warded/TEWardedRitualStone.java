package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class TEWardedRitualStone extends TileEntity
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        if (blockOwner != null)
        {
            tag.setLong("OwnerMost", blockOwner.getMostSignificantBits());
            tag.setLong("OwnerLeast", blockOwner.getLeastSignificantBits());
        }

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        if (tag.hasKey("OwnerMost") && tag.hasKey("OwnerLeast"))
        {
            this.blockOwner = new UUID(tag.getLong("OwnerMost"), tag.getLong("OwnerLeast"));
        }
    }

    public boolean canBreak(EntityPlayer player)
    {
        if (blockOwner == null)
            return true;
        if (player.getPersistentID().equals(blockOwner))
            return true;
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            if (player.canUseCommand(2, "") && Base.ritual.stones.opsCanBreakWardedBlocks)
                return true;
        return false;
    }
}
