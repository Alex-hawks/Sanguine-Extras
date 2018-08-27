package io.github.alex_hawks.SanguineExtras.common
package items.sigils

import java.util
import java.util.function.Consumer

import WayofTime.bloodmagic.client.IMeshProvider
import WayofTime.bloodmagic.core.data.Binding
import WayofTime.bloodmagic.item.ItemBindableBase
import WayofTime.bloodmagic.util.helper.TextHelper
import io.github.alex_hawks.SanguineExtras.common.enchantment.Cutting
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsDestruction
import io.github.alex_hawks.SanguineExtras.common.util.{BloodUtils, SanguineExtrasCreativeTab}
import io.github.alex_hawks.util.minecraft.common.Implicit.{iItemStack, item}
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

sealed trait HelperDestruction {
  val ID      =     "sigil_destruction"
  val RL      = new ResourceLocation(Constants.Metadata.MOD_ID, ID)
  val TIER    =     "tier"
  val LENGTH  =     "length"
}

object ItemDestruction extends ItemBindableBase with IMeshProvider with HelperDestruction {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName(ID)
  this.setRegistryName(ID)

  override def addInformation(stack: ItemStack, world: World, tooltip: util.List[String], flag: ITooltipFlag): Unit = {
    tooltip.add(loreFormat + TextHelper.localize("pun.se.sigil.destruction"))
    tooltip.add("")

    val binding: Binding = getBinding(stack)
    if (binding != null)
      tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", binding.getOwnerName))
    else
      tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"))

    tooltip.add("")
    tooltip.add(TextHelper.localize("tooltip.se.destruction.currentlength", "" + getLength(stack)))
    tooltip.add(TextHelper.localize("tooltip.se.destruction.maximumlength", "" + getMaxLength(stack)))
  }

  override def getUnlocalizedName(is: ItemStack): String = super.getUnlocalizedName + s".$TIER" + getTier(is)

  override def onItemUse(player: EntityPlayer, w: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    if (w.isRemote)
      return EnumActionResult.SUCCESS

    val stack: ItemStack = player.getHeldItem(hand)
    val bind = BloodUtils.getOrBind(stack, player)

    if (player.isSneaking) {
      val l = getLength(stack)
      if (l * 4 <= getMaxLength(stack))
        setLength(stack, l * 4)
      else
        setLength(stack, 1)
      EnumActionResult.SUCCESS
    }
    else {
      val toBreak = UtilsDestruction.find(pos, w, side, getLength(stack))
      UtilsDestruction.doDrops(player, hand, bind.getOwnerId, toBreak, w)
      EnumActionResult.SUCCESS
    }
  }

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val stack = player.getHeldItem(hand)
    if (player.isSneaking) {
      val l = getLength(stack)
      if (l * 4 <= getMaxLength(stack))
        setLength(stack, l * 4)
      else
        setLength(stack, 1)
    }
    ActionResult.newResult(EnumActionResult.SUCCESS, stack)
  }

  @SideOnly(Side.CLIENT)
  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]) {
    if (tab == SanguineExtrasCreativeTab.Instance)
      for (i <- 0 until getMaxTier)
        items.add(this.withNBT(TIER, i))
  }

  override def gatherVariants(ls: Consumer[String]): Unit = {
    for (i <- 0 until getMaxTier)
      ls.accept(s"$TIER=$i")
  }

  override def getMeshDefinition: ItemMeshDefinition = new ItemMeshDefinition {
    override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
      new ModelResourceLocation(ItemDestruction.this.getRegistryName, s"$TIER=${getTier(stack)}") // Don't need the NBT == null check here, as it's in getTier()
    }
  }
  
  override def canApplyAtEnchantingTable(stack: ItemStack, enchantment: Enchantment): Boolean = enchantment match {
    case Cutting ⇒ true
    case _: EnchantmentUntouching ⇒ true
    case x: EnchantmentLootBonus if x.getName.equals("lootBonusDigger") ⇒ true
    case _ ⇒ false
  }

  def getMaxTier: Int = BloodUtils.getHighestTierOrb

  def getTier(stack: ItemStack): Int = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey(TIER))
      return stack.getTagCompound.getInteger(TIER)
    stack.withNBT(TIER, 0)
    0
  }

  private def getLength(stack: ItemStack): Int = {
    if (stack.getTagCompound != null && stack.getTagCompound.hasKey(LENGTH))
      return stack.getTagCompound.getInteger(LENGTH)
    1
  }

  private def getMaxLength(stack: ItemStack) = Math.pow(4, getTier(stack)).round.toInt

  private def setLength(stack: ItemStack, length: Int): Unit = {
    stack.withNBT(LENGTH, length)
  }
}
