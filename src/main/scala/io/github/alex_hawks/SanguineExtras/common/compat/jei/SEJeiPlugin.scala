package io.github.alex_hawks.SanguineExtras.common.compat.jei

import io.github.alex_hawks.SanguineExtras.common.{Blocks, Items}
import mezz.jei.api.{IModPlugin, ISubtypeRegistry, JEIPlugin}

@JEIPlugin
class SEJeiPlugin extends IModPlugin {

  override def registerItemSubtypes(sr: ISubtypeRegistry): Unit = {
    import sr.{registerSubtypeInterpreter => interpret}

    interpret(Blocks.chest.getItem, SESubtypeInterpreter)
    interpret(Items.sigil_destruction, SESubtypeInterpreter)
    interpret(Items.bauble_liquid_summoner, SESubtypeInterpreter)
    // TODO add other NBT sensitive items here as well as in SESubtypeInterpreter
  }
}
