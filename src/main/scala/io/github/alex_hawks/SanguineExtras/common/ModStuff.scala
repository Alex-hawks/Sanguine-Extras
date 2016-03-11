package io.github.alex_hawks.SanguineExtras.common

import WayofTime.bloodmagic.item.block.ItemBlockRitualStone
import io.github.alex_hawks.SanguineExtras.common.constructs.{BlockChest, TileChest}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroStone
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.{BlockWardedRitualStone, TEWardedRitualStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.{BlockAdvancedMasterStone, TEAdvancedMasterStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.{BlockWardedMasterStone, TEWardedMasterStone}
import io.github.alex_hawks.SanguineExtras.common.sigils._
import net.minecraft.item.Item
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by Alex on 2016-03-10.
  */
object Blocks {
  var AdvancedMRS: BlockAdvancedMasterStone = null
  var WardedMRS: BlockWardedMasterStone = null
  var WardedRitualStone: BlockWardedRitualStone = null
  var Chest: BlockChest.type  = null

  def initBlocks {
    AdvancedMRS = new BlockAdvancedMasterStone
    GameRegistry.registerBlock(AdvancedMRS, "AdvancedMasterStone")
    GameRegistry.registerTileEntity(classOf[TEAdvancedMasterStone], "AdvancedMasterStone")

    WardedMRS = new BlockWardedMasterStone
    GameRegistry.registerBlock(WardedMRS, "WardedMasterStone")
    GameRegistry.registerTileEntity(classOf[TEWardedMasterStone], "WardedMasterStone")

    WardedRitualStone = new BlockWardedRitualStone
    GameRegistry.registerBlock(WardedRitualStone, classOf[ItemBlockRitualStone], "WardedRitualStone")
    GameRegistry.registerTileEntity(classOf[TEWardedRitualStone], "WardedRitualStone")

    Chest = BlockChest
    GameRegistry.registerBlock(Chest, "SanguineChest")
    GameRegistry.registerTileEntity(classOf[TileChest], "SanguineChest")

    System.out.println("Initializing Blocks")
  }
}

object Items {
  var SigilBuilding: ItemBuilding = null
  var SigilDestruction: ItemDestruction = null
  var SigilInterdiction: ItemInterdiction = null
  var SigilMobNet: ItemMobNet = null
  var SigilRebuild: ItemRebuilding = null
  var MicroRitualStone: Item = null

  def initItems {
    SigilBuilding = new ItemBuilding
    GameRegistry.registerItem(SigilBuilding, "sigilBuilding")

    SigilDestruction = new ItemDestruction
    GameRegistry.registerItem(SigilDestruction, "sigilDestruction")

    SigilInterdiction = new ItemInterdiction
    GameRegistry.registerItem(SigilInterdiction, "sigilInterdiction")

    SigilMobNet = new ItemMobNet
    GameRegistry.registerItem(SigilMobNet, "sigilMobNet")

    SigilRebuild = new ItemRebuilding
    GameRegistry.registerItem(SigilRebuild, "sigilRebuilding")

    if (Loader.isModLoaded("mcmultipart")) {
      MicroRitualStone = new ItemMicroStone
      GameRegistry.registerItem(MicroRitualStone, "microRitualStone")
    }

    System.out.println("Initializing Items")
  }
}
