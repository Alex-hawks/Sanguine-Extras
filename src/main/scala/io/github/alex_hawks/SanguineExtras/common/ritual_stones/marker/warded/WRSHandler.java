package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import WayofTime.bloodmagic.util.ChatUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WRSHandler
{
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e)
    {
        if (e.getState().getBlock() instanceof BlockWardedRitualStone)
        {
            TileEntity te = e.getWorld().getTileEntity(e.getPos());

            if (te instanceof TEWardedRitualStone)
            {
                if (!((TEWardedRitualStone) te).canBreak(e.getPlayer()))
                {
                    e.setCanceled(true);

                    if (e.getPlayer() instanceof EntityPlayerMP)
                        ChatUtil.sendNoSpamUnloc(e.getPlayer(), "msg.se.fail.mine.RitualStone.warding");
                }
            }
        }
    }
}
