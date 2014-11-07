package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsDestruction;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.Vector3;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDestruction extends Item implements IBindable 
{
    public ItemDestruction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilDestruction");
        this.setTextureName("SanguineExtras:sigilDestruction");
    }
    
    @Override
    public String getUnlocalizedName(ItemStack is)
    {
        return this.getUnlocalizedName() + ".tier" + is.getItemDamage();
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add("§5§o" + translate("pun.se.sigil.destruction"));
        par3List.add("");

        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));

        par3List.add("");

        par3List.add(translate("tooltip.se.destruction.currentlength").replace("%s", "" + getLength(stack)));
        par3List.add(translate("tooltip.se.destruction.maximumlength").replace("%s", "" + getMaxLength(stack)));
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;
        EnergyItems.checkAndSetItemOwner(stack, player);

        if (player.isSneaking())
        {
            int l = getLength(stack);
            if (l * 4 <= getMaxLength(stack))
                setLength(stack, l * 4);
            else
                setLength(stack, 1);
            return true;
        }
        else
        {
            String sigilOwner = stack.stackTagCompound.getString("ownerName");
            List<Vector3> toBreak = UtilsDestruction.find(x, y, z, w, side, getLength(stack));
            for(Vector3 vec3 : toBreak)
                System.out.println(vec3);
            
            UtilsDestruction.doDrops(player, sigilOwner, toBreak, w);
            return true;
        }
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
    {
        EnergyItems.checkAndSetItemOwner(stack, player);
        
        if (player.isSneaking())
        {
            int l = getLength(stack);
            if (l * 4 <= getMaxLength(stack))
                setLength(stack, l * 4);
            else
                setLength(stack, 1);
        }
        return stack;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (int i = 0; i < BloodUtils.getHighestTierOrb(); i++)
            items.add(new ItemStack(item, 1, i));
    }
    
    private static int getLength(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("length"))
        {
            return stack.stackTagCompound.getInteger("length");
        }
        return 1;
    }
    
    private static int getMaxLength(ItemStack stack)
    {
        return (int) Math.round(Math.pow(4, stack.getItemDamage()));
    }
    
    private static void setLength(ItemStack stack, int length)
    {
        if (stack.stackTagCompound != null)
        {
            stack.stackTagCompound.setInteger("length", length);
        }
    }
}
