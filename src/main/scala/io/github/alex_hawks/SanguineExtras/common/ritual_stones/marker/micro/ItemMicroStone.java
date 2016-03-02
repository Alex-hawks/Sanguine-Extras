package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro;

import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemMicroStone extends ItemMultiPart
{
    public ItemMicroStone()
    {
        this.setRegistryName(Constants.MetaData.MOD_ID, "microRitualStone");
        this.setUnlocalizedName("microRitualStone");
        this.setCreativeTab(SanguineExtrasCreativeTab.Instance);
    }

    @Override
    public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player)
    {
        return new MultipartStone();
    }

    @Override
    public Block.SoundType getPlacementSound(ItemStack stack) {

        return Block.soundTypeStone;
    }
}
