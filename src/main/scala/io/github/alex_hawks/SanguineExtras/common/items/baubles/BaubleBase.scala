package io.github.alex_hawks.SanguineExtras.common.items.baubles

import WayofTime.bloodmagic.api.impl.ItemBindable
import baubles.api.{BaublesApi, IBauble}
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand}
import net.minecraft.world.World

import scala.util.control.Breaks._

trait BaubleBase extends ItemBindable with IBauble {

  /**
    * Code for right-clicking to equip baubles. This was copied from Botania which was authored by Vazkii et al,
    * and modified to suit my use case by myself (Alex_hawks)
    */
  override def onItemRightClick(stack: ItemStack, w: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    if (PlayerUtils.isNotFakePlayer(player))
      return ActionResult.newResult(EnumActionResult.FAIL, stack)

    val toEquip = stack.copy
    toEquip.stackSize = 1

    if (canEquip(toEquip, player)) {
      val baubles = BaublesApi.getBaublesHandler(player)
      breakable {
        for (i <- 0 until baubles.getSlots) {
          if (baubles.isItemValidForSlot(i, toEquip, player)) {
            val stackInSlot = baubles.getStackInSlot(i)
            if (stackInSlot == null || stackInSlot.getItem.asInstanceOf[IBauble].canUnequip(stackInSlot, player)) {
              if (!w.isRemote) {
                baubles.setStackInSlot(i, toEquip)
                stack.stackSize -= 1
              }

              if (stackInSlot != null) {
                stackInSlot.getItem.asInstanceOf[IBauble].onUnequipped(stackInSlot, player)
                return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot.copy())
              }
              break
            }
          }
        }
      }
    }

    return ActionResult.newResult(EnumActionResult.PASS, stack)
  }
}
