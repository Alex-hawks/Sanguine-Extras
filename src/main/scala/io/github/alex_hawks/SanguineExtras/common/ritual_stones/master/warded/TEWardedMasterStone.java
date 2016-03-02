package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;

import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class TEWardedMasterStone extends TileMasterRitualStone
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
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            if (MinecraftServer.getServer().getConfigurationManager().canSendCommands(player.getGameProfile()) && SanguineExtras.opsCanBreakWardedBlocks)
                return true;
        return false;
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

    @Override
    public boolean activateRitual(ItemStack crystal, EntityPlayer player, Ritual ritual)
    {
        if (super.activateRitual(crystal, player, ritual))
        {
            WMRSHandler.wardRitual(this, player);
            return true;
        }
        return false;
    }
}
