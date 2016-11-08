package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import WayofTime.bloodmagic.api.ritual.{CapabilityRuneType, EnumRuneType, IRitualStone}
import WayofTime.bloodmagic.item.ItemInscriptionTool
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MultipartStone._
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.Multipart
import mcmultipart.raytrace.PartMOP
import net.minecraft.block.material.Material
import net.minecraft.block.properties.{IProperty, PropertyEnum}
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{ResourceLocation, BlockRenderLayer, EnumFacing, EnumHand}
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}

import scala.collection.mutable.ArrayBuffer

object MultipartStone {
  val NAME = new ResourceLocation(Constants.MetaData.MOD_ID, "microRitualStone")

  @CapabilityInject(classOf[IRitualStone.Tile])
  var CAPABILITY_RUNE_TYPE: Capability[IRitualStone.Tile] = null
  val box = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)
  val PROPERTY_RUNE_TYPE: PropertyEnum[EnumRuneType] = PropertyEnum.create("type", classOf[EnumRuneType])
  val MULTIPART_STATE = {
    val listed = new ArrayBuffer[IProperty[_]]
    listed += PROPERTY_RUNE_TYPE

    new ExtendedBlockState(MCMultiPartMod.multipart, listed.toArray, Array[IUnlistedProperty[_]]())
  }
}

class MultipartStone extends Multipart {
  val runeType: IRitualStone.Tile = new CapabilityRuneType.RuneTypeWrapper

  def initRuneType(rune: EnumRuneType) = {
    runeType.setRuneType(rune)
    this
  }

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (capability == CAPABILITY_RUNE_TYPE)
      return true
    else
      return super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (capability == CAPABILITY_RUNE_TYPE)
      return runeType.asInstanceOf[T]
    else
      return super.getCapability(capability, facing)
  }

  override def getMaterial: Material = Material.ROCK

  override def getHardness(hit: PartMOP): Float = 2.0F

  override def onActivated(player: EntityPlayer, hand: EnumHand, stack: ItemStack, hit: PartMOP): Boolean = {
    if (stack != null && stack.getItem.isInstanceOf[ItemInscriptionTool]) {
      this.runeType.setRuneType(stack.getItem.asInstanceOf[ItemInscriptionTool].getType(stack))
      return true;
    }
    return super.onActivated(player, hand, stack, hit)
  }

  override def writeToNBT(tag: NBTTagCompound) = {
    tag.setByte("runeType", runeType.getRuneType.ordinal.toByte); tag
  }

  override def readFromNBT(tag: NBTTagCompound) = {
    runeType.setRuneType(EnumRuneType.byMetadata(tag.getByte("runeType")))
  }

  override def writeUpdatePacket(buf: PacketBuffer) = {
    buf.writeByte(runeType.getRuneType.ordinal.toByte)
  }

  override def readUpdatePacket(buf: PacketBuffer) = {
    runeType.setRuneType(EnumRuneType.byMetadata(buf.readByte))
  }

  override def getModelPath: ResourceLocation = {
    NAME
  }

  override def canRenderInLayer(layer: BlockRenderLayer): Boolean = {
    layer == BlockRenderLayer.CUTOUT
  }

  override def getType: ResourceLocation = {
    NAME
  }

  override def addSelectionBoxes (list: java.util.List[AxisAlignedBB]) = {
    list.add(box)
  }

  override def addCollisionBoxes(mask: AxisAlignedBB, list: java.util.List[AxisAlignedBB] , collidingEntity: Entity) = {
    if (mask.intersectsWith(box))
      list.add(box)
  }

  override def createBlockState: BlockStateContainer = { MULTIPART_STATE }

  override def getExtendedState(state: IBlockState): IBlockState = {
    state.withProperty(PROPERTY_RUNE_TYPE, runeType.getRuneType)
  }
}
