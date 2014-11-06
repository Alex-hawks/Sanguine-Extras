package io.github.alex_hawks.SanguineExtras.common.network.entity_motion;

import net.minecraft.entity.Entity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MsgEntityMotion implements IMessage
{
    public final Entity e;
    
    public int worldID;
    public int entityID;
    public double movX;
    public double movY;
    public double movZ;
    public double posX;
    public double posY;
    public double posZ;
    
    public MsgEntityMotion() 
    {
        e = null;
    }
    
    public MsgEntityMotion(Entity e) 
    {
        this.e = e;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.worldID = buf.readInt();
        this.entityID = buf.readInt();
        this.movX = buf.readDouble();;
        this.movY = buf.readDouble();;
        this.movZ = buf.readDouble();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(e.worldObj.provider.dimensionId);
        buf.writeInt(e.getEntityId());
        buf.writeDouble(e.motionX);
        buf.writeDouble(e.motionY);
        buf.writeDouble(e.motionZ);
        buf.writeDouble(e.posX);
        buf.writeDouble(e.posY);
        buf.writeDouble(e.posZ);
    }
    
}
