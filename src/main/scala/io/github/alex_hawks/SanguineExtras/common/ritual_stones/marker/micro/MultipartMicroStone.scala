package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import WayofTime.bloodmagic.api.ritual.{EnumRuneType, IRitualStone}
import WayofTime.bloodmagic.item.ItemInscriptionTool
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MultipartStone._
import mcmultipart.multipart.Multipart
import mcmultipart.raytrace.PartMOP
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}

object MultipartStone {
  @CapabilityInject(IRitualStone.Tile)
  var CAPABILITY_RUNE_TYPE: Capability[IRitualStone.Tile] = null
}

class MultipartStone extends Multipart {
  val runeType: IRitualStone.Tile = CAPABILITY_RUNE_TYPE.getDefaultInstance

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (capability == CAPABILITY_RUNE_TYPE)
      return true
    else
      return super.hasCapability(capability[_], facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (capability == CAPABILITY_RUNE_TYPE)
      return runeType.asInstanceOf[T]
    else
      return super.getCapability(capability[_], facing)
  }

  override def getMaterial: Material = Material.rock

  override def getHardness(hit: PartMOP): Float = 2.0F

  override def onActivated(player: EntityPlayer, stack: ItemStack, hit: PartMOP): Boolean = {
    if (stack != null && stack.getItem.isInstanceOf[ItemInscriptionTool]) {
      this.runeType.setRuneType(stack.getItem.asInstanceOf[ItemInscriptionTool].getType(stack))
      return true;
    }
    return super.onActivated(player, stack, hit)
  }

  override def writeToNBT(tag: NBTTagCompound) = {
    tag.setByte("runeType", runeType.getRuneType())
  }

  override def readFromNBT(tag: NBTTagCompound) = {
    runeType.setRuneType(EnumRuneType.byMetadata(tag.getByte("runeType")))
  }

  override def writeUpdatePacket(buf: PacketBuffer) = {
    buf.writeByte(runeType.getRuneType())
  }

  override def readUpdatePacket(buf: PacketBuffer) = {
    runeType.setRuneType(EnumRuneType.byMetadata(buf.readByte()))
  }

  override def getModelPath: String = {
    return null
  }
}
