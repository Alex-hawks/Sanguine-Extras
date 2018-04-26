package io.github.alex_hawks.SanguineExtras.common.rituals.advanced;

import WayofTime.bloodmagic.ritual.EnumRuneType;
import WayofTime.bloodmagic.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import java.util.function.Consumer;

public class Test extends AdvancedRitual
{
    public static final String name = "SE002TEST";

    public Test()
    {
        super(name, 0, 100, "ritual." + Constants.Metadata.MOD_ID + ".test");
    }

    @Override
    public int getRefreshCost()
    {
        return 0;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> ls)
    {
        this.addRune(ls, 0, -1, 0, EnumRuneType.BLANK);
    }

    @Override
    public void performRitual(IMasterRitualStone arg0)
    {

    }

    @Override
    public boolean onRightClick(IAdvancedMasterRitualStone mrs, EntityPlayer player, EnumHand hand, EnumFacing side, double hitX, double hitY, double hitZ)
    {
        System.out.println("Right Click");
        return true;
    }

    @Override
    public boolean onLeftClick(IAdvancedMasterRitualStone mrs, EntityPlayer player, EnumHand hand, EnumFacing side, double hitX, double hitY, double hitZ)
    {
        System.out.println("Left Click");
        return true;
    }

    @Override
    public void onFallUpon(IAdvancedMasterRitualStone mrs, Entity ent, float fallDistance)
    {
        System.out.println("A " + ent.getName() + " collided with the stone");
    }

    @Override
    public Ritual getNewCopy()
    {
        return new Test();
    }

}
