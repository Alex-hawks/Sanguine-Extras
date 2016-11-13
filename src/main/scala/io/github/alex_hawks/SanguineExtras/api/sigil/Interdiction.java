package io.github.alex_hawks.SanguineExtras.api.sigil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Interdiction
{
    private static final Set<Class<?>> pushDeny = new LinkedHashSet<Class<?>>();
    private static final Set<Class<?>> pushAllow = new LinkedHashSet<Class<?>>();

    private static final Map<Class<?>, IPushCondition> pushConditional = new HashMap<Class<?>, IPushCondition>();

    static
    {
        addToPushDeny(IEntityMultiPart.class);
        addToPushDeny(EntityPlayer.class);
        addToPushDeny("thaumcraft.common.entities.construct.golem.EntityThaumcraftGolem");
        addToPushDeny("vazkii.botania.api.internal.IManaBurst");

        addToPushAllow(IProjectile.class);
        addToPushAllow(EntityLivingBase.class);
    }

    public static void addToPushAllow(Class<?> ent)
    {
        pushAllow.add(ent);
    }

    public static void addToPushConditional(Class<?> ent, IPushCondition handler)
    {
        pushConditional.put(ent, handler);
    }

    public static void addToPushDeny(Class<?> ent)
    {
        pushDeny.add(ent);
    }

    public static void addToPushAllow(String name)
    {
        try
        {
            addToPushAllow(Class.forName(name));
        } catch (ClassNotFoundException e)
        {
        }
    }

    public static void addToPushConditional(String name, IPushCondition handler)
    {
        try
        {
            addToPushConditional(Class.forName(name), handler);
        } catch (ClassNotFoundException e)
        {
        }
    }

    public static void addToPushDeny(String name)
    {
        try
        {
            addToPushDeny(Class.forName(name));
        } catch (ClassNotFoundException e)
        {
        }
    }

    public static boolean isPushAllowed(Entity e, EntityPlayer pushedBy)
    {
        if (pushDeny.contains(e.getClass()))
            return false;
        else
        {
            for (Class<?> clazz : pushDeny)
            {
                if (clazz.isInstance(e))
                {
                    pushDeny.add(e.getClass());
                    return false;
                }
            }
        }

        boolean allowCondition = true;
        boolean handlerExists = false;

        for (Class<?> clazz : pushConditional.keySet())
        {
            if (clazz.isInstance(e))
            {
                allowCondition &= pushConditional.get(clazz).canPush(e, pushedBy);
                handlerExists = true;
            }
        }
        if (handlerExists)
            return allowCondition;


        if (pushAllow.contains(e.getClass()))
            return true;
        else
        {
            for (Class<?> clazz : pushAllow)
            {
                if (clazz.isInstance(e))
                {
                    pushAllow.add(e.getClass());
                    return true;
                }
            }
        }
        return false;
    }
}
