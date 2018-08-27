package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.ItemBindableBase;
import WayofTime.bloodmagic.util.helper.TextHelper;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsRebuilding;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;

public class ItemRebuilding extends ItemBindableBase implements ISigil
{
    public static final String              ID              =       "sigil_rebuilding";
    public static final ResourceLocation    RL              = new   ResourceLocation(Constants.Metadata.MOD_ID, ID);
    public static final int                 TICKS_PER_OP    =       10;

    public static final String              NBT_STATE       =       "state";


    public ItemRebuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName(ID);
        this.setRegistryName(RL);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.rebuilding"));
        tooltip.add("");

        Binding binding = getBinding(stack);

        if (binding != null)
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", binding.getOwnerName()));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        IBlockState s = getNewBlock(stack);
        boolean b = s == null; // check is used numerous times
        ItemStack is = b
                ? null
                : new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s));

        tooltip.add("");
        tooltip.add(TextHelper.localize(b ? "tooltip.se.rebuilding.block.null" : "tooltip.se.rebuilding.block", b ? "" : is.getDisplayName()));
        tooltip.add(TextHelper.localize(b ? "tooltip.se.rebuilding.meta.null" : "tooltip.se.rebuilding.meta", "" + (b ? "" : is.getMetadata())));
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return EnumActionResult.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);
        final Binding bind = BloodUtils.getOrBind(stack, player);

        if (bind == null)
            return EnumActionResult.FAIL;

        if (player.isSneaking())
        {
            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());

            IBlockState b = w.getBlockState(pos);
            stack.getTagCompound().setInteger(NBT_STATE, Block.getStateId(b));
            return EnumActionResult.SUCCESS;
        }
        else
        {
            Map<Integer, Set<BlockPos>> toReplace = UtilsRebuilding.find(pos, w);
            if (getNewBlock(stack) != null)
                UtilsRebuilding.doReplace(player, bind.getOwnerId(), toReplace, w, w.getBlockState(pos), getNewBlock(stack), hand);
            return EnumActionResult.SUCCESS;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
    {
        World w = player.getEntityWorld();
        if (w.isRemote)
            return true;

        final Binding bind = BloodUtils.getOrBind(stack, player);
        if (bind == null)
            return false;

        if (getNewBlock(stack) != null)
            UtilsRebuilding.doReplace(player, bind.getOwnerId(),  pos, w, w.getBlockState(pos), getNewBlock(stack), EnumHand.MAIN_HAND);
        return true;
    }

    private static IBlockState getNewBlock(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(NBT_STATE))
        {
            return Block.getStateById(stack.getTagCompound().getInteger(NBT_STATE));
        }
        return null;
    }
}
