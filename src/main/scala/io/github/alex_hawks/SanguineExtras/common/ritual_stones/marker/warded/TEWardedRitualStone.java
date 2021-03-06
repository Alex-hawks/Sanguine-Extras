package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;

import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

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
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        if (blockOwner != null) 
        {
            tag.setLong("OwnerMost", blockOwner.getMostSignificantBits());
            tag.setLong("OwnerLeast", blockOwner.getLeastSignificantBits());
        }
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
        	if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile()) && SanguineExtras.opsCanBreakWardedBlocks)
        		return true;
        return false;
    }
}
