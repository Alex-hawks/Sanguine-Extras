package io.github.alex_hawks.SanguineExtras.common.util

import WayofTime.bloodmagic.api.event.BloodMagicCraftedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object BMCraftingHandler {

  @SubscribeEvent
  def onAltarCraft(e: BloodMagicCraftedEvent.Altar): Unit = {

  }

}
