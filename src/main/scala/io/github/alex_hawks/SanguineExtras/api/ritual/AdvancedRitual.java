package io.github.alex_hawks.SanguineExtras.api.ritual;

import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.ritual.RitualRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;


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

    public boolean onRightClick(IAdvancedMasterRitualStone mrs, EnumHand hand)
    {
        return false;
    }

    public boolean onLeftClick(IAdvancedMasterRitualStone mrs, EnumHand hand)
    {
        return false;
    }

    public void onCollideWith(IAdvancedMasterRitualStone mrs, Entity ent)
    {
    }

    @Override
    public final boolean activateRitual(IMasterRitualStone ritualStone, EntityPlayer player, String owner)
    {
        return ritualStone instanceof IAdvancedMasterRitualStone && start((IAdvancedMasterRitualStone) ritualStone, player, owner);
    }

    public boolean start(IAdvancedMasterRitualStone masterRitualStone, EntityPlayer player, String owner)
    {
        return true;
    }
}
