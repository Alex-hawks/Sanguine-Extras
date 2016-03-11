package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro;

import WayofTime.bloodmagic.api.ritual.EnumRuneType;
import WayofTime.bloodmagic.block.BlockRitualStone;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemMicroStone extends ItemMultiPart
{
    public ItemMicroStone()
    {
        this.setRegistryName(Constants.MetaData.MOD_ID, "microRitualStone");
        this.setUnlocalizedName("microRitualStone.");
        this.setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setHasSubtypes(true);
    }

    @Override
    public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player)
    {
        if (stack == null || stack.getItem() == null)
            return new MultipartStone();
        return new MultipartStone().initRuneType(EnumRuneType.byMetadata(stack.getMetadata()));
    }

    @Override
    public Block.SoundType getPlacementSound(ItemStack stack) {

        return Block.soundTypeStone;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (int i = 0; i < EnumRuneType.values().length; i++)
            items.add(new ItemStack(item, 1, i));
    }


    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + BlockRitualStone.names[stack.getItemDamage()];
    }

}
