package io.github.alex_hawks.SanguineExtras.common.util

import javax.annotation.ParametersAreNonnullByDefault

import WayofTime.bloodmagic.ritual.CapabilityRuneType.RuneTypeWrapper
import WayofTime.bloodmagic.ritual.IRitualStone
import io.github.alex_hawks.SanguineExtras.common.util.capability.GenericProvider
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import io.github.alex_hawks.SanguineExtras.common.items.ItemRitualChalk
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.anywhere.SERuneTypeStorage

import scala.annotation.meta.setter

@ParametersAreNonnullByDefault
object SEEventHandler {
  @(CapabilityInject @setter)(classOf[IRitualStone.Tile])
  var CapRuneType: Capability[IRitualStone.Tile] = null

  @SubscribeEvent
  def attachCapsTE(e: AttachCapabilitiesEvent[TileEntity]): Unit = {
      e.addCapability(ItemRitualChalk.RL, new GenericProvider[IRitualStone.Tile](CapRuneType, new RuneTypeWrapper, null, SERuneTypeStorage))
  }

}
