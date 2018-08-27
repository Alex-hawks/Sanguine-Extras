package io.github.alex_hawks.SanguineExtras.common.compat.jei

import io.github.alex_hawks.SanguineExtras.common.constructs.{Chest, ItemBlockChest}
import io.github.alex_hawks.SanguineExtras.common.items.baubles.{LiquidSummoner, LiquidSummonerHelper}
import io.github.alex_hawks.SanguineExtras.common.items.sigils.ItemDestruction
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter
import net.minecraft.item.ItemStack

object SESubtypeInterpreter extends ISubtypeInterpreter{
  override def apply(is: ItemStack) = is.getItem match {
    case ItemDestruction  ⇒ s"tier=${ItemDestruction.getTier(is)}"
    case ItemBlockChest   ⇒ s"tier=${Chest.getTier(is)}"
    case LiquidSummoner   ⇒ s"type=${LiquidSummonerHelper.getType(is)}"
    // TODO add other NBT sensitive items here as well as in SEJeiPlugin
  }
}
