package io.github.alex_hawks.SanguineExtras.common.rituals.interactable;

import java.util.LinkedList;
import java.util.List;

import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.api.ritual.InteractableRitualEffect;

public class TestInteractableRitual extends InteractableRitualEffect
{

    @Override
    public int getCostPerRefresh()
    {
        return 0;
    }

    @Override
    public List<RitualComponent> getRitualComponentList()
    {
        List<RitualComponent> ls = new LinkedList<RitualComponent>();
        
        ls.add(new RitualComponent( 0,  1,  0, RitualComponent.AIR));

        return ls;
    }

    @Override
    public void performEffect(IMasterRitualStone arg0)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean onRightClick(IAdvancedMasterRitualStone mrs) 
    {
        System.out.println("Right Click");
        return true;
    }

    @Override
    public boolean onLeftClick(IAdvancedMasterRitualStone mrs) 
    {
        System.out.println("Left Click");
        return true;
    }
    

}
