package io.github.alex_hawks.SanguineExtras.common.compat

import baubles.api.BaublesApi
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

object Bauble {
  def isWearing(player: EntityPlayer, bauble: ItemStack, ignoreMeta: Boolean): (Boolean, ItemStack) = {
    val slot = BaublesApi.isBaubleEquipped(player, bauble.getItem)

    if (slot == -1)
        (false, ItemStack.EMPTY)
    else {
      val baubles = BaublesApi.getBaublesHandler(player)
      val is = baubles.getStackInSlot(slot)
  
      (true, is)
    }
  }
}
