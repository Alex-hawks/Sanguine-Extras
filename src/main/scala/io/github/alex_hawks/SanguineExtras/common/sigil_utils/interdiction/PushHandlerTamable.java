package io.github.alex_hawks.SanguineExtras.common.sigil_utils.interdiction;

import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;

public class PushHandlerTamable implements IPushCondition
{

    @Override
    public boolean canPush(Entity pushedEntity, EntityPlayer pusher)
    {
        if (pushedEntity instanceof IEntityOwnable)
        {
            IEntityOwnable ent = (IEntityOwnable) pushedEntity;

            return !ent.getOwner().equals(pusher);

        }
        return false;
    }

}
