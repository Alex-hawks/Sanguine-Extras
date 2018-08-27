package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.client.IMeshProvider;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.item.ItemBindableBase;
import WayofTime.bloodmagic.util.helper.TextHelper;
import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.MsgEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import io.github.alex_hawks.SanguineExtras.common.util.config.InterdictionEntry;
import io.github.alex_hawks.SanguineExtras.common.util.config.Overrides;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.config.Overrides.Interdiction.INSTANCE;

public class ItemInterdiction extends ItemBindableBase implements IMeshProvider
{
    public static final String ID                   =       "sigil_interdiction";
    public static final ResourceLocation RL         = new   ResourceLocation(Constants.Metadata.MOD_ID, ID);
    public static final String NBT_ID               =       "active";

    private final ModelResourceLocation active      = new   ModelResourceLocation(RL, NBT_ID + "=true");
    private final ModelResourceLocation inactive    = new   ModelResourceLocation(RL, NBT_ID + "=false");

    public ItemInterdiction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName(ID);
        this.setRegistryName(RL);
        this.setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        toggleState(stack);

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.interdiction"));
        tooltip.add("");

        Binding binding = getBinding(stack);

        if (binding != null)
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", binding.getOwnerName()));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        tooltip.add("");

        if (isActive(stack))
            tooltip.add(TextHelper.localize("tooltip.se.sigil.active"));
        else
            tooltip.add(TextHelper.localize("tooltip.se.sigil.inactive"));
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity ent, int par4, boolean par5)
    {
        if (!(ent instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer p = (EntityPlayer) ent;
        final Binding bind = BloodUtils.getOrBind(stack, p);

        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (isActive(stack))
        {
            List<Entity> l = w.getEntitiesWithinAABB(Entity.class, getInterdictionAABB(p, Base.sigil.interdiction.range));

            for (Entity e : l)
            {
                final InterdictionEntry data = INSTANCE.getData().get(Overrides.getEntityID(e));
                // TODO fix to actually use the new filtering system

                if (push(e, p))
                {


                    if (!e.getEntityWorld().isRemote)
                        SanguineExtras.networkWrapper.sendToAll(new MsgEntityMotion(e));
                }
            }
        }

        if (w.getWorldTime() % 200 == stack.getTagCompound().getInteger("worldTimeDelay") && isActive(stack))
        {
            BloodUtils.drainSoulNetworkWithDamage(bind.getOwnerId(), Base.sigil.interdiction.cost, p);
        }
    }

    private static AxisAlignedBB getInterdictionAABB(Entity e, double range)
    {
        return new AxisAlignedBB(e.posX - range, e.posY - range, e.posZ - range, e.posX + range, e.posY + range, e.posZ + range);
    }

    @Override
    public ItemMeshDefinition getMeshDefinition()
    {
        return stack -> isActive(stack)
                ? ItemInterdiction.this.active
                : ItemInterdiction.this.inactive;
    }

    @Override
    public void gatherVariants(Consumer<String> ls)
    {
        ls.accept(NBT_ID + "=true");
        ls.accept(NBT_ID + "=false");
    }

    private static boolean push(Entity target, EntityPlayer player)
    {
        final InterdictionEntry data = INSTANCE.getData().get(Overrides.getEntityID(target));
        if (data == null) return false;
        final Map<String, IPushCondition> filters = INSTANCE.getFilters();
        IPushCondition.Push tmp, result = IPushCondition.Push.IGNORE;

        for (String name : data.enabledFilters())
        {
            IPushCondition filter = filters.get(name);
            tmp = filter.canPush(target, player);
            if (tmp.compareTo(result) > 0)
                result = tmp;
        }
        return result.push;
    }

    private static boolean isActive(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getTagId(NBT_ID) == NBT.TAG_BYTE && stack.getTagCompound().getBoolean(NBT_ID);
    }

    private static void toggleState(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setBoolean(NBT_ID, !isActive(stack));
    }
}
