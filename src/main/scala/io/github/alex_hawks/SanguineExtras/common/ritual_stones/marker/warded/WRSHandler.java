package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.chat_handler.MsgDisplayChat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WRSHandler
{
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e)
    {
        if (e.block instanceof BlockWardedRitualStone)
        {
            TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);

            if (te instanceof TEWardedRitualStone)
            {
                if (!((TEWardedRitualStone) te).canBreak(e.getPlayer()))
                {
                    e.setCanceled(true);
                    
                    if (e.getPlayer() instanceof EntityPlayerMP)
                    	SanguineExtras.networkWrapper.sendTo(new MsgDisplayChat("msg.se.fail.mine.RitualStone.warding"), (EntityPlayerMP) e.getPlayer());
                }
            }
        }
    }
}
