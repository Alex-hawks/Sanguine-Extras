package io.github.alex_hawks.SanguineExtras.common.network.chat_handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

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