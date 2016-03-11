package io.github.alex_hawks.SanguineExtras.common.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.item.ItemBindable;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsMobNet;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.api.sigil.MobNet.isCaptureBlacklisted;
import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

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
        tooltip.add(MODULE$.loreFormat() + translate("pun.se.sigil.mobnet"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));

        tooltip.add("");
        String s = UtilsMobNet.getEntityName(stack);
        tooltip.add(translate(s == null ? "tooltip.se.mobnet.mob.null" : "tooltip.se.mobnet.mob").replace("%s", translate(s == null ? "" : s)));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target)
    {
        if (player.worldObj.isRemote)
        {
            if (stack.getTagCompound().hasKey("entityClass") || stack.getTagCompound().hasKey("entity"))
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.full")));
            if (target instanceof IBossDisplayData && !SanguineExtras.trappableBossMobs)
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.boss")));
            if (target instanceof EntityPlayer)
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.boss")));
            return true;
        }

        if (isCaptureBlacklisted(target.getClass()))
            return true;
        if (stack.getTagCompound().hasKey("entityClass") || stack.getTagCompound().hasKey("entity"))
            return true;
        if (target instanceof IBossDisplayData && !SanguineExtras.trappableBossMobs)
            return true;
        if (target instanceof EntityPlayer)
            return true;

        if (BloodUtils.drainSoulNetworkWithDamage(UUID.fromString(getBindableOwner(stack)), player, target instanceof IBossDisplayData ? 10000 : 1000))
        {
            NBTTagCompound tag = new NBTTagCompound();
            target.writeToNBT(tag);

            target.worldObj.removeEntity(target);

            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setTag("entity", tag);
            stack.getTagCompound().setString("entityClass", target.getClass().getName());
            stack.getTagCompound().setBoolean("isBoss", target instanceof IBossDisplayData);
            stack.getTagCompound().setString("entityName", "entity." + EntityList.getEntityString(target) + ".name");
            stack.setItemDamage(1);
        }
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
    {
        if (w.isRemote)
            return stack;
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
            return stack;

        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(player.posX + w.rand.nextDouble() - 0.5, player.posY, player.posZ + w.rand.nextDouble() - 0.5);
        w.spawnEntityInWorld(ent);

        stack.getTagCompound().removeTag("entity");
        stack.getTagCompound().removeTag("entityClass");
        stack.getTagCompound().removeTag("isBoss");
        stack.getTagCompound().removeTag("entityName");
        stack.setItemDamage(0);

        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("entityClass") || !stack.getTagCompound().hasKey("entity"))
            return true;

        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(pos.getX() + side.getFrontOffsetX() + hitX, pos.getY() + side.getFrontOffsetY(), pos.getZ() + side.getFrontOffsetZ() + hitZ);
        w.spawnEntityInWorld(ent);

        stack.getTagCompound().removeTag("entity");
        stack.getTagCompound().removeTag("entityClass");
        stack.getTagCompound().removeTag("isBoss");
        stack.getTagCompound().removeTag("entityName");
        return true;
    }
}
