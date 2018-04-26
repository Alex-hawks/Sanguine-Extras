package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.anywhere

import WayofTime.bloodmagic.ritual.{EnumRuneType, IRitualStone}
import net.minecraft.nbt.{NBTBase, NBTTagByte}
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class SERuneTypeWrapper(private var rune: EnumRuneType = null) extends IRitualStone.Tile {
  override def setRuneType(runeType: EnumRuneType): Unit = rune = runeType

  override def getRuneType: EnumRuneType = rune

  override def isRuneType(runeType: EnumRuneType): Boolean = if (runeType == null|| rune == null) false else rune == runeType

  def isDefined = rune != null
}

object SERuneTypeStorage extends Capability.IStorage[IRitualStone.Tile] {
  override def readNBT(cap: Capability[IRitualStone.Tile], inst: IRitualStone.Tile, side: EnumFacing, tag: NBTBase): Unit = inst match {
    case x: SERuneTypeWrapper =>
      inst.setRuneType(EnumRuneType.byMetadata(tag.asInstanceOf[NBTTagByte].getByte))
    case _ =>
      cap.readNBT(inst, side, tag)
  }

  override def writeNBT(cap: Capability[IRitualStone.Tile], inst: IRitualStone.Tile, side: EnumFacing): NBTBase = inst match {
    case x: SERuneTypeWrapper =>
      if (!x.isDefined)
        null
      else
        new NBTTagByte(x.getRuneType.ordinal().toByte)
    case _ =>
      cap.writeNBT(inst, side)
  }
}
