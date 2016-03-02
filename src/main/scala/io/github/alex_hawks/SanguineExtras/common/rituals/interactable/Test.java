package io.github.alex_hawks.SanguineExtras.common.rituals.interactable;

import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.ritual.RitualComponent;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.api.ritual.InteractableRitualEffect;
import io.github.alex_hawks.SanguineExtras.common.Constants;

import java.util.ArrayList;

public class Test extends InteractableRitualEffect
{

    public Test()
    {
        super("SE002Test", 0, 100, "ritual." + Constants.MetaData.MOD_ID + ".test");
    }

    @Override
    public int getRefreshCost()
    {
        return 0;
    }

    @Override
    public ArrayList<RitualComponent> getComponents()
    {
        ArrayList<RitualComponent> ls = new ArrayList<RitualComponent>();

        this.addRune(ls, 0, 1, 0, EnumRuneType.AIR);

        return ls;
    }

    @Override
    public void performRitual(IMasterRitualStone arg0)
    {

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

    @Override
    public Ritual getNewCopy()
    {
        return new Test();
    }

}
