package io.github.alex_hawks.SanguineExtras.common.network.chat_handler;

import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class HandlerDisplayChat implements IMessageHandler<MsgDisplayChat, IMessage> 
{
	@Override
	public IMessage onMessage(MsgDisplayChat message, MessageContext ctx) 
	{
		if (ctx.side == Side.CLIENT)
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(translate(message.unlocalizedName)));
			
		return null;
	}
}