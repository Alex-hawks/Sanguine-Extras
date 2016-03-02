package io.github.alex_hawks.SanguineExtras.api.ritual;

import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public abstract class InteractableRitualEffect extends Ritual
{
    public InteractableRitualEffect(String name, int crystalLevel, int activationCost, String unlocalizedName)
    {
        super(name, crystalLevel, activationCost, unlocalizedName);
    }

    public boolean onRightClick(IAdvancedMasterRitualStone mrs)
    {
        return false;
    }

    public boolean onLeftClick(IAdvancedMasterRitualStone mrs)
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
