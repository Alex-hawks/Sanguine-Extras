package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.client.IMeshProvider;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.ItemBindableBase;
import WayofTime.bloodmagic.util.ChatUtil;
import WayofTime.bloodmagic.util.helper.TextHelper;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import io.github.alex_hawks.SanguineExtras.common.util.config.CaptureEntry;
import io.github.alex_hawks.SanguineExtras.common.util.config.Overrides;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsMobNet;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.config.Overrides.Capture.INSTANCE;

public class ItemMobNet extends ItemBindableBase implements ISigil, IMeshProvider
{
    public static final String           ID         =       "sigil_mob_net";
    public static final ResourceLocation RL         = new   ResourceLocation(Constants.Metadata.MOD_ID, ID);
    public static final String           NBT_ID     =       "full";

    public static final String NBT_ENT_NAME         =       "entityName";
    public static final String NBT_ENT_ID           =       "entityID";
    public static final String NBT_ENT_BOSS         =       "isBoss";
    public static final String NBT_ENT              =       "entity";

    private final ModelResourceLocation full        = new   ModelResourceLocation(RL, NBT_ID + "=true");
    private final ModelResourceLocation empty       = new   ModelResourceLocation(RL, NBT_ID + "=false");

    public ItemMobNet()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName(ID);
        this.setRegistryName(RL);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.mobnet"));
        tooltip.add("");

        Binding binding = getBinding(stack);

        if (binding != null)
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", binding.getOwnerName()));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        tooltip.add("");

        String s = UtilsMobNet.getEntityName(stack);
        tooltip.add(TextHelper.localize(s == null ? "tooltip.se.mobnet.mob.null" : "tooltip.se.mobnet.mob").replace("%s", TextHelper.localize(s == null ? "" : s)));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand)
    {
        final CaptureEntry data = INSTANCE.getData().get(Overrides.getEntityID(target));
        if (data == null || !data.enabled())
        {
            return true;
        }
        else if (stack.getSubCompound(NBT_ENT) != null)
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.full");
            return true;
        }
        else if (!target.isNonBoss() && !Base.sigil.holding.trappableBosses)
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
        else if (data.isPercentage() && (target.getHealth() / target.getMaxHealth() * 100.0) > data.maxHealth())
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.health");
            return true;
        }
        else if(target.getHealth() > data.maxHealth())
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.health");
            return true;
        }

        Binding bd = BloodUtils.getOrBind(stack, player);
        ResourceLocation loc = EntityList.getKey(target);
        if (loc == null)
        {
            ChatUtil.sendNoSpamUnloc(player, "msg.se.fail.sigil.mobnet.internal");
            return true;
        }

        if (BloodUtils.drainSoulNetworkWithDamage(bd.getOwnerId(), Base.sigil.holding.cost * (target.isNonBoss() ? 1 : 10), player))
        {
            NBTTagCompound tag = new NBTTagCompound();
            target.writeToNBT(tag);

            if (stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setTag(NBT_ENT, tag);
            stack.getTagCompound().setString(NBT_ENT_ID, loc.toString());
            stack.getTagCompound().setBoolean(NBT_ENT_BOSS, !target.isNonBoss());
            stack.getTagCompound().setString(NBT_ENT_NAME, "entity." + EntityList.getEntityString(target) + ".name");
            stack.setItemDamage(1);

            target.getEntityWorld().removeEntity(target);
        }
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer player, EnumHand hand)
    {
        final ItemStack stack = player.getHeldItem(hand);

        if (w.isRemote)
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(NBT_ENT_ID) || !stack.getTagCompound().hasKey(NBT_ENT))
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        Entity ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(player.posX + w.rand.nextDouble() - 0.5, player.posY, player.posZ + w.rand.nextDouble() - 0.5);
        w.spawnEntity(ent);

        stack.getTagCompound().removeTag(NBT_ENT);
        stack.getTagCompound().removeTag(NBT_ENT_ID);
        stack.getTagCompound().removeTag(NBT_ENT_BOSS);
        stack.getTagCompound().removeTag(NBT_ENT_NAME);
        stack.setItemDamage(0);

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        final ItemStack stack = player.getHeldItem(hand);

        if (w.isRemote)
            return EnumActionResult.SUCCESS;
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(NBT_ENT_ID) || !stack.getTagCompound().hasKey(NBT_ENT))
            return EnumActionResult.SUCCESS;

        Entity ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(pos.getX() + side.getFrontOffsetX() + hitX, pos.getY() + side.getFrontOffsetY(), pos.getZ() + side.getFrontOffsetZ() + hitZ);
        w.spawnEntity(ent);

        stack.getTagCompound().removeTag(NBT_ENT);
        stack.getTagCompound().removeTag(NBT_ENT_ID);
        stack.getTagCompound().removeTag(NBT_ENT_BOSS);
        stack.getTagCompound().removeTag(NBT_ENT_NAME);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ItemMeshDefinition getMeshDefinition()
    {
        return stack -> stack.getSubCompound(NBT_ENT) != null
                ? ItemMobNet.this.full
                : ItemMobNet.this.empty;

    }

    @Override
    public void gatherVariants(Consumer<String> ls)
    {
        ls.accept("full=true");
        ls.accept("full=false");
    }
}
