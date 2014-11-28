package io.github.alex_hawks.SanguineExtras.common.multipart

import codechicken.multipart.MultiPartRegistry.IPartFactory
import codechicken.multipart.TMultiPart
import codechicken.multipart.MultiPartRegistry
import codechicken.multipart.MultipartGenerator
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MicroRitualStone
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.MicroStone
import io.github.alex_hawks.SanguineExtras.common.ModItems
import net.minecraft.item.ItemStack

class PartFactory extends IPartFactory {

  override def createPart(name: String, client: Boolean): TMultiPart = name match {
    case "MicroRitualStone" => new MicroRitualStone();
    case "MicroStone" => new MicroStone(name = name, pickedItem = new ItemStack(ModItems.MicroStone));
    case _ => null
  }

  def init: Unit = {
    MultiPartRegistry.registerParts(this, Array("MicroRitualStone", "MicroStone"));
    MultipartGenerator.registerTrait("WayofTime.alchemicalWizardry.api.rituals.ITileRitualStone", "io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.TRitualStoneTile")
  }
}