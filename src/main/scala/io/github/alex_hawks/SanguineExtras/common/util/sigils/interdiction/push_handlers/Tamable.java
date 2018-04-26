package io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.push_handlers;

import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Tamable implements IPushCondition
{

    @Override
    public Push canPush(Entity pushedEntity, EntityPlayer pusher)
    {
        if (pushedEntity instanceof IEntityOwnable)
        {
            IEntityOwnable ent = (IEntityOwnable) pushedEntity;

            return pusher.equals(ent.getOwner()) ? Push.DENY : Push.ALLOW;
        }
        return Push.IGNORE;
    }

    @Override
    public Push canPush(Entity pushedEntity, UUID player)
    {
        if (pushedEntity instanceof IEntityOwnable)
        {
            IEntityOwnable ent = (IEntityOwnable) pushedEntity;

            return player.equals(ent.getOwnerId()) ? Push.DENY : Push.ALLOW;
        }
        return Push.IGNORE;
    }

    @Override
    public boolean handlesEntity(ResourceLocation entityID)
    {
        IForgeRegistry<EntityEntry> ents = ForgeRegistries.ENTITIES;
        EntityEntry ent = ents.getValue(entityID);
        if (ent == null)
            return false;
        Class<? extends Entity> clazz = ent.getEntityClass();
        return clazz != null && IEntityOwnable.class.isAssignableFrom(clazz);
    }

}
