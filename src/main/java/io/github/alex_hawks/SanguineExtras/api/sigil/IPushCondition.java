package io.github.alex_hawks.SanguineExtras.api.sigil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IPushCondition
{
    public boolean canPush(Entity pushedEntity, EntityPlayer pusher);
}
