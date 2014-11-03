package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class UtilsMobNet
{
    public static String getEntityName(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("entityName"))
            return stack.stackTagCompound.getString("entityName");
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static EntityLivingBase createCopiedEntity(ItemStack stack, World w)
    {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("entityClass") || !stack.stackTagCompound.hasKey("entity"))
        {
            return null;
        }
        else
        {
            try
            {
                Class<? extends EntityLivingBase> clazz = (Class<? extends EntityLivingBase>) Class.forName(stack.stackTagCompound.getString("entityClass"));
                Constructor<? extends EntityLivingBase> constructor = clazz.getConstructor(World.class);
                
                EntityLivingBase newmob = constructor.newInstance(w);
                newmob.readFromNBT(stack.stackTagCompound.getCompoundTag("entity"));
                return newmob;
            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (SecurityException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static EntityLivingBase createNewEntity(ItemStack stack, World w)
    {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("entityClass") || !stack.stackTagCompound.hasKey("entity"))
        {
            return null;
        }
        else
        {
            try
            {
                Class<? extends EntityLivingBase> clazz = (Class<? extends EntityLivingBase>) Class.forName(stack.stackTagCompound.getString("entityClass"));
                Constructor<? extends EntityLivingBase> constructor = clazz.getConstructor(World.class);
                
                EntityLivingBase newmob = constructor.newInstance(w);
                return newmob;
            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (SecurityException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}

