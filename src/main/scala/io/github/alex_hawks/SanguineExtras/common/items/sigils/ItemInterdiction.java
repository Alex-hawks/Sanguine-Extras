package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.impl.ItemBindable;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.registry.ModPotions;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.api.sigil.Interdiction;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.MsgEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;

public class ItemInterdiction extends ItemBindable
{

    public ItemInterdiction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilInterdiction");
        this.setRegistryName("sigilInterdiction");
        this.setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World w, EntityPlayer player, EnumHand hand)
    {
        boolean b = !stack.getTagCompound().getBoolean("isActive");
        stack.getTagCompound().setBoolean("isActive", b);

        stack.setItemDamage(b ? 1 : 0);

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.interdiction"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));

        tooltip.add("");

        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("isActive") && stack.getTagCompound().getBoolean("isActive"))
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

        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (stack.getTagCompound().getBoolean("isActive"))
        {
            List<Entity> l = w.getEntitiesWithinAABB(Entity.class, getInterdictionAABB(p, SanguineExtras.interdictionRange));

            for (Entity e : l)
            {
                if (Interdiction.isPushAllowed((Entity) e, p))
                {
                    p.addPotionEffect(new PotionEffect(ModPotions.whirlwind, 2, 1));  //  #ImLazy
//                    System.out.println("movX: " + e.motionX + ", calc: " + (p.posX - e.posX));
//                    System.out.println("movY: " + e.motionY + ", calc: " + (p.posY - e.posY));
//                    System.out.println("movZ: " + e.motionZ + ", calc: " + (p.posZ - e.posZ));
//                    
//                    System.out.println("x: " + (e.motionX < 0 && p.posX - e.posX > 0 || e.motionX > 0 && p.posX - e.posX < 0));
//                    System.out.println("z: " + (e.motionZ < 0 && p.posZ - e.posZ > 0 || e.motionZ > 0 && p.posZ - e.posZ < 0));
//                    
//                    if ((e.motionX < 0 && p.posX - e.posX < 0) || (e.motionX > 0 && p.posX - e.posX > 0))
//                        e.motionX = -e.motionX;
//                    
//                    if ((e.motionY < 0 && p.posY - e.posY < 0) || (e.motionY > 0 && p.posY - e.posY > 0))
//                        e.motionY = -e.motionY;
//                    
//                    if ((e.motionZ < 0 && p.posZ - e.posZ < 0) || (e.motionZ > 0 && p.posZ - e.posZ > 0))
//                        e.motionZ = -e.motionZ;

                    if (e instanceof IProjectile)
                        continue;

                    e.motionX -= (p.posX - e.posX);
                    e.motionY -= (p.posY - e.posY);
                    e.motionZ -= (p.posZ - e.posZ);

                    if (!e.worldObj.isRemote)
                        SanguineExtras.networkWrapper.sendToAll(new MsgEntityMotion(e));
                }
            }
        }

        if (w.getWorldTime() % 200 == stack.getTagCompound().getInteger("worldTimeDelay") && stack.getTagCompound().getBoolean("isActive"))
        {
            if (!p.capabilities.isCreativeMode)
            {
                if (!BloodUtils.drainSoulNetworkWithDamage(getOwnerUUID(stack), p, 200))
                {
                    // if code here is executed, something went horribly wrong...
                }
            }
        }

        return;
    }

    static AxisAlignedBB getInterdictionAABB(Entity e, double range)
    {
        return new AxisAlignedBB(e.posX - range, e.posY - range, e.posZ - range, e.posX + range, e.posY + range, e.posZ + range);
    }
}
