package io.github.alex_hawks.SanguineExtras.common.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.iface.ISigil;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.item.ItemBindable;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsRebuilding;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

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
        tooltip.add(MODULE$.loreFormat() + translate("pun.se.sigil.rebuilding"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));

        IBlockState s = getNewBlock(stack);
        boolean b = getNewBlock(stack) == null;
        ItemStack is = b ? null : new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s));

        tooltip.add("");
        tooltip.add(translate(b ? "tooltip.se.rebuilding.block.null" : "tooltip.se.rebuilding.block").replace("%s", b ? "" : is.getDisplayName()));
        tooltip.add(translate(b ? "tooltip.se.rebuilding.meta.null" : "tooltip.se.rebuilding.meta").replace("%s", "" + (b ? "" : is.getMetadata())));
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;

        if (player.isSneaking())
        {
            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());

            IBlockState b = w.getBlockState(pos);
            stack.getTagCompound().setInteger("state", Block.getStateId(b));
            return true;
        } else
        {
            List<Vector3> toReplace = UtilsRebuilding.find(pos, w);
            if (getNewBlock(stack) != null)
                UtilsRebuilding.doReplace(player, UUID.fromString(getBindableOwner(stack)), toReplace, w, w.getBlockState(pos), getNewBlock(stack));
            return true;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
    {
        World w = player.worldObj;
        if (w.isRemote)
            return true;

        Vector3[] toReplace = new Vector3[]{new Vector3(pos)};
        if (getNewBlock(stack) != null)
            UtilsRebuilding.doReplace(player, UUID.fromString(getBindableOwner(stack)), toReplace, w, w.getBlockState(pos), getNewBlock(stack));
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
