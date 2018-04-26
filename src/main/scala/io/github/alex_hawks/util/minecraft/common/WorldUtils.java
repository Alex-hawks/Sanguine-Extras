package io.github.alex_hawks.util.minecraft.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class WorldUtils
{
    public static void dropItem(World world, ItemStack item, int x, int y, int z)
    {
        Random rand = new Random();

        if (item != null && item.getCount() > 0)
        {
            float rx = rand.nextFloat() * 0.8F + 0.1F;
            float ry = rand.nextFloat() * 0.8F + 0.1F;
            float rz = rand.nextFloat() * 0.8F + 0.1F;
            EntityItem entityItem = new EntityItem(world,
                    x + rx, y + ry, z + rz,
                    new ItemStack(item.getItem(), item.getCount(), item.getItemDamage()));

            if (item.hasTagCompound())
            {
                entityItem.getItem().setTagCompound(item.getTagCompound().copy());
            }

            float factor = 0.05F;
            entityItem.motionX = rand.nextGaussian() * factor;
            entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
            entityItem.motionZ = rand.nextGaussian() * factor;
            world.spawnEntity(entityItem);
            item.setCount(0);
        }
    }
}
