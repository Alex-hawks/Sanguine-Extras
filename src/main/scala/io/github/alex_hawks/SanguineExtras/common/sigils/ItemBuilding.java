package io.github.alex_hawks.SanguineExtras.common.sigils;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.Vector3;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;

public class ItemBuilding extends Item implements IBindable
{
    public ItemBuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilBuilding");
        this.setTextureName("SanguineExtras:sigilBuilding");
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add(MODULE$.loreFormat() + translate("pun.se.sigil.building"));
        par3List.add("");
        
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ownerName"))
            par3List.add(translate("tooltip.se.owner").replace("%s", stack.stackTagCompound.getString("ownerName")));
        else
            par3List.add(translate("tooltip.se.owner.null"));
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        EnergyItems.checkAndSetItemOwner(stack, player);
        
        Set<Vector3> ls = UtilsBuilding.getBlocksForBuild(w, new Vector3(x, y, z), ForgeDirection.getOrientation(side), player, 9);
        
        PlaceEvent e;
        
        for (Vector3 v : ls)
        {
            e = new PlaceEvent(new BlockSnapshot(w, v.x, v.y, v.z, w.getBlock(x, y, z), w.getBlockMetadata(x, y, z)), null, player);
            if (!MinecraftForge.EVENT_BUS.post(e))
            {
                if (BloodUtils.drainSoulNetworkWithDamage(stack.stackTagCompound.getString("ownerName"), player, SanguineExtras.rebuildSigilCost)
                        && PlayerUtils.takeItem(player, new ItemStack(w.getBlock(x, y, z), 1, w.getBlockMetadata(x, y, z))))
                {
                    w.setBlock(v.x, v.y, v.z, w.getBlock(x, y, z), w.getBlockMetadata(x, y, z), 0x3);
                }
            }
        }
        
        return true;
    }
}
