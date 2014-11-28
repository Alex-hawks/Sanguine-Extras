package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import WayofTime.alchemicalWizardry.api.rituals.ITileRitualStone
import codechicken.multipart.TileMultipart
import net.minecraft.world.World
import cpw.mods.fml.common.Optional

trait TRitualStoneTile extends TileMultipart with ITileRitualStone {

  override def isRuneType(runeType: Int): Boolean = {
    for (a <- partList) {
      if (a.isInstanceOf[ITileRitualStone] && a.asInstanceOf[ITileRitualStone].isRuneType(runeType)) {
        return true
      }
    }

    return false
  }
}