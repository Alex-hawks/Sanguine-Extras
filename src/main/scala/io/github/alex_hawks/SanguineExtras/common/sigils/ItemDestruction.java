package io.github.alex_hawks.SanguineExtras.common.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.item.ItemBindable;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsDestruction;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.Vector3;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

public class ItemDestruction extends ItemBindable
{
    public ItemDestruction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilDestruction");
        this.setRegistryName("sigilDestruction");
    }

    @Override
    public String getUnlocalizedName(ItemStack is)
    {
        return this.getUnlocalizedName() + ".tier" + is.getItemDamage();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + translate("pun.se.sigil.destruction"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));

        tooltip.add("");

        tooltip.add(translate("tooltip.se.destruction.currentlength").replace("%s", "" + getLength(stack)));
        tooltip.add(translate("tooltip.se.destruction.maximumlength").replace("%s", "" + getMaxLength(stack)));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;

        if (player.isSneaking())
        {
            int l = getLength(stack);
            if (l * 4 <= getMaxLength(stack))
                setLength(stack, l * 4);
            else
                setLength(stack, 1);
            return true;
        } else
        {
            List<Vector3> toBreak = UtilsDestruction.find(pos, w, side, getLength(stack));

            UtilsDestruction.doDrops(player, UUID.fromString(this.getBindableOwner(stack)), toBreak, w);
            return true;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
    {
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (int i = 0; i < BloodUtils.getHighestTierOrb(); i++)
            items.add(new ItemStack(item, 1, i));
    }

    private static int getLength(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("length"))
        {
            return stack.getTagCompound().getInteger("length");
        }
        return 1;
    }

    private static int getMaxLength(ItemStack stack)
    {
        return (int) Math.round(Math.pow(4, stack.getItemDamage()));
    }

    private static void setLength(ItemStack stack, int length)
    {
        if (stack.getTagCompound() != null)
        {
            stack.getTagCompound().setInteger("length", length);
        }
    }
}
