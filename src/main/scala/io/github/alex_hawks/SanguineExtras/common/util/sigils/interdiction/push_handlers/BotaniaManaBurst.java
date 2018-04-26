package io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.push_handlers;

import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BotaniaManaBurst implements IPushCondition
{
    static final Class<?> ManaBurstClass = getClas();

    static Class<?> getClas()
    {
        try
        {
            return Class.forName("vazkii.botania.api.internal.IManaBurst");
        }
        catch (ClassNotFoundException e)
        {
            final class Dummy { }
            return Dummy.class;
        }
    }

    Push canPush(Entity pushedEntity)
    {
        if (ManaBurstClass.isAssignableFrom(pushedEntity.getClass()))
            return Push.DENY;
        return Push.IGNORE;
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
        IForgeRegistry<EntityEntry> ents = ForgeRegistries.ENTITIES;
        EntityEntry ent = ents.getValue(entityID);
        if (ent == null)
            return false;
        Class<? extends Entity> clazz = ent.getEntityClass();
        return clazz != null && ManaBurstClass.isAssignableFrom(clazz);
    }
}
