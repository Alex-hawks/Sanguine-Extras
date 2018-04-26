package io.github.alex_hawks.SanguineExtras.common.util.capability

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.common.util.INBTSerializable

class GenericProvider[B](val cap: Capability[B], val instance: B, val side: EnumFacing = null, val customStorage: Capability.IStorage[B] = null) extends ICapabilityProvider with INBTSerializable[NBTBase] {
  override def getCapability[T](cap: Capability[T], facing: EnumFacing) = {
    if (this.cap == cap && facing == side)
      this.cap.cast(instance)
    else
      null.asInstanceOf[T]
  }

  override def hasCapability(cap: Capability[_], facing: EnumFacing) = {
    if (this.cap == cap && facing == side)
      true
    else
      false
  }

  override def deserializeNBT(nbt: NBTBase): Unit = {
    if (customStorage == null)
      cap.readNBT(instance, side, nbt)
    else
      customStorage.readNBT(cap, instance, side, nbt)}

  override def serializeNBT() = {
    if (customStorage == null)
      cap.writeNBT(instance, side)
    else
      customStorage.writeNBT(cap, instance, side)
  }
}
