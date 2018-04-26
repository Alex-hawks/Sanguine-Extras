package io.github.alex_hawks.SanguineExtras.common.rituals.basic

import java.util
import java.util.function.Consumer

import ApiaryOverclock._
import WayofTime.bloodmagic.ritual.{IMasterRitualStone, Ritual, RitualComponent}
import forestry.api.apiculture.IBeeHousing
import net.minecraft.tileentity.TileEntity
import WayofTime.bloodmagic.ritual.EnumRuneType._
import io.github.alex_hawks.SanguineExtras.common.Constants

object ApiaryOverclock {
  val name = "SE004ApiaryOverclock"
  val activationCost = 10000
  val upkeepCost = 10
}

class ApiaryOverclock extends Ritual(name, 0, activationCost, s"ritual.${Constants.Metadata.MOD_ID}.apiary_overclock") {
  override val getRefreshCost: Int = upkeepCost

  override def performRitual(mrs: IMasterRitualStone): Unit = {
    // Yes, I hardcode what Bee Housings I support, so that I can actually make them consume stuff without just calling update()
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

  override def gatherComponents(ls: Consumer[RitualComponent]): Unit = {
    //Pillars
    this.addCornerRunes(ls, 1, 0, AIR)
    this.addCornerRunes(ls, 1, 1, AIR)
    this.addCornerRunes(ls, 1, 2, AIR)

    //Player needs to know what they're doing with bees, this rune is on top of the apiary, bee needs to be Cave-Dwelling
    this.addRune(ls, 0, 2, 0, DUSK)
  }
}
