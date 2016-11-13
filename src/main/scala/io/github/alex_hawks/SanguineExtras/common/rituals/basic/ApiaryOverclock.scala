package io.github.alex_hawks.SanguineExtras.common.rituals.basic

import java.util

import WayofTime.bloodmagic.api.ritual.EnumRuneType._
import WayofTime.bloodmagic.api.ritual.{IMasterRitualStone, RitualComponent, Ritual}
import ApiaryOverclock._
import forestry.api.apiculture.IBeeHousing
import io.github.alex_hawks.SanguineExtras.common.Constants
import net.minecraft.tileentity.TileEntity

object ApiaryOverclock {
  val name = "SE004ApiaryOverclock"
  val activationCost = 10000
  val upkeepCost = 10
}

class ApiaryOverclock extends Ritual(name, 0, activationCost, s"ritual.${Constants.MetaData.MOD_ID}.apiaryOverclock") {
  override val getRefreshCost: Int = upkeepCost

  override def performRitual(mrs: IMasterRitualStone): Unit = {
    // Yes I hardcode what Bee Housings I support, so that I can actually make them consume stuff without just calling update()
    val tile = mrs.getWorldObj.getTileEntity(mrs.getBlockPos.add(0,1,0))
    if (tile == null)
      return
    tile.getClass.getName match {
      case "forestry.apiculture.tiles.TileApiary" => forestryAccel(tile)
      case "forestry.apiculture.tiles.TileBeeHouse" => forestryAccel(tile)
    }
  }

  def forestryAccel(tile: TileEntity) {
    val t: (TileEntity with IBeeHousing) = tile.asInstanceOf[TileEntity with IBeeHousing]
    val logic = t.getBeekeepingLogic
    if (logic.canWork)
      logic.doWork
  }

  override def getNewCopy: Ritual = this.getClass.newInstance

  override def getComponents: util.ArrayList[RitualComponent] = {
    val ls = new util.ArrayList[RitualComponent]

    //Pillars
    this.addCornerRunes(ls, 1, 0, AIR)
    this.addCornerRunes(ls, 1, 1, AIR)
    this.addCornerRunes(ls, 1, 2, AIR)

    //Player needs to know what they're doing with bees
    this.addRune(ls, 0, 2, 0, DUSK)

    return ls
  }
}
