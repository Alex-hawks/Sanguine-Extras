package io.github.alex_hawks.SanguineExtras.common.util

import WayofTime.bloodmagic.api.ritual.EnumRuneType
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MultipartStone
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.IPartFactory.IAdvancedPartFactory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation

class MultipartFactory extends IAdvancedPartFactory {
  override def createPart(name: ResourceLocation, buf: PacketBuffer): IMultipart = name match {
    case MultipartStone.NAME => {
      val stone: MultipartStone = new MultipartStone
      stone.runeType.setRuneType(EnumRuneType.byMetadata(buf.readByte()))
      stone
    }
  }

  override def createPart(name: ResourceLocation, tag: NBTTagCompound): IMultipart = name match {
    case MultipartStone.NAME => {
      val stone: MultipartStone = new MultipartStone
      stone.runeType.setRuneType(EnumRuneType.byMetadata(tag.getByte("runeType")))
      stone
    }
  }

}
