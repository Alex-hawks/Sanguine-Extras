package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsRebuilding;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.Vector3;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;

public class ItemRebuilding extends Item implements IBindable
{
    public ItemRebuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilRebuilding");
        this.setTextureName("SanguineExtras:sigilRebuilding");
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add("§5§o" + translate("pun.se.sigil.rebuilding"));
        par3List.add("");

        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));

        ItemStack is = new ItemStack(getNewBlock(stack), 1, getNewMeta(stack));
        boolean b = getNewBlock(stack) == null;

        par3List.add("");
        par3List.add(translate(b ? "tooltip.se.rebuilding.block.null" : "tooltip.se.rebuilding.block").replace("%s", b ? "" : is.getDisplayName()));
        par3List.add(translate(b ? "tooltip.se.rebuilding.meta.null" : "tooltip.se.rebuilding.meta").replace("%s", "" + (b ? "" : getNewMeta(stack))));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;
        EnergyItems.checkAndSetItemOwner(stack, player);

        if (player.isSneaking())
        {
            if (stack.stackTagCompound == null)
                stack.stackTagCompound = new NBTTagCompound();

            Block b = w.getBlock(x, y, z);
            NBTTagCompound tag = stack.getTagCompound();

            NBTTagCompound blockTag = new NBTTagCompound();
            blockTag.setString("blockId", Block.blockRegistry.getNameForObject(b));
            blockTag.setInteger("meta", w.getBlockMetadata(x, y, z));
            tag.setTag("newBlock", blockTag);
            stack.setTagCompound(tag);
            return true;
        }
        else 
        {
            String sigilOwner = stack.stackTagCompound.getString("ownerName");
            List<Vector3> toReplace = UtilsRebuilding.find(x, y, z, w);
            UtilsRebuilding.doReplace(player, sigilOwner, toReplace, w, w.getBlock(x, y, z), w.getBlockMetadata(x, y, z), getNewBlock(stack), getNewMeta(stack));
            return true;
        }
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World w = player.worldObj;
        if (w.isRemote)
            return true;

        EnergyItems.checkAndSetItemOwner(stack, player);
        
        String sigilOwner = stack.stackTagCompound.getString("ownerName");
        Vector3[] toReplace = new Vector3[] { new Vector3(x, y, z) };
        UtilsRebuilding.doReplace(player, sigilOwner, toReplace, w, w.getBlock(x, y, z), w.getBlockMetadata(x, y, z), getNewBlock(stack), getNewMeta(stack));
        return true;
    }

    private static Block getNewBlock(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("newBlock"))
        {
            NBTTagCompound blockTag = stack.stackTagCompound.getCompoundTag("newBlock");
            return (Block) Block.blockRegistry.getObject(blockTag.getString("blockId"));
        }
        return null;
    }

    private static int getNewMeta(ItemStack stack)
    {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("newBlock"))
        {
            NBTTagCompound blockTag = stack.stackTagCompound.getCompoundTag("newBlock");
            return blockTag.getInteger("meta");
        }
        return 0;
    }
}
