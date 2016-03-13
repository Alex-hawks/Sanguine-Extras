package io.github.alex_hawks.SanguineExtras.common.rituals.advanced

import java.util
import java.util.UUID

import WayofTime.bloodmagic.api.ritual.EnumRuneType._
import WayofTime.bloodmagic.api.ritual.{IMasterRitualStone, Ritual, RitualComponent}
import io.github.alex_hawks.SanguineExtras.api.ritual.{IAdvancedMasterRitualStone, AdvancedRitual}
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils
import io.github.alex_hawks.util.minecraft.common.Vector3
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.util.{EnumFacing, AxisAlignedBB, BlockPos}
import Forge._
import net.minecraft.util.EnumFacing._
import net.minecraftforge.oredict.OreDictionary
import recipes.{getSmeltingResult => get}
import recipes.{getSmeltingExperience => getXp}

object Forge {
  val recipes = FurnaceRecipes.instance
  val baseSmeltTime = 80
  val baseSmeltCost = 200
  val name = "SE003Forge"
}

class Forge extends AdvancedRitual(name, 0, 100, s"ritual.${Constants.MetaData.MOD_ID}.forge") {

  private val inputs = new util.ArrayList[ItemStack]
  private val outputs = new util.ArrayList[ItemStack]
  private var currentProgress = 0
  private var ticks = 0
  private var xp = 0f

  override def getRefreshTime = 1

  override def onCollideWith(mrs: IAdvancedMasterRitualStone, entity: Entity): Unit = {
    if (verifyBounds(entity, mrs.getBlockPos) && entity.isInstanceOf[EntityItem]) { // has the item? been thrown on top?
      val ent = entity.asInstanceOf[EntityItem]
      val is = ent.getEntityItem
      println(s"${is.getItem} smelts into ${get(is)}, but filter says ${isValidInFilter(is, mrs)}")
      if (isValidInFilter(is, mrs) && get(is) != null) // it smelts, and matches the filter
        inputs.add(is)
      else
        outputs.add(is) // pass it through, let's not break automation because your friend is an idiot

      ent.worldObj.removeEntity(ent)
    }
  }

  def verifyBounds(ent: Entity, s: BlockPos): Boolean = {
    new AxisAlignedBB(s.getX, s.getY, s.getZ, s.getX + 1, s.getY + 2, s.getZ + 1).isVecInside(ent.getPositionVector)
  }

  def getInputFilter(mrs: IAdvancedMasterRitualStone): (Boolean, IInventory)= {
    val white = new Vector3(mrs.getBlockPos).shift(mrs.getDirection)
    val black = new Vector3(mrs.getBlockPos).shift(mrs.getDirection.getOpposite)
    if (mrs.getWorldObj.getTileEntity(white.toPos).isInstanceOf[IInventory])
      (true, mrs.getWorldObj.getTileEntity(white.toPos).asInstanceOf[IInventory])
    else if (mrs.getWorldObj.getTileEntity(black.toPos).isInstanceOf[IInventory])
      (false, mrs.getWorldObj.getTileEntity(black.toPos).asInstanceOf[IInventory])
    else
      null
  }

  def isValidInFilter(is: ItemStack, mrs: IAdvancedMasterRitualStone): Boolean = {
    if (is == null)
      return false
    val (isWhiteList, filter) = getInputFilter(mrs)
    if (filter == null)
      return true //neither filter exists
    import filter._
    for (i <- 0 until getSizeInventory)
      if (is.equals(getStackInSlot(i)))
        return isWhiteList
    return !isWhiteList
  }

  override def performRitual(mrs: IMasterRitualStone): Unit = {
    ticks += 1

    if (!inputs.isEmpty) {
      currentProgress += 1
      if (currentProgress >= baseSmeltTime && BloodUtils.drainSoulNetworkWithNausea(UUID.fromString(mrs.getOwner), baseSmeltCost, null)) {
        currentProgress %= baseSmeltTime
        outputs.add(get(inputs.get(0)))
        xp += getXp(get(inputs.get(0)))
        nuggets(mrs, get(inputs.get(0)))

        if (inputs.get(0).stackSize > 1)
          inputs.get(0).stackSize -= 1
        else
          inputs.remove(0)

      }
    }

    if (outputs.size == 0)
      return
    if (ticks >= Math.max(baseSmeltTime / outputs.size, 1)) {
      ticks %= Math.max(baseSmeltTime / outputs.size, 1)
      val pos = mrs.getBlockPos
      val ent = new EntityItem(mrs.getWorldObj, pos.getX + 0.5, pos.getY - 1, pos.getZ + 0.5, outputs.get(0))
      mrs.getWorldObj.spawnEntityInWorld(ent)
      ent.motionX = 0
      ent.motionZ = 0
      ent.motionY = 0

      outputs.remove(0)
    }
  }

  def nuggets(mrs: IMasterRitualStone, output:ItemStack): Unit = {
    for(y <- OreDictionary.getOreIDs(inputs.get(0))) {
      val ore = OreDictionary.getOreName(y)
      if (ore.startsWith("dust"))
        return
    }

    for( x <- OreDictionary.getOreIDs(output)) {
      val ingot = OreDictionary.getOreName(x)
      if (ingot.startsWith("ingot") && OreDictionary.doesOreNameExist(s"nugget${ingot.substring("ingot".length)}") && OreDictionary.getOres(ingot.substring("ingot".length)).size() > 0)
        for(y <- 0 until mrs.getWorldObj.rand.nextInt(3 * output.stackSize) + 2 * output.stackSize)
          outputs.add(OreDictionary.getOres(ingot.substring("ingot".length)).get(0))
    }
  }

  override def getRefreshCost: Int = 0 // I'll take it when I need it

  override def getNewCopy: Ritual = return this.getClass.newInstance

  override def getComponents: util.ArrayList[RitualComponent] = {
    val ls = new util.ArrayList[RitualComponent]

    //Pillars
    this.addCornerRunes(ls, 1, -2, FIRE)
    this.addCornerRunes(ls, 1, -1, FIRE)
    this.addCornerRunes(ls, 1,  0, FIRE)
    this.addCornerRunes(ls, 1,  1, FIRE)
    this.addCornerRunes(ls, 1,  2, FIRE)

    //Ritual Facing
    this.addRune(ls, NORTH.getFrontOffsetX, 1, NORTH.getFrontOffsetZ  , DUSK)

    //Others
    for (dir: EnumFacing <- Array[EnumFacing](EAST, SOUTH, WEST))
      this.addRune(ls, dir.getFrontOffsetX, 1, dir.getFrontOffsetZ, AIR)

    return ls
  }

  override def readFromNBT(tag:NBTTagCompound): Unit = {
    if (tag.hasKey("inputs")) {
      val ls = tag.getTagList("inputs", tag.getId)
      for (x <- 0 until ls.tagCount)
        inputs.add(ItemStack.loadItemStackFromNBT(ls.getCompoundTagAt(x)))
    }

    if (tag.hasKey("outputs")) {
      val ls = tag.getTagList("outputs", tag.getId)
      for (x <- 0 until ls.tagCount)
        outputs.add(ItemStack.loadItemStackFromNBT(ls.getCompoundTagAt(x)))
    }

    if (tag.hasKey("currentProgress"))
      currentProgress = tag.getInteger("currentProgress")

    if (tag.hasKey("ticks"))
      ticks = tag.getInteger("ticks")

    if (tag.hasKey("xp"))
      xp = tag.getFloat("xp")
  }

  override def writeToNBT(tag: NBTTagCompound): Unit = {
    val inputTag = new NBTTagList
    val outputTag = new NBTTagList

    for (x <- 0 until inputs.size())
      inputTag.appendTag(inputs.get(x).writeToNBT(new NBTTagCompound))

    for(x <- 0 until outputs.size())
      outputTag.appendTag(outputs.get(x).writeToNBT(new NBTTagCompound))

    tag.setTag("inputs", inputTag)
    tag.setTag("outputs", outputTag)
    tag.setInteger("currentProgress", currentProgress)
    tag.setInteger("ticks", ticks)
    tag.setFloat("xp", xp)
  }
}
