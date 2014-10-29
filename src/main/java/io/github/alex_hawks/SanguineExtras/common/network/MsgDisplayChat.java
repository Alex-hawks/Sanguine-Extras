package io.github.alex_hawks.SanguineExtras.common.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MsgDisplayChat implements IMessage 
{
	public String unlocalizedName;
	
	public MsgDisplayChat() 
	{
		
	}
	
	public MsgDisplayChat(String unlocalizedName) 
	{
		this.unlocalizedName = unlocalizedName;
	}
	
	@Override
	public void toBytes(ByteBuf buf) 
	{
		byte[] bytes = unlocalizedName.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.unlocalizedName = new String(buf.readBytes(buf.readInt()).array());
	}
}
