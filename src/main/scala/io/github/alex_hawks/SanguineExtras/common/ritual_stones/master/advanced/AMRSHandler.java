package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.api.ritual.Ritual;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AMRSHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerClick(PlayerInteractEvent e)
    {
        if (e.useBlock == Event.Result.DENY || e.world.isRemote)
            return;
        TileEntity te = e.world.getTileEntity(e.pos);

        if (!(te instanceof IAdvancedMasterRitualStone))
            return;

        Ritual ritual = ((IAdvancedMasterRitualStone) te).getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof AdvancedRitual)
        {
            if (e.action == Action.LEFT_CLICK_BLOCK)
                ((AdvancedRitual) ritual).onLeftClick((IAdvancedMasterRitualStone) te);
            if (e.action == Action.RIGHT_CLICK_BLOCK)
                ((AdvancedRitual) ritual).onRightClick((IAdvancedMasterRitualStone) te);
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
