package io.github.alex_hawks.SanguineExtras.common.items

import java.util
import java.util.function.Consumer
import javax.annotation.Nullable

import WayofTime.bloodmagic.client.IMeshProvider
import WayofTime.bloodmagic.ritual.{EnumRuneType, IRitualStone}
import WayofTime.bloodmagic.util.helper.TextHelper
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.anywhere.TEDummyMarker
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab
import io.github.alex_hawks.util.minecraft.common.Implicit._
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumActionResult._
import net.minecraft.util.math.BlockPos
import net.minecraft.util._
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.annotation.meta.setter

sealed trait HelperRitualChalk {
  @(CapabilityInject @setter)(classOf[IRitualStone.Tile])
  var CapRuneType: Capability[IRitualStone.Tile] = null

  val ID  =     "ritual_chalk"
  val RL  = new ResourceLocation(Constants.Metadata.MOD_ID, ID)
  val NBT =     "type"
}

object ItemRitualChalk extends Item with IMeshProvider with HelperRitualChalk {
  this.maxStackSize = 1
  this.setUnlocalizedName(ID)
  this.setRegistryName(RL)
  this.setHasSubtypes(true)

  override def addInformation(stack: ItemStack, @Nullable worldIn: World, tooltip: util.List[String], flagIn: ITooltipFlag) {
    tooltip.add(TextHelper.localize("tooltip.se.chalk." + getType(stack)))
  }

  @SideOnly(Side.CLIENT)
  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]): Unit = {
    if (tab == SanguineExtrasCreativeTab.Instance)
      for (i <- EnumRuneType.values)
        items.add(this.withNBT(NBT, i))
  }

  override def onItemUse(p: EntityPlayer, w: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val te = w.getTileEntity(pos)

    // Don't let people make multirunes this early into their career. There will be a way much later in the progression once I work out how to balance it, and prevent certain bugs
    if (w.getBlockState(pos).getBlock.isInstanceOf[IRitualStone] || te.isInstanceOf[IRitualStone.Tile] || (te.hasCapability(CapRuneType, null)))
      PASS
    else if (w.getTileEntity(pos) == null) {
      w.setTileEntity(pos, new TEDummyMarker)
    }
    else {

    }

    val cap = w.getTileEntity(pos).getCapability(CapRuneType, null)
    cap.setRuneType(this.getType(p.getHeldItem(hand)))

    SUCCESS
  }

  override def gatherVariants(ls: Consumer[String]): Unit = EnumRuneType.values().foreach(rune => ls.accept(s"$NBT=$rune"))

  override def getMeshDefinition = (stack: ItemStack) => new ModelResourceLocation(ItemRitualChalk.this.getRegistryName, s"$NBT=${getType(stack)}")

  def getType(stack: ItemStack): EnumRuneType = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey(NBT))
      EnumRuneType.byMetadata(stack.getTagCompound.getByte(NBT))
    else
      EnumRuneType.BLANK
  }
}
