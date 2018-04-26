package io.github.alex_hawks.SanguineExtras.common.util;

import io.github.alex_hawks.SanguineExtras.common.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SanguineExtrasCreativeTab extends CreativeTabs
{
    public static final SanguineExtrasCreativeTab Instance = new SanguineExtrasCreativeTab("tabSanguineExtras");

    public SanguineExtrasCreativeTab(String label)
    {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem()
    {
      return new ItemStack(Items.sigil_rebuild(), 1, 0);
    }
}
