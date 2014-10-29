package io.github.alex_hawks.SanguineExtras.api;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;

public class MobNetBlacklist
{
    private static final Set<Class<? extends EntityLivingBase>> capture = new LinkedHashSet<Class<? extends EntityLivingBase>>();
    private static final Set<Class<? extends EntityLivingBase>> spawn = new LinkedHashSet<Class<? extends EntityLivingBase>>();
    
    public static void addToCaptureBlacklist(Class<? extends EntityLivingBase> ent)
    {
        capture.add(ent);
    }    
    
    public static void addToSpawnBlacklist(Class<? extends EntityLivingBase> ent)
    {
        spawn.add(ent);
    }
    
    public static void removeFromCaptureBlacklist(Class<? extends EntityLivingBase> ent)
    {
        capture.remove(ent);
    }
    
    public static void removeFromSpawnBlacklist(Class<? extends EntityLivingBase> ent)
    {
        spawn.remove(ent);
    }
    
    public static boolean isCaptureBlacklisted(Class<? extends EntityLivingBase> ent)
    {
        return capture.contains(ent);
    }
    
    public static boolean isSpawnBlacklisted(Class<? extends EntityLivingBase> ent)
    {
        return spawn.contains(ent);
    }
}
