package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.api.sigil.MobNet.isCaptureBlacklisted;
import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsMobNet;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMobNet extends Item implements IBindable
{
    @SideOnly(Side.CLIENT)
    private IIcon emptyIcon;
    @SideOnly(Side.CLIENT)
    private IIcon fullIcon;
    
    public ItemMobNet()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilMobNet");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("SanguineExtras:sigilEntrapment.empty");
        this.emptyIcon = iconRegister.registerIcon("SanguineExtras:sigilEntrapment.empty");
        this.fullIcon = iconRegister.registerIcon("SanguineExtras:sigilEntrapment.full");
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        
        NBTTagCompound tag = stack.stackTagCompound;
        
        if (tag.hasKey("entityClass") || tag.hasKey("entity"))
        {
            return this.fullIcon;
        } else
        {
            return this.emptyIcon;
        }
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add(MODULE$.loreFormat() + translate("pun.se.sigil.mobnet"));
        par3List.add("");
        
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));
        
        par3List.add("");
        String s = UtilsMobNet.getEntityName(stack);
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
        
        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(player.posX + w.rand.nextDouble() - 0.5, player.posY, player.posZ + w.rand.nextDouble() - 0.5);
        w.spawnEntityInWorld(ent);
        
        stack.stackTagCompound.removeTag("entity");
        stack.stackTagCompound.removeTag("entityClass");
        stack.stackTagCompound.removeTag("isBoss");
        stack.stackTagCompound.removeTag("entityName");
        ;
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
        
        EntityLivingBase ent = UtilsMobNet.createCopiedEntity(stack, w);
        ent.setPosition(x + d.offsetX + hitX, y + d.offsetY, z + d.offsetZ + hitZ);
        w.spawnEntityInWorld(ent);
        
        stack.stackTagCompound.removeTag("entity");
        stack.stackTagCompound.removeTag("entityClass");
        stack.stackTagCompound.removeTag("isBoss");
        stack.stackTagCompound.removeTag("entityName");
        ;
        return true;
    }
}
