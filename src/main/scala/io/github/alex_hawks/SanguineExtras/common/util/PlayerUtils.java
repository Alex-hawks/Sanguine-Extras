package io.github.alex_hawks.SanguineExtras.common.util;

import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.item.IBlockProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerUtils
{
    private static final Map<EntityPlayer, List<ItemStack>> orb = new HashMap<>();

    /**
     * @param player The player to take from
     * @param is     The item to take. Will only take 1 (ONE) item
     * @return
     */
    public static boolean takeItemInv(EntityPlayer player, ItemStack is)
    {
        if (player.capabilities.isCreativeMode)
            return true;
        ItemStack is2;
        for (int i = 0; i < player.inventory.mainInventory.length; i++)
        {
            is2 = player.inventory.mainInventory[i];
            if (is2 == null)
                continue;
            if (is.isItemEqual(is2) && ItemStack.areItemStackTagsEqual(is, is2))
            {
                is2.stackSize--;
                if (is2.stackSize <= 0)
                    player.inventory.mainInventory[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * WARNING: DOES NOT ENSURE BOTANIA IS LOADED, OR EVEN THAT ITS API EXISTS
     */
    public static boolean takeItemBotania(EntityPlayer player, ItemStack is, ItemStack requester)
    {
        if (is.getItem() instanceof ItemBlock)
        {
            ItemStack is2;
            for (int i = 0; i < player.inventory.mainInventory.length; i++)
            {
                is2 = player.inventory.mainInventory[i];
                if (is2.getItem() instanceof IBlockProvider)
                {
                    IBlockProvider prov = (IBlockProvider) is2.getItem();
                    if (prov.provideBlock(player, requester, is2, ((ItemBlock) is.getItem()).getBlock(), is.getMetadata(), false))
                    {
                        prov.provideBlock(player, requester, is2, ((ItemBlock) is.getItem()).getBlock(), is.getMetadata(), true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean takeItem(EntityPlayer player, ItemStack is, ItemStack requester)
    {
        if (takeItemInv(player, is))
            return true;
        else if (Constants.Loaded.BOTANIA && takeItemBotania(player, is, requester))
            return true;
        return false;
    }

    public static void putItemWithDrop(EntityPlayer player, ItemStack... is2)
    {
        if (player.capabilities.isCreativeMode)
            return;
        for (ItemStack is : is2)
        {
            if (!player.inventory.addItemStackToInventory(is))
            {
                player.dropItem(is, false);
            }
        }
    }

    public static boolean putItem(EntityPlayer player, ItemStack is)
    {
        if (player.capabilities.isCreativeMode)
            return true;
        return player.inventory.addItemStackToInventory(is);
    }

    public static void startOrb(EntityPlayer player)
    {
        orb.put(player, new ArrayList<ItemStack>());
    }

    public static void addToOrb(EntityPlayer player, ItemStack... is2)
    {
        for (ItemStack is : is2)
        {
            for (ItemStack stack : orb.get(player))
            {
                if (stack.getMaxStackSize() == stack.stackSize)
                    continue;
                if (stack.isItemEqual(is) && ItemStack.areItemStackTagsEqual(stack, is))
                {
                    int space = stack.getMaxStackSize() - stack.stackSize;

                    if (is.stackSize <= space)
                    {
                        stack.stackSize += is.stackSize;
                        is.stackSize = 0;
                        break;
                    }
                    else
                    {
                        stack.stackSize = stack.getMaxStackSize();
                        is.stackSize -= space;
                        continue;
                    }
                }
            }

            if (is.stackSize > 0)
                orb.get(player).add(is);
        }
    }

    public static void finishOrb(EntityPlayer player)
    {
        ItemStack is = new ItemStack(Items.DropOrb());
        Items.DropOrb().addItems(is, orb.get(player));
        orb.remove(player);
        PlayerUtils.putItemWithDrop(player, is);
    }

    public static boolean isNotFakePlayer(EntityPlayer p)
    {
        return !PlayerHelper.isFakePlayer(p);
    }
}
