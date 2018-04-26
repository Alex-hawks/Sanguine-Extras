package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;


import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.util.ChatUtil;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.TEWardedRitualStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WMRSHandler
{
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e)
    {
        if (e.getState().getBlock() instanceof BlockWardedMasterStone)
        {
            TileEntity te = e.getWorld().getTileEntity(e.getPos());

            if (te instanceof TEWardedMasterStone)
            {
                if (!((TEWardedMasterStone) te).canBreak(e.getPlayer()))
                {
                    e.setCanceled(true);

                    if (e.getPlayer() instanceof EntityPlayerMP)
                        ChatUtil.sendNoSpamUnloc(e.getPlayer(), "msg.se.fail.mine.RitualStone.warding");
                }
            }
        }
    }

    public static void wardRitual(TEWardedMasterStone mrs, EntityPlayer player)
    {
        if (mrs == null)
            return;

        Ritual ritual = mrs.getCurrentRitual();

        if (ritual == null)
            return;

        ritual.gatherComponents(component ->
        {
            TileEntity te = mrs.getWorldObj().getTileEntity(mrs.getPos().add(component.getOffset()));
            if (te instanceof TEWardedRitualStone)
                ((TEWardedRitualStone) te).setBlockOwner(player.getPersistentID());
        });
    }
}
