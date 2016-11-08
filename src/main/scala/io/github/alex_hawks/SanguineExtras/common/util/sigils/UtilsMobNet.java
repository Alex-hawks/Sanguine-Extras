package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class UtilsMobNet
{
    public static String getEntityName(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("entityName"))
            return stack.getTagCompound().getString("entityName");
        return null;
    }

    @SuppressWarnings("unchecked")
    public static EntityLivingBase createCopiedEntity(ItemStack stack, World w)
    {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
        {
            return null;
        } else
        {
            try
            {
                Class<? extends EntityLivingBase> clazz = (Class<? extends EntityLivingBase>) Class.forName(stack.getTagCompound().getString("entityClass"));
                Constructor<? extends EntityLivingBase> constructor = clazz.getConstructor(World.class);

                EntityLivingBase newmob = constructor.newInstance(w);
                newmob.readFromNBT(stack.getTagCompound().getCompoundTag("entity"));
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
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
        {
            return null;
        } else
        {
            try
            {
                Class<? extends EntityLivingBase> clazz = (Class<? extends EntityLivingBase>) Class.forName(stack.getTagCompound().getString("entityClass"));
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

