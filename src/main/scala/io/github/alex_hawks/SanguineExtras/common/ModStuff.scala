package io.github.alex_hawks.SanguineExtras.common

import WayofTime.bloodmagic.item.block.ItemBlockRitualStone
import io.github.alex_hawks.SanguineExtras.common.constructs.{BlockChest, TileChest}
import io.github.alex_hawks.SanguineExtras.common.items.ItemDropOrb
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroStone
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.{BlockWardedRitualStone, TEWardedRitualStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.{BlockAdvancedMasterStone, TEAdvancedMasterStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.{BlockWardedMasterStone, TEWardedMasterStone}
import io.github.alex_hawks.SanguineExtras.common.items.sigils._
import io.github.alex_hawks.SanguineExtras.common.items.baubles.{FeatheredKnife, StoneSummoner, LiquidSummoner}
import net.minecraft.item.{ItemBlock, Item}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.GameRegistry
import Helpers._

object Helpers {
  def rl(loc: String) = {
    new ResourceLocation(Constants.MetaData.MOD_ID, loc)
  }
}

object Blocks {
  var AdvancedMRS: BlockAdvancedMasterStone = null
  var WardedMRS: BlockWardedMasterStone = null
  var WardedRitualStone: BlockWardedRitualStone = null
  var Chest: BlockChest.type  = null

  var ItemWardedRitualStone: ItemBlockRitualStone = null

  def initBlocks {
    AdvancedMRS = new BlockAdvancedMasterStone
    GameRegistry.register(AdvancedMRS)
    GameRegistry.register(new ItemBlock(AdvancedMRS).setRegistryName(AdvancedMRS.getRegistryName))
    GameRegistry.registerTileEntity(classOf[TEAdvancedMasterStone], "AdvancedMasterStone")

    WardedMRS = new BlockWardedMasterStone
    GameRegistry.register(WardedMRS)
    GameRegistry.register(new ItemBlock(WardedMRS).setRegistryName(WardedMRS.getRegistryName))
    GameRegistry.registerTileEntity(classOf[TEWardedMasterStone], "WardedMasterStone")

    WardedRitualStone = new BlockWardedRitualStone
    GameRegistry.register(WardedRitualStone)
    ItemWardedRitualStone = new ItemBlockRitualStone(WardedRitualStone)
    ItemWardedRitualStone.setRegistryName(rl("WardedRitualStone"))
    GameRegistry.register(ItemWardedRitualStone)
    GameRegistry.registerTileEntity(classOf[TEWardedRitualStone], "WardedRitualStone")

    Chest = BlockChest
    GameRegistry.register(Chest)
    GameRegistry.register(new ItemBlock(Chest).setRegistryName(Chest.getRegistryName))
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

  var BaubleLiquidSummoner: Item = null
  var BaubleStoneSummoner: Item = null
  var BaubleFeatheredKnife: Item = null

  var DropOrb = ItemDropOrb

  def initItems {
    SigilBuilding = new ItemBuilding
    GameRegistry.register(SigilBuilding)

    SigilDestruction = new ItemDestruction
    GameRegistry.register(SigilDestruction)

    SigilInterdiction = new ItemInterdiction
    GameRegistry.register(SigilInterdiction)

    SigilMobNet = new ItemMobNet
    GameRegistry.register(SigilMobNet)

    SigilRebuild = new ItemRebuilding
    GameRegistry.register(SigilRebuild)

    if (Loader.isModLoaded(Constants.MetaData.MCMP_ID)) {
      MicroRitualStone = new ItemMicroStone
      GameRegistry.register(MicroRitualStone)
    }

    if (Loader.isModLoaded(Constants.MetaData.BAUBLES_ID)) {
      BaubleLiquidSummoner = LiquidSummoner
      GameRegistry.register(BaubleLiquidSummoner)

      BaubleStoneSummoner = StoneSummoner
      GameRegistry.register(BaubleStoneSummoner)

      BaubleFeatheredKnife = FeatheredKnife
      GameRegistry.register(BaubleFeatheredKnife)
    }

    GameRegistry.register(DropOrb)

    System.out.println("Initializing Items")
  }
}
