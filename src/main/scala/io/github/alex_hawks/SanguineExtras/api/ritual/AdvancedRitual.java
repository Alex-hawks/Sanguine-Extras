package io.github.alex_hawks.SanguineExtras.api.ritual;

import WayofTime.bloodmagic.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import java.util.UUID;


public abstract class AdvancedRitual extends Ritual
{
    public AdvancedRitual(String name, int crystalLevel, int activationCost, RitualRenderer renderer, String unlocalizedName)
    {
        super(name, crystalLevel, activationCost, renderer, unlocalizedName);
    }

    public AdvancedRitual(String name, int crystalLevel, int activationCost, String unlocalizedName)
    {
        super(name, crystalLevel, activationCost, unlocalizedName);
    }

    public boolean onRightClick(IAdvancedMasterRitualStone mrs, EntityPlayer player,  EnumHand hand, EnumFacing side, double hitX, double hitY, double hitZ)
    {
        return false;
    }

    public boolean onLeftClick(IAdvancedMasterRitualStone mrs, EntityPlayer player,  EnumHand hand, EnumFacing side, double hitX, double hitY, double hitZ)
    {
        return false;
    }

    public void onFallUpon(IAdvancedMasterRitualStone mrs, Entity ent, float fallDistance)
    {
        // Intentionally left blank, @Override to add functionality
    }

    @Override
    public final boolean activateRitual(IMasterRitualStone ritualStone, EntityPlayer player, UUID owner)
    {
        return ritualStone instanceof IAdvancedMasterRitualStone && start((IAdvancedMasterRitualStone) ritualStone, player, owner);
    }

    public boolean start(IAdvancedMasterRitualStone masterRitualStone, EntityPlayer player, UUID owner)
    {
        return true;
    }
}
