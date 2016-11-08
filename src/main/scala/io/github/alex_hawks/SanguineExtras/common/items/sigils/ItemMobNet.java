package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.impl.ItemBindable;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.util.ChatUtil;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsMobNet;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static io.github.alex_hawks.SanguineExtras.api.sigil.MobNet.isCaptureBlacklisted;
import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;

public class ItemMobNet extends ItemBindable
{
    public ItemMobNet()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilMobNet");
        this.setRegistryName("sigilMobNet");
        this.setHasSubtypes(true);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.mobnet"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        tooltip.add("");

        String s = UtilsMobNet.getEntityName(stack);
        tooltip.add(TextHelper.localize(s == null ? "tooltip.se.mobnet.mob.null" : "tooltip.se.mobnet.mob").replace("%s", TextHelper.localize(s == null ? "" : s)));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand)
    {
        if (isCaptureBlacklisted(target.getClass()))
        {
            return true;
        }
        else if (stack.getTagCompound().hasKey("entityClass") || stack.getTagCompound().hasKey("entity"))
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.full");
            return true;
        }
        else if (!target.isNonBoss() && !SanguineExtras.trappableBossMobs)
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.boss");
            return true;
        }
        else if (target instanceof EntityPlayer)
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.boss");
            return true;
        }
        else if (player.capabilities.isCreativeMode)
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.creative");
            return true;
        }

        System.out.println("Stack" + getOwnerUUID(stack));
        System.out.println("Player" + player.getUniqueID().toString());

        if (BloodUtils.drainSoulNetworkWithDamage(getOwnerUUID(stack), player, target.isNonBoss() ? 1000 : 10000))
        {
            NBTTagCompound tag = new NBTTagCompound();
            target.writeToNBT(tag);

            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setTag("entity", tag);
            stack.getTagCompound().setString("entityClass", target.getClass().getName());
            stack.getTagCompound().setBoolean("isBoss", !target.isNonBoss());
            stack.getTagCompound().setString("entityName", "entity." + EntityList.getEntityString(target) + ".name");
            stack.setItemDamage(1);

            target.worldObj.removeEntity(target);
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World w, EntityPlayer player, EnumHand hand)
    {
        if (w.isRemote)
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(player.posX + w.rand.nextDouble() - 0.5, player.posY, player.posZ + w.rand.nextDouble() - 0.5);
        w.spawnEntityInWorld(ent);

        stack.getTagCompound().removeTag("entity");
        stack.getTagCompound().removeTag("entityClass");
        stack.getTagCompound().removeTag("isBoss");
        stack.getTagCompound().removeTag("entityName");
        stack.setItemDamage(0);

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return EnumActionResult.SUCCESS;
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
            return EnumActionResult.SUCCESS;

        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(pos.getX() + side.getFrontOffsetX() + hitX, pos.getY() + side.getFrontOffsetY(), pos.getZ() + side.getFrontOffsetZ() + hitZ);
        w.spawnEntityInWorld(ent);

        stack.getTagCompound().removeTag("entity");
        stack.getTagCompound().removeTag("entityClass");
        stack.getTagCompound().removeTag("isBoss");
        stack.getTagCompound().removeTag("entityName");
        return EnumActionResult.SUCCESS;
    }
}
