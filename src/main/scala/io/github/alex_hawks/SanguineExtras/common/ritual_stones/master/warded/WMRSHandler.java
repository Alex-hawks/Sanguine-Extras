package io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded;

import java.lang.reflect.Field;
import java.util.List;

import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.chat_handler.MsgDisplayChat;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.TEWardedRitualStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WMRSHandler
{
    private static Field effect;
    
    static
    {
        try
        {
            effect = Rituals.class.getDeclaredField("effect");
            effect.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
    
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
                        SanguineExtras.networkWrapper.sendTo(new MsgDisplayChat("msg.se.fail.mine.RitualStone.warding"), (EntityPlayerMP) e.getPlayer());
                }
            }
        }
    }
    
    public static void wardRitual(TEWardedMasterStone mrs, EntityPlayer player)
    {
        try
        {
            if (mrs == null)
                return;
            
            Rituals ritual = Rituals.ritualMap.get(mrs.getCurrentRitual());
            
            if (ritual == null)
                return;
            
            RitualEffect E = (RitualEffect) effect.get(ritual);
            
            if (E != null)
            {
                List<RitualComponent> stones = E.getRitualComponentList();
                
                if (stones != null && !stones.isEmpty())
                {
                    for (RitualComponent stone : stones)
                    {
                        TileEntity te = mrs.getWorldObj().getTileEntity(mrs.xCoord + stone.getX(), mrs.yCoord + stone.getY(), mrs.zCoord + stone.getZ());
                        if (te instanceof TEWardedRitualStone)
                        {
                            ((TEWardedRitualStone) te).setBlockOwner(player.getPersistentID());
                        }
                    }
                }
            }
        } catch (SecurityException e1)
        {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1)
        {
            e1.printStackTrace();
        } catch (IllegalAccessException e1)
        {
            e1.printStackTrace();
        }
    }
}
