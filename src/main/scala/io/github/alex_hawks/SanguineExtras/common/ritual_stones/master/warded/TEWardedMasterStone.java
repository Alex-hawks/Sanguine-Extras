package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;

import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.tile.TileMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
        if (blockOwner == null) // not claimed
            return true;
        if (player.getPersistentID().equals(blockOwner)) // owner is breaking
            return true;
        if (this.getCurrentRitual() == null) // ritual not activated at all. even if disabled with redstone, that still returns a ritual
            return true;
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            if (player.canUseCommand(2, "") && Base.ritual.stones.opsCanBreakWardedBlocks)
                return true;
        return false;
    }

    @Override
    public NBTTagCompound serialize(NBTTagCompound tag)
    {
        super.serialize(tag);
        if (blockOwner != null)
            tag.setUniqueId("BlockOwner", blockOwner);

        return tag;
    }

    @Override
    public void deserialize(NBTTagCompound tag)
    {
        super.deserialize(tag);
        if (tag.hasUniqueId("BlockOwner"))
            this.blockOwner = tag.getUniqueId("BlockOwner");
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
