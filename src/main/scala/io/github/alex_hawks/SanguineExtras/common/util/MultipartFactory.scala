package io.github.alex_hawks.SanguineExtras.common.util

import WayofTime.bloodmagic.api.ritual.EnumRuneType
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MultipartStone
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.IPartFactory.IAdvancedPartFactory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

class MultipartFactory extends IAdvancedPartFactory {
  override def createPart(name: String, buf: PacketBuffer): IMultipart = name match {
    case "sanguineExtras:MicroRitualStone" => {
      val stone: MultipartStone = new MultipartStone
      stone.runeType.setRuneType(EnumRuneType.byMetadata(buf.readByte()))
      stone
    }
  }

  override def createPart(name: String, tag: NBTTagCompound): IMultipart = name match {
    case "sanguineExtras:MicroRitualStone" => {
      val stone: MultipartStone = new MultipartStone
      stone.runeType.setRuneType(EnumRuneType.byMetadata(tag.getByte("runeType")))
      stone
    }
  }

}
