package io.github.alex_hawks.SanguineExtras.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerUtils
{
    /**
     * @param player The player to take from
     * @param is The item to take. Will only take 1 (ONE) item
     * @return
     */
    public static boolean takeItem(EntityPlayer player, ItemStack is)
    {
        if (player.capabilities.isCreativeMode)
            return true;
        for (ItemStack is2 : player.inventory.mainInventory)
        {
            if (is.isItemEqual(is2) && ItemStack.areItemStackTagsEqual(is, is2))
            {
                is2.stackSize--;
                return true;
            }
        }
        return false;
    }    

    public static void putItem(EntityPlayer player, ItemStack... is2)
    {
        if (player.capabilities.isCreativeMode)
            return;
        for (ItemStack is : is2)
        {
            if (!player.inventory.addItemStackToInventory(is))
            {
                player.dropPlayerItemWithRandomChoice(is, false);
            }
        }
    }
}
