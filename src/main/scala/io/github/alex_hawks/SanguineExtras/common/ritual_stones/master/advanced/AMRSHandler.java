package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.api.ritual.Ritual;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AMRSHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock e)
    {
        if (e.getResult() == Event.Result.DENY || e.getWorld().isRemote)
            return;
        TileEntity te = e.getWorld().getTileEntity(e.getPos());

        if (!(te instanceof IAdvancedMasterRitualStone))
            return;

        Ritual ritual = ((IAdvancedMasterRitualStone) te).getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof AdvancedRitual)
        {
            ((AdvancedRitual) ritual).onLeftClick((IAdvancedMasterRitualStone) te, e.getHand());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock e)
    {
        if (e.getResult() == Event.Result.DENY || e.getWorld().isRemote)
            return;
        TileEntity te = e.getWorld().getTileEntity(e.getPos());

        if (!(te instanceof IAdvancedMasterRitualStone))
            return;

        Ritual ritual = ((IAdvancedMasterRitualStone) te).getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof AdvancedRitual)
        {
            ((AdvancedRitual) ritual).onRightClick((IAdvancedMasterRitualStone) te, e.getHand());
        }
    }

    public static void onCollide(IAdvancedMasterRitualStone mrs, Entity ent)
    {
        if (mrs == null)
            return;

        Ritual ritual = mrs.getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof AdvancedRitual)
        {
            ((AdvancedRitual) ritual).onCollideWith(mrs, ent);
        }
    }
}
