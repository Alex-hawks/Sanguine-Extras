package io.github.alex_hawks.SanguineExtras.common.util;

import WayofTime.bloodmagic.util.helper.PlayerHelper;
import baubles.api.cap.IBaublesItemHandler;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.Items;
import mcmultipart.capability.CapabilityJoiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;
import vazkii.botania.api.item.IBlockProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayerUtils
{
    /**
     * for UUID, see {@link EntityPlayer#getUniqueID() EntityPlayer#getUniqueID()}
     */
    private static final Map<UUID, List<ItemStack>> orb = new HashMap<>();

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> CapInv = null;

    @CapabilityInject(IBaublesItemHandler.class)
    public static Capability<IBaublesItemHandler> CapBauble = null;

    /**
     *          This will pull from the items stored directly in a {@code player}'s inventory
     * <br/>    Does not check Baubles or Armour Slots, as this is checking for the item, not something containing the item.
     * <br/>    Checks offhand slot before the main inventory
     * @param player The player to take the item from
     * @param is     The item to take. Will ignore count and only take 1 (ONE) item, but respects NBT
     * @return {@code true} if it found and took the item. There is no simulation; Do the thing
     */
    public static boolean takeItemRaw(EntityPlayer player, ItemStack is)
    {
        if(!player.inventory.offHandInventory.get(0).isEmpty())
        {
            ItemStack is2 = player.inventory.offHandInventory.get(0);
            if (is.isItemEqual(is2) && ItemStack.areItemStackTagsEqual(is, is2))
            {
                is2.shrink(1);
                return true;
            }
        }
        for (ItemStack is2 : player.inventory.mainInventory)
        {
            if (is2.isEmpty())
                continue;
            if (is.isItemEqual(is2) && ItemStack.areItemStackTagsEqual(is, is2))
            {
                is2.shrink(1);
                return true;
            }
        }
        return false;
    }

    /**
     *          This will pull from the items stored in capabilities attached to items in the {@code player}'s inventory
     * <br/>    Will check Baubles and Armour Slots, as this is checking for something containing the item, not the item.
     * <br/>    Checks in the following order:
     * <ol>
     * <li>     Offhand slot
     * <li>     Armour Slots
     * <li>     Baubles
     * <li>     TODO: Alchemical Bags
     * <li>     The player's main inventory
     * </ol>
     * @param player The player to take the item from
     * @param is     The item to take. Will ignore count and only take 1 (ONE) item, but respects NBT
     * @return {@code true} if it found and took the item. There is no simulation; Do the thing
     */
    public static boolean takeItemCap(EntityPlayer player, ItemStack is)
    {
        IItemHandler ih = getAllPlayerSlots(player);
        ItemStack is2;
        for (int h = 0; h < ih.getSlots(); h++)
        {
            is2 = ih.getStackInSlot(h);
            if (is2.isEmpty())
                continue;
            if (is2.hasCapability(CapInv, null))
            {
                ItemStack is3;
                IItemHandler inv = is2.getCapability(CapInv, null);
                int lim = inv.getSlots();
                for (int i = 0; i < lim; i++)
                {
                    is3 = inv.getStackInSlot(i);
                    if (is.isItemEqual(is3) && ItemStack.areItemStackTagsEqual(is, is3))
                    {
                        is3.shrink(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * WARNING: DOES NOT ENSURE BOTANIA IS LOADED, BUT DOES ENSURE THE API EXISTS ENOUGH TO REGISTER WITH {@link ModAPIManager}
     *
     * <br/>    This will pull from the items stored in items that are {@link IBlockProvider}'s in {@code player}'s inventory
     * <br/>    Will check Baubles and Armour Slots, as this is checking for something containing the item, not the item.
     * <br/>    Checks in the following order:
     * <ol>
     * <li>     Offhand slot
     * <li>     Armour Slots
     * <li>     Baubles
     * <li>     TODO: Alchemical Bags
     * <li>     The player's main inventory
     * </ol>
     * @param player The player to take the item from
     * @param is     The item to take. Will ignore count and only take 1 (ONE) item, but respects NBT
     * @return {@code true} if it found and took the item. There is no simulation; Do the thing
     */
    public static boolean takeItemBotania(EntityPlayer player, ItemStack is, ItemStack requester)
    {
        if (!ModAPIManager.INSTANCE.hasAPI("BotaniaAPI"))
            return false;
        if (is.getItem() instanceof ItemBlock)
        {
            IItemHandler ih = getAllPlayerSlots(player);
            ItemStack is2;
            for (int h = 0; h < ih.getSlots(); h++)
            {
                is2 = ih.getStackInSlot(h);
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

    /**
     *          This will pull from the items stored in items that are anywhere* in {@code player}'s inventory
     * <br/>    Will check Baubles and Armour Slots, as this is checking for the item, not something containing the item.
     * <br/>    Checks in the following order:
     * <ol>
     * <li>     Assumes a player in Creative Mode has a bottomless supply of everything
     * <li>     {@link #takeItemRaw(EntityPlayer, ItemStack)}
     * <li>     {@link #takeItemBotania(EntityPlayer, ItemStack, ItemStack)}, only if Botania is loaded
     * <li>     {@link #takeItemCap(EntityPlayer, ItemStack)}
     * </ol>
     * @param player    The player to take the item from
     * @param is        The item to take. Will ignore count and only take 1 (ONE) item, but respects NBT
     * @param requester Only used in the case of finding the item in {@link #takeItemBotania(EntityPlayer, ItemStack, ItemStack)} as that needs it for the API to work as intended
     * @return {@code true} if it found and took the item. There is no simulation; Do the thing
     */
    public static boolean takeItem(@Nonnull EntityPlayer player, @Nonnull ItemStack is, @Nullable ItemStack requester)
    { // this list is sorted by computational cost
        if (player.capabilities.isCreativeMode)
            return true;
        else if (takeItemRaw(player, is))
            return true;
        else if (Constants.Loaded.BOTANIA && takeItemBotania(player, is, requester))
            return true;
        else if (takeItemCap(player, is))
            return true;
        return false;
    }

    public static IItemHandler getAllPlayerSlots(EntityPlayer player)
    {
        InventoryPlayer inv = player.inventory;
        return CapabilityJoiner.JoinedItemHandler.join(Arrays.<IItemHandler>asList(
                new PlayerOffhandInvWrapper(inv),
                new PlayerArmorInvWrapper(inv),
                player.getCapability(CapBauble, null),
                //TODO Alchemical Bags from ProjectE, but make sure the player has one of the correct colour in their inventory (or in another bag) to actually use the contents
                new PlayerMainInvWrapper(inv)
        ));
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
        // TODO work out which event to post here, and post it, so that other mods with backpacks and the like can still be used as intended
        return player.inventory.addItemStackToInventory(is);
    }

    public static void startOrb(EntityPlayer player)
    {
        if (!orb.containsKey(player.getUniqueID()))
            orb.put(player.getUniqueID(), new ArrayList<>());
    }

    public static void addToOrb(EntityPlayer player, ItemStack... is2)
    {
        for (ItemStack is : is2)
        {
            for (ItemStack stack : orb.get(player.getUniqueID()))
            {
                if (stack.getMaxStackSize() == stack.getCount())
                    continue;
                if (stack.isItemEqual(is) && ItemStack.areItemStackTagsEqual(stack, is))
                {
                    int space = stack.getMaxStackSize() - stack.getCount();

                    if (is.getCount() <= space)
                    {
                        stack.grow(is.getCount());
                        is.setCount(0);
                        break;
                    }
                    else
                    {
                        stack.grow(space);
                        is.shrink(space);
                        continue;
                    }
                }
            }

            if (is.getCount() > 0)
                orb.get(player.getUniqueID()).add(is);
        }
    }

    public static void finishOrb(EntityPlayer player)
    {
        ItemStack is = new ItemStack(Items.drop_orb());
        Items.drop_orb().addItems(is, orb.get(player.getUniqueID()));
        orb.remove(player.getUniqueID());
        PlayerUtils.putItemWithDrop(player, is);
    }

    public static boolean isRealPlayer(EntityPlayer p)
    {
        return !PlayerHelper.isFakePlayer(p);
    }
}
