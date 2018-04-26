package io.github.alex_hawks.SanguineExtras.common.items.baubles

import WayofTime.bloodmagic.item.ItemBindableBase
import baubles.api.{BaublesApi, IBauble}
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils
import io.github.alex_hawks.util.minecraft.common.Implicit.iItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}

import scala.util.control.Breaks._

trait BaubleBase extends ItemBindableBase with IBauble {

  @CapabilityInject(classOf[IBauble])
  val CAP_BAUBLE: Capability[IBauble]= null

  /**
    * Code for right-clicking to equip baubles. This was copied from Botania, which was authored by Vazkii et al,
    * and modified to suit my use case by myself (Alex_hawks)
    */
  override def onItemRightClick(w: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val stack = player.getHeldItem(hand)

    if (!w.isRemote) {
      if (!PlayerUtils.isRealPlayer(player))
        return ActionResult.newResult(EnumActionResult.FAIL, stack)

      val toEquip = stack.copyWithCount(1)

      if (canEquip(toEquip, player)) {
        val baubles = BaublesApi.getBaublesHandler(player)
        breakable {
          for (i <- 0 until baubles.getSlots) {
            if (baubles.isItemValidForSlot(i, toEquip, player)) {
              val stackInSlot = baubles.getStackInSlot(i)
              if (stackInSlot.isEmpty) {
                baubles.setStackInSlot(i, toEquip)
                stack.shrink(1)
              }

              if (stackInSlot.hasCapability(CAP_BAUBLE, null) && stack.getCapability[IBauble](CAP_BAUBLE, null).canUnequip(stackInSlot, player)) {
                stack.getCapability[IBauble](CAP_BAUBLE, null).onUnequipped(stackInSlot, player)

                baubles.setStackInSlot(i, toEquip)
                stack.shrink(1)

                if (stack.isEmpty) {
                  return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot)
                }
                else {
                  PlayerUtils.putItemWithDrop(player, stackInSlot)
                  return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
                }
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
