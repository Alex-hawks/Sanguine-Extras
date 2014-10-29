package io.github.alex_hawks.SanguineExtras.api.ritual;

import net.minecraft.entity.player.EntityPlayer;
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;

public abstract class InteractableRitualEffect extends RitualEffect
{
    public boolean onRightClick(IAdvancedMasterRitualStone mrs) 
    {
        return false;
    }
    
    public boolean onLeftClick(IAdvancedMasterRitualStone mrs) 
    {
        return false;
    }
    
    @Override
    public boolean startRitual(IMasterRitualStone ritualStone, EntityPlayer player)
    {
        return ritualStone instanceof IAdvancedMasterRitualStone;
    }
}
