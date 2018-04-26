package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static io.github.alex_hawks.SanguineExtras.common.items.sigils.ItemMobNet.*;

public class UtilsMobNet
{
    public static String getEntityName(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(NBT_ENT_NAME))
            return stack.getTagCompound().getString(NBT_ENT_NAME);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static EntityLivingBase createCopiedEntity(ItemStack stack, World w)
    {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(NBT_ENT_ID) || !stack.getTagCompound().hasKey(NBT_ENT))
        {
            return null;
        } else
        {
            try
            {
                EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(stack.getTagCompound().getString(NBT_ENT_ID)));

                if (entry == null)
                {
                    SanguineExtras.LOG.fatal("Someone saved a bad entity ({}) to the Sigil of Holding. This is not meant to happen", stack.getTagCompound().getString(NBT_ENT_ID));
                    return null;
                }

                Entity newmob = entry.newInstance(w);

                if (!(newmob instanceof EntityLivingBase))
                {
                    SanguineExtras.LOG.fatal("Someone saved {} (RegistryName: \"{}\") which is not an instance of EntityLivingBase to the Sigil of Holding. This is not meant to happen", newmob, entry.getRegistryName());
                    return null;
                }

                newmob.readFromNBT(stack.getTagCompound().getCompoundTag(NBT_ENT));
                return (EntityLivingBase) newmob;
            } catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static EntityLivingBase createNewEntity(ItemStack stack, World w)
    {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(NBT_ENT_ID) || !stack.getTagCompound().hasKey(NBT_ENT))
        {
            return null;
        } else
        {
            try
            {
                EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(stack.getTagCompound().getString(NBT_ENT_ID)));

                if (entry == null)
                {
                    SanguineExtras.LOG.fatal("Someone saved a bad entity ({}) to the Sigil of Holding. This is not meant to happen", stack.getTagCompound().getString(NBT_ENT_ID));
                    return null;
                }

                Entity newmob = entry.newInstance(w);

                if (!(newmob instanceof EntityLivingBase))
                {
                    SanguineExtras.LOG.fatal("Someone saved {} (RegistryName: \"{}\") which is not an instance of EntityLivingBase to the Sigil of Holding. This is not meant to happen", newmob, entry.getRegistryName());
                    return null;
                }

                return (EntityLivingBase) newmob;
            } catch (SecurityException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}

