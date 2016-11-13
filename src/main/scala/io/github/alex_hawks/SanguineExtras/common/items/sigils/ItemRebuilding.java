package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.iface.ISigil;
import WayofTime.bloodmagic.api.impl.ItemBindable;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsRebuilding;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;

public class ItemRebuilding extends ItemBindable implements ISigil
{
    public ItemRebuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilRebuilding");
        this.setRegistryName("sigilRebuilding");
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.rebuilding"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        IBlockState s = getNewBlock(stack);
        boolean b = getNewBlock(stack) == null;
        ItemStack is = b ? null : new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s));

        tooltip.add("");
        tooltip.add(TextHelper.localize(b ? "tooltip.se.rebuilding.block.null" : "tooltip.se.rebuilding.block", b ? "" : is.getDisplayName()));
        tooltip.add(TextHelper.localize(b ? "tooltip.se.rebuilding.meta.null" : "tooltip.se.rebuilding.meta", "" + (b ? "" : is.getMetadata())));
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return EnumActionResult.SUCCESS;

        if (player.isSneaking())
        {
            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());

            IBlockState b = w.getBlockState(pos);
            stack.getTagCompound().setInteger("state", Block.getStateId(b));
            return EnumActionResult.SUCCESS;
        }
        else
        {
            Map<Integer, Set<Vector3>> toReplace = UtilsRebuilding.find(pos, w);
            if (getNewBlock(stack) != null)
                UtilsRebuilding.doReplace(player, getOwnerUUID(stack), toReplace, w, w.getBlockState(pos), getNewBlock(stack));
            return EnumActionResult.SUCCESS;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
    {
        World w = player.worldObj;
        if (w.isRemote)
            return true;

        if (getNewBlock(stack) != null)
            UtilsRebuilding.doReplace(player, getOwnerUUID(stack), new Vector3(pos), w, w.getBlockState(pos), getNewBlock(stack));
        return true;
    }

    private static IBlockState getNewBlock(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("state"))
        {
            return Block.getStateById(stack.getTagCompound().getInteger("state"));
        }
        return null;
    }
}
