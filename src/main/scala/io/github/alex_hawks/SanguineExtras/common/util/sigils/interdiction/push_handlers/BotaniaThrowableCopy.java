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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BotaniaThrowableCopy implements IPushCondition
{
    static final Class<?> EntityThrowableCopyClass = getClas();

    static Class<?> getClas()
    {
        try
        {
            return Class.forName("vazkii.botania.common.entity.EntityThrowableCopy");
        }
        catch (ClassNotFoundException e)
        {
            final class Dummy { }
            return Dummy.class;
        }
    }

    Push canPush(Entity pushedEntity)
    {
        if (EntityThrowableCopyClass.isAssignableFrom(pushedEntity.getClass()))
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
        return clazz != null && EntityThrowableCopyClass.isAssignableFrom(clazz);
    }
}
