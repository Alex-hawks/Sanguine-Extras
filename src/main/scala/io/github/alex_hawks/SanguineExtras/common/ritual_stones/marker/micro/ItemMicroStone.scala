package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import net.minecraft.item.Item
import codechicken.multipart.TItemMultiPart
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import codechicken.lib.vec.BlockCoord
import codechicken.lib.vec.Vector3
import codechicken.multipart.TMultiPart
import codechicken.multipart.MultiPartRegistry
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab

class ItemMicroStone extends Item with TItemMultiPart {

  setCreativeTab(SanguineExtrasCreativeTab.Instance);
  this.setUnlocalizedName("partMicroStone");
  this.setTextureName("SanguineExtras:sigilBuilding");

  override def newPart(item: ItemStack, player: EntityPlayer, world: World, pos: BlockCoord, side: Int, vhit: Vector3): TMultiPart = {
    return MultiPartRegistry.createPart("MicroStone", false)
  }
}