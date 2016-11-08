package io.github.alex_hawks.SanguineExtras.common.util

import baubles.api.{BaublesApi, IBauble}
import baubles.api.cap.IBaublesItemHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

object Bauble {
  def isWearing(player: EntityPlayer, bauble: ItemStack, ignoreMeta: Boolean): (Boolean, ItemStack)= {
    if (!(bauble.getItem.isInstanceOf[IBauble]))
      return (false, null)

    val items: IBaublesItemHandler = BaublesApi.getBaublesHandler(player)

    for (i <- (bauble.getItem.asInstanceOf[IBauble]).getBaubleType(bauble).getValidSlots) {
      if (ignoreMeta) {
        if (bauble.getItem == items.getStackInSlot(i).getItem)
          return (true, items.getStackInSlot(i))
      }
      else {
        if (bauble.isItemEqual(items.getStackInSlot(i)))
          return (true, items.getStackInSlot(i))
      }
    }
    return (false, null)
  }
}
