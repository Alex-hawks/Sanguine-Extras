package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.api.ritual.Ritual;
import io.github.alex_hawks.SanguineExtras.api.ritual.InteractableRitualEffect;
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
        if (e.useBlock != Event.Result.ALLOW)
            return;
        TileEntity te = e.world.getTileEntity(e.pos);

        if (!(te instanceof TEAdvancedMasterStone))
            return;

        Ritual ritual = ((TEAdvancedMasterStone) te).getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof InteractableRitualEffect)
        {
            if (e.action == Action.LEFT_CLICK_BLOCK)
                ((InteractableRitualEffect) ritual).onLeftClick((TEAdvancedMasterStone) te);
            if (e.action == Action.RIGHT_CLICK_BLOCK)
                ((InteractableRitualEffect) ritual).onRightClick((TEAdvancedMasterStone) te);
        }
    }

    public static void onCollide(TEAdvancedMasterStone mrs, Entity ent)
    {
        if (mrs == null)
            return;

        Ritual ritual = mrs.getCurrentRitual();

        if (ritual == null)
            return;

        if (ritual instanceof InteractableRitualEffect)
        {
            ((InteractableRitualEffect) ritual).onCollideWith(mrs, ent);
        }
    }
}
