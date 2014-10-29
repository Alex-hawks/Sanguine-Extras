package io.github.alex_hawks.SanguineExtras.common.ritual_stones.warded_master;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.MsgDisplayChat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WMRSListener
{
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e)
    {
        if (e.block instanceof BlockWardedMasterStone)
        {
            TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);

            if (te instanceof TEWardedMasterStone)
            {
                if (!((TEWardedMasterStone) te).canBreak(e.getPlayer()))
                {
                    e.setCanceled(true);
                    
                    if (e.getPlayer() instanceof EntityPlayerMP)
                    	SanguineExtras.networkWrapper.sendTo(new MsgDisplayChat("msg.se.fail.block.MRS.warding"), (EntityPlayerMP) e.getPlayer());
                }
            }
        }
    }
}
