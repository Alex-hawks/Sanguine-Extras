package io.github.alex_hawks.SanguineExtras.common.items

import WayofTime.bloodmagic.util.helper.TextHelper
import io.github.alex_hawks.SanguineExtras.common.util.{SanguineExtrasCreativeTab, PlayerUtils}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{EnumActionResult, ActionResult, EnumHand}
import net.minecraft.world.World

import scala.collection.JavaConversions

object ItemDropOrb extends Item {
    this.maxStackSize = 1
//    setCreativeTab(SanguineExtrasCreativeTab.Instance)
    this.setUnlocalizedName("dropOrb")
    this.setRegistryName("dropOrb")
    this.setHasSubtypes(true)

  def addItems(orb: ItemStack, stacks: java.util.List[ItemStack]) {
    val list = new NBTTagList
    var tag:NBTTagCompound = null

    for (stack <- JavaConversions.asScalaBuffer(stacks)) {
      tag = new NBTTagCompound
      stack.writeToNBT(tag)
      list.appendTag(tag)
    }

    if (orb.getTagCompound == null)
      orb.setTagCompound(new NBTTagCompound)
    orb.getTagCompound.setTag("items", list)
  }

  override def onItemRightClick(stack: ItemStack, w: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    if(!player.isSneaking)
      return new ActionResult[ItemStack](EnumActionResult.PASS, stack)
    if(getItemCount(stack) < 1) {
      stack.stackSize = 0
      return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }


    while (PlayerUtils.putItem(player, getNextItem(stack))) {
      if (removeNextItem(stack)) {
        stack.stackSize = 0
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
      }
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
  }

  override def addInformation(stack: ItemStack, par2EntityPlayer: EntityPlayer, tooltip: java.util.List[String], par4: Boolean) {
    val unlocal = "tooltip.se.dropOrb.contains"
    val count = getItemCount(stack)
    val str = unlocal + "." + count

    val specific = TextHelper.localize(str, "")

    if(str.equals(specific))
      tooltip.add(TextHelper.localize(unlocal, "" + count))
    else
      tooltip.add(specific)
  }

  def getItemCount(stack: ItemStack):Int = {
    if (stack.hasTagCompound)
      if (stack.getTagCompound.hasKey("items"))
        return stack.getTagCompound.getTagList("items", 10).tagCount
    return 0
  }

  def getNextItem(orb: ItemStack):ItemStack = {
    if (orb.hasTagCompound && orb.getTagCompound.hasKey("items")) {
      val tag = orb.getTagCompound.getTagList("items", 10).getCompoundTagAt(0)
      val stack = ItemStack.loadItemStackFromNBT(tag)
      return stack
    }
    return null;
  }

  def removeNextItem(orb: ItemStack):Boolean = {
    if (getItemCount(orb) > 0) {
      orb.getTagCompound.getTagList("items", 10).removeTag(0)
    }
    return getItemCount(orb) < 1
  }
}
