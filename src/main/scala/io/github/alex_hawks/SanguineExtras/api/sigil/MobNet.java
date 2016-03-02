package io.github.alex_hawks.SanguineExtras.api.sigil;

import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.monster.EntityWitch;

import java.util.LinkedHashSet;
import java.util.Set;

public class MobNet
{
    private static final Set<Class<?>> capture = new LinkedHashSet<Class<?>>();
    private static final Set<Class<?>> spawn = new LinkedHashSet<Class<?>>();

    static
    {
        addToCaptureBlacklist(IEntityMultiPart.class);
        addToCaptureBlacklist("thaumcraft.common.entities.golems.EntityGolemBase");

        addToSpawnBlacklist(IEntityMultiPart.class);
        addToSpawnBlacklist(EntityWitch.class);
    }

    public static void addToCaptureBlacklist(Class<?> ent)
    {
        capture.add(ent);
    }

    public static void addToCaptureBlacklist(String name)
    {
        try
        {
            addToCaptureBlacklist(Class.forName(name));
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void addToSpawnBlacklist(Class<?> ent)
    {
        spawn.add(ent);
    }

    public static void addToSpawnBlacklist(String name)
    {
        try
        {
            addToSpawnBlacklist(Class.forName(name));
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void removeFromCaptureBlacklist(Class<?> ent)
    {
        capture.remove(ent);
    }

    public static void removeFromSpawnBlacklist(Class<?> ent)
    {
        spawn.remove(ent);
    }

    public static boolean isCaptureBlacklisted(Object ent)
    {
        if (capture.contains(ent.getClass()))
            return true;
        else
        {
            for (Class<?> clazz : capture)
            {
                if (clazz.isInstance(ent))
                {
                    capture.add(ent.getClass());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSpawnBlacklisted(Object ent)
    {
        if (spawn.contains(ent.getClass()))
            return true;
        else
        {
            for (Class<?> clazz : spawn)
            {
                if (clazz.isInstance(ent))
                {
                    spawn.add(ent.getClass());
                    return true;
                }
            }
        }
        return false;
    }
}
