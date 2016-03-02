package io.github.alex_hawks.SanguineExtras.common.network.entity_motion;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerEntityMotion implements IMessageHandler<MsgEntityMotion, IMessage>
{

    @Override
    public IMessage onMessage(MsgEntityMotion msg, MessageContext ctx)
    {
        World w = DimensionManager.getWorld(msg.worldID);
        if (w != null)
        {
            Entity e = w.getEntityByID(msg.entityID);
            if (e != null)
            {
                e.motionX = msg.movX;
                e.motionY = msg.movY;
                e.motionZ = msg.movZ;
                e.posX = msg.posX;
                e.posY = msg.posY;
                e.posZ = msg.posZ;
            }
        }
        return null;
    }

}
