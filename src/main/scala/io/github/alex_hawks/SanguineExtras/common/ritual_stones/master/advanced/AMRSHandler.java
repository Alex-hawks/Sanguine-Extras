package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced;

import WayofTime.bloodmagic.ritual.Ritual;
import io.github.alex_hawks.SanguineExtras.api.ritual.AdvancedRitual;
import io.github.alex_hawks.SanguineExtras.api.ritual.IAdvancedMasterRitualStone;
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
        if (e.getResult() == Event.Result.DENY || e.getWorld().isRemote || e.isCanceled())
            return;
        TileEntity te = e.getWorld().getTileEntity(e.getPos());

        if (!(te instanceof IAdvancedMasterRitualStone))
            return;

        Ritual ritual = ((IAdvancedMasterRitualStone) te).getCurrentRitual();

        if (ritual instanceof AdvancedRitual)
        {
            AdvancedRitual ar = (AdvancedRitual) ritual;
            IAdvancedMasterRitualStone i = (IAdvancedMasterRitualStone) te;
            double x = e.getHitVec().x, y = e.getHitVec().y, z = e.getHitVec().z;

            if(ar.onLeftClick(i, e.getEntityPlayer(), e.getHand(), e.getFace(), x, y, z ))
                e.setCanceled(true);
        }
    }
}
