package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.alex_hawks.SanguineExtras.api.sigil.Interdiction;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.network.entity_motion.MsgEntityMotion;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;

public class ItemInterdiction extends Item implements IBindable
{
    @SideOnly(Side.CLIENT)
    private IIcon passiveIcon;
    @SideOnly(Side.CLIENT)
    private IIcon activeIcon;
    
    public ItemInterdiction()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilInterdiction");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("SanguineExtras:sigilInterdiction.passive");
        this.passiveIcon = iconRegister.registerIcon("SanguineExtras:sigilInterdiction.passive");
        this.activeIcon = iconRegister.registerIcon("SanguineExtras:sigilInterdiction.active");
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        
        NBTTagCompound tag = stack.stackTagCompound;
        
        if (tag.getBoolean("isActive"))
        {
            return this.activeIcon;
        } else
        {
            return this.passiveIcon;
        }
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
    {
        EnergyItems.checkAndSetItemOwner(stack, player);
        
        stack.stackTagCompound.setBoolean("isActive", !stack.stackTagCompound.getBoolean("isActive"));
        
        return stack;
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add("§5§o" + translate("pun.se.sigil.interdiction"));
        par3List.add("");

        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));
        
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("isActive") && stack.stackTagCompound.getBoolean("isActive"))
            par3List.add(translate("tooltip.se.sigil.active"));
        else
            par3List.add(translate("tooltip.se.sigil.inactive"));
    }
    
    @Override
    public void onUpdate(ItemStack stack, World w, Entity ent, int par4, boolean par5)
    {
        if (!(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName")))
            return;
        
        String sigilOwner = stack.stackTagCompound.getString("ownerName");
        if (!(ent instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer p = (EntityPlayer) ent;

        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (stack.stackTagCompound.getBoolean("isActive"))
        {
            List<?> l = w.getEntitiesWithinAABB(Entity.class, getInterdictionAABB(p, SanguineExtras.interdictionRange));
            
            for (Object o : l)  //  Stupid lack of typing... You don't support Java 5 anymore, Mojang
            {
                if (o instanceof Entity && Interdiction.isPushAllowed((Entity) o, p))
                {
                    Entity e = (Entity) o;

                    p.addPotionEffect(new PotionEffect(AlchemicalWizardry.customPotionProjProt.id, 2, 1));  //  #ImLazy
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

        if (w.getWorldTime() % 200 == stack.stackTagCompound.getInteger("worldTimeDelay") && stack.stackTagCompound.getBoolean("isActive"))
        {
            if (!p.capabilities.isCreativeMode)
            {
                if (!BloodUtils.drainSoulNetworkWithDamage(sigilOwner, p, 200))
                {
                    // if code here is executed, something went horribly wrong...
                }
            }
        }

        return;
    }
    
    static AxisAlignedBB getInterdictionAABB(Entity e, double range)
    {
        return AxisAlignedBB.getBoundingBox(e.posX - range, e.posY - range, e.posZ - range, e.posX + range, e.posY + range, e.posZ + range);
    }
}
