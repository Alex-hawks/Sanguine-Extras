package io.github.alex_hawks.SanguineExtras.common.util;

import io.github.alex_hawks.SanguineExtras.common.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SanguineExtrasCreativeTab extends CreativeTabs
{
    public static final SanguineExtrasCreativeTab Instance = new SanguineExtrasCreativeTab("tabSanguineExtras");

    public SanguineExtrasCreativeTab(String lable)
    {
        super(lable);
    }

    @Override
    public Item getTabIconItem()
    {
        return Items.SigilBuilding();
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return new ItemStack(Items.SigilRebuild(), 1, 0);
    }
}
