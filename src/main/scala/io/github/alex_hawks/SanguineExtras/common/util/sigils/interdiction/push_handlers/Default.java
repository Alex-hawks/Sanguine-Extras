package io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.push_handlers;

import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition.Push.*;

/**
 * This class handles the hardcoded blacklist and base permitted classes
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Default implements IPushCondition
{
    private Push canPush(Entity pushedEntity)
    {
        if (pushedEntity instanceof IEntityMultiPart)
            return DENY;
        if (pushedEntity instanceof MultiPartEntityPart)
            return DENY;
        if (pushedEntity instanceof EntityPlayer)
            return DENY;

        if (pushedEntity instanceof IProjectile)
            return ALLOW;
        if (pushedEntity instanceof EntityEvokerFangs)
            return ALLOW;
        if (pushedEntity instanceof EntityLivingBase)
            return ALLOW;
        if (pushedEntity instanceof EntityFallingBlock)
            return ALLOW;
        if (pushedEntity instanceof EntityShulkerBullet)
            return ALLOW;
        if (pushedEntity instanceof EntityFireball)
            return ALLOW;
        if (pushedEntity instanceof EntityTNTPrimed)
            return ALLOW;
        return IGNORE;
    }

    @Override
    public Push canPush(Entity pushedEntity, EntityPlayer pusher)
    {
        return canPush(pushedEntity);
    }

    @Override
    public Push canPush(Entity pushedEntity, UUID player)
    {
        return canPush(pushedEntity);
    }

    @Override
    public boolean handlesEntity(ResourceLocation entityID)
    {
        return true;
    }
}
