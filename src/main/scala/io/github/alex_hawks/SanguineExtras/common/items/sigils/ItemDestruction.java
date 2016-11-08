package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.impl.ItemBindable;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsDestruction;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;

public class ItemDestruction extends ItemBindable
{
    public ItemDestruction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilDestruction");
        this.setRegistryName("sigilDestruction");
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack is)
    {
        return this.getUnlocalizedName() + ".tier" + is.getItemDamage();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.destruction"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        tooltip.add("");

        tooltip.add(TextHelper.localize("tooltip.se.destruction.currentlength", "" + getLength(stack)));
        tooltip.add(TextHelper.localize("tooltip.se.destruction.maximumlength", "" + getMaxLength(stack)));
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return EnumActionResult.SUCCESS;

        if (player.isSneaking())
        {
            int l = getLength(stack);
            if (l * 4 <= getMaxLength(stack))
                setLength(stack, l * 4);
            else
                setLength(stack, 1);
            return EnumActionResult.SUCCESS;
        } else
        {
            List<Vector3> toBreak = UtilsDestruction.find(pos, w, side, getLength(stack));

            UtilsDestruction.doDrops(player, this.getOwnerUUID(stack), toBreak, w);
            return EnumActionResult.SUCCESS;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (player.isSneaking())
        {
            int l = getLength(stack);
            if (l * 4 <= getMaxLength(stack))
                setLength(stack, l * 4);
            else
                setLength(stack, 1);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (int i = 0; i < getMaxTier(); i++)
            items.add(new ItemStack(item, 1, i));
    }

    public int getMaxTier()
    {
        return BloodUtils.getHighestTierOrb();
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
