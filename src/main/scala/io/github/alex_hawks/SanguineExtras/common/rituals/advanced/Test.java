package io.github.alex_hawks.SanguineExtras.common.rituals.advanced;

import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.ritual.RitualComponent;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

public class Test extends AdvancedRitual
{
    public static final String name = "SE002TEST";

    public Test()
    {
        super(name, 0, 100, "ritual." + Constants.MetaData.MOD_ID + ".test");
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

        this.addRune(ls, 0, -1, 0, EnumRuneType.BLANK);

        return ls;
    }

    @Override
    public void performRitual(IMasterRitualStone arg0)
    {

    }

    @Override
    public boolean onRightClick(IAdvancedMasterRitualStone mrs, EnumHand hand)
    {
        System.out.println("Right Click");
        return true;
    }

    @Override
    public boolean onLeftClick(IAdvancedMasterRitualStone mrs, EnumHand hand)
    {
        System.out.println("Left Click");
        return true;
    }

    @Override
    public void onCollideWith(IAdvancedMasterRitualStone mrs, Entity ent)
    {
        System.out.println("A " + ent.getName() + " collided with the stone");
    }

    @Override
    public Ritual getNewCopy()
    {
        return new Test();
    }

}
