package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.api.MobNetBlacklist.isCaptureBlacklisted;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;

public class ItemMobNet extends Item implements IBindable 
{
    public ItemMobNet()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilMobNet");
    }
    
	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add("§5§o" + translate("pun.se.sigil.mobnet"));
        par3List.add("");

        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));

        par3List.add("");
        String s = getEntityName(stack);
        par3List.add(translate(s == null ? "tooltip.se.mobnet.mob.null" : "tooltip.se.mobnet.mob").replace("%s", translate(s == null ? "" : s)));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack1, EntityPlayer player, EntityLivingBase target)
    {
        ItemStack stack = player.inventory.getCurrentItem();
        
        EnergyItems.checkAndSetItemOwner(stack, player);

        if (player.worldObj.isRemote)
        {
            if (stack.stackTagCompound.hasKey("entityClass") || stack.stackTagCompound.hasKey("entity"))
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.full")));
            if (target instanceof IBossDisplayData && !SanguineExtras.trappableBossMobs)
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.boss")));
            if (target instanceof EntityPlayer)
                player.addChatComponentMessage(new ChatComponentText(translate("msg.se.fail.sigil.mobnet.boss")));
            return true;
        }
        
        if (isCaptureBlacklisted(target.getClass()))
            return true;
        if (stack.stackTagCompound.hasKey("entityClass") || stack.stackTagCompound.hasKey("entity"))
            return true;
        if (target instanceof IBossDisplayData && !SanguineExtras.trappableBossMobs)
            return true;
        if (target instanceof EntityPlayer)
            return true;
        
        String sigilOwner = stack.stackTagCompound.getString("ownerName");
        if (BloodUtils.drainSoulNetworkWithDamage(sigilOwner, player, target instanceof IBossDisplayData ? 10000 : 1000))
        {
            NBTTagCompound tag = new NBTTagCompound();
            target.writeToNBT(tag);

            target.worldObj.removeEntity(target);

            if (stack.stackTagCompound == null)
                stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setTag("entity", tag);
            stack.stackTagCompound.setString("entityClass", target.getClass().getName());
            stack.stackTagCompound.setBoolean("isBoss", target instanceof IBossDisplayData);
            stack.stackTagCompound.setString("entityName", "entity." + EntityList.getEntityString(target) + ".name");
        }
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
    {
        if (w.isRemote)
            return stack;
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("entityClass") || !stack.stackTagCompound.hasKey("entity"))
            return stack;

        EnergyItems.checkAndSetItemOwner(stack, player);
        
        EntityLivingBase ent = createNewEntity(stack, w);
        ent.setPosition(player.posX + w.rand.nextDouble() - 0.5, player.posY, player.posZ + w.rand.nextDouble() - 0.5);
        w.spawnEntityInWorld(ent);

        stack.stackTagCompound.removeTag("entity");
        stack.stackTagCompound.removeTag("entityClass");
        stack.stackTagCompound.removeTag("isBoss");
        stack.stackTagCompound.removeTag("entityName");;
        return stack;
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("entityClass") || !stack.stackTagCompound.hasKey("entity"))
            return true;
        
        ForgeDirection d = ForgeDirection.getOrientation(side);
        
        EnergyItems.checkAndSetItemOwner(stack, player);
        
        EntityLivingBase ent = createNewEntity(stack, w);
        ent.setPosition(x + d.offsetX + hitX, y + d.offsetY, z + d.offsetZ + hitZ);
        w.spawnEntityInWorld(ent);

        stack.stackTagCompound.removeTag("entity");
        stack.stackTagCompound.removeTag("entityClass");
        stack.stackTagCompound.removeTag("isBoss");
        stack.stackTagCompound.removeTag("entityName");;
        return true;
    }
    
    public static String getEntityName(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("entityName"))
            return stack.stackTagCompound.getString("entityName");
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public static EntityLivingBase createNewEntity(ItemStack stack, World w)
    {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("entityClass") || !stack.stackTagCompound.hasKey("entity"))
        {
            return null;
        }
        else
        {
            try
            {
                Class<? extends EntityLivingBase> clazz = (Class<? extends EntityLivingBase>) Class.forName(stack.stackTagCompound.getString("entityClass"));
                Constructor<? extends EntityLivingBase> constructor = clazz.getConstructor(World.class);
                
                EntityLivingBase newmob = constructor.newInstance(w);
                newmob.readFromNBT(stack.stackTagCompound.getCompoundTag("entity"));
                return newmob;
            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (SecurityException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
