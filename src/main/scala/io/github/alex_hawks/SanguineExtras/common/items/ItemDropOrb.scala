package io.github.alex_hawks.SanguineExtras.common.items

import java.util
import javax.annotation.Nullable

import WayofTime.bloodmagic.util.helper.TextHelper
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.items.ItemDropOrb.getItemCount
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils
import io.github.alex_hawks.SanguineExtras.common.util.capability.GenericProvider
import io.github.alex_hawks.util.minecraft.common.Implicit._
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.items.IItemHandler

import scala.annotation.meta.setter
import scala.collection.JavaConverters._
import scala.ref.WeakReference

//  TODO test the Inventory capability attached to this
sealed trait HelperDropOrb {
          val ID            =     "drop_orb"
          val RL            = new ResourceLocation(Constants.Metadata.MOD_ID, ID)
          val NBT           =     "items"
  @inline val CompoundTagID =     10

  @(CapabilityInject @setter)(classOf[IItemHandler])
  var CapInv: Capability[IItemHandler] = null
  val EMPTY_TAG_LIST = new NBTTagList
}

object ItemDropOrb extends Item with HelperDropOrb {
  this.maxStackSize = 1
  this.setUnlocalizedName(ID)
  this.setRegistryName(RL)
  this.setHasSubtypes(true)

  def addItems(orb: ItemStack, stacks: java.util.List[ItemStack]) {
    val list = new NBTTagList
    var tag: NBTTagCompound = null

    for (stack <- stacks.asScala) {
      tag = new NBTTagCompound
      stack.writeToNBT(tag)
      list.appendTag(tag)
    }

    if (orb.getTagCompound == null)
      orb.setTagCompound(new NBTTagCompound)
    orb.getTagCompound.setTag(NBT, list)
  }

  override def onItemRightClick(w: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val stack = player.getHeldItem(hand)
    if (!player.isSneaking)
      return new ActionResult[ItemStack](EnumActionResult.PASS, stack)

    while (PlayerUtils.putItem(player, getNextItem(stack))) {
      if (removeNextItem(stack)) {
        stack.setCount(0)
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
      }
    }
    ActionResult.newResult(EnumActionResult.SUCCESS, stack)
  }

  override def addInformation(stack: ItemStack, @Nullable worldIn: World, tooltip: util.List[String], flagIn: ITooltipFlag) {
    val unlocal = "tooltip.se.dropOrb.contains"
    val count = getItemCount(stack)
    val str = unlocal + "." + count

    val specific = TextHelper.localize(str)

    if (str.equals(specific))
      tooltip.add(TextHelper.localize(unlocal, s"$count"))
    else
      tooltip.add(specific)
  }

  def getItemCount(stack: ItemStack): Int = {
    if (stack.hasTagCompound && stack.getItem == this)
      if (stack.getTagCompound.hasKey(NBT))
        return stack.getTagCompound.getTagList(NBT, CompoundTagID).tagCount
    0
  }

  def getNextItem(orb: ItemStack): ItemStack = {
    if (orb.hasTagCompound && orb.getTagCompound.hasKey(NBT)) {
      val tag = orb.getTagCompound.getTagList(NBT, CompoundTagID).getCompoundTagAt(0)
      val stack = new ItemStack(tag)
      stack
    }
    else
      ItemStack.EMPTY
  }

  def removeNextItem(orb: ItemStack): Boolean = {
    if (getItemCount(orb) > 0) {
      orb.getTagCompound.getTagList(NBT, CompoundTagID).removeTag(0)
    }
    getItemCount(orb) < 1
  }

  override def initCapabilities(stack: ItemStack, nbt: NBTTagCompound) = CapDropOrb(stack)
}

object CapDropOrb extends HelperDropOrb {
  def apply(is: ItemStack): GenericProvider[IItemHandler] = new GenericProvider[IItemHandler](CapInv, new CapDropOrb(is))
}
// I don't want to keep the ItemStack force loaded in memory as this is circular reference holding them if I using a Strong Reference
class CapDropOrb private (val ref: WeakReference[ItemStack]) extends IItemHandler with HelperDropOrb {
  private def this(is: ItemStack) = this(new WeakReference[ItemStack](is))

  override def getStackInSlot(slot: Int): ItemStack = new ItemStack(getItemList.getCompoundTagAt(slot))

  override def extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = {
    val stack = getStackInSlot(slot)
    if (stack.isEmpty) {
      this.optimise()
      return ItemStack.EMPTY
    }
    var ret: ItemStack = null
    if (amount <= stack.getCount) {
      ret = stack.copyWithCount(amount)
      if (!simulate)
        stack.shrink(amount)
    }
    else {
      ret = stack.copy()
      if (!simulate)
        stack.shrink(stack.getCount)
    }

    if(!simulate)
      optimise()
    ret
  }

  override def getSlotLimit(slot: Int) = 0

  override def getSlots = getItemCount(ref.get.getOrElse(ItemStack.EMPTY))

  override def insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack   // No-op, not allowed to fill it

  def getItemList: NBTTagList = {
    val orb = ref.get.getOrElse(ItemStack.EMPTY)
    if (orb.hasTagCompound && orb.getTagCompound.hasKey(NBT))
      orb.getTagCompound.getTagList(NBT, CompoundTagID)
    else
      EMPTY_TAG_LIST
  }

  def optimise(): Unit = {
    val it = getItemList.iterator
    it.forEachRemaining {
      case x: NBTTagCompound =>
        if (new ItemStack(x).isEmpty)
          it.remove()
      case _ =>
      // No-op
    }
  }
}
