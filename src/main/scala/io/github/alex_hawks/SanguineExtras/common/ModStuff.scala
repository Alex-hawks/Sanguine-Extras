package io.github.alex_hawks.SanguineExtras.common

import java.util.function.Consumer

import WayofTime.bloodmagic.block.IBMBlock
import WayofTime.bloodmagic.client.{IMeshProvider, IVariantProvider}
import com.google.common.collect.Lists
import io.github.alex_hawks.SanguineExtras.common.constructs.{BlockChest, TileChest}
import io.github.alex_hawks.SanguineExtras.common.enchantment.Cutting
import io.github.alex_hawks.SanguineExtras.common.items.baubles.{FeatheredKnife, LiquidSummoner, StoneSummoner}
import io.github.alex_hawks.SanguineExtras.common.items.sigils._
import io.github.alex_hawks.SanguineExtras.common.items.ItemDropOrb
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.{BlockWardedRitualStone, TEWardedRitualStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.{BlockAdvancedMasterStone, TEAdvancedMasterStone}
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.{BlockWardedMasterStone, TEWardedMasterStone}
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.{ModelBakery, ModelResourceLocation}
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.{Item, ItemBlock}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.control.Breaks._

object Helpers {
  def rl(loc: String) = new ResourceLocation(Constants.Metadata.MOD_ID, loc)

  final val MOD_ID = "sanguineextras"
}

object Blocks {
  lazy val advanced_mrs           = new BlockAdvancedMasterStone
  lazy val warded_mrs             = new BlockWardedMasterStone
  lazy val warded_rs              = new BlockWardedRitualStone
  lazy val chest                  =     BlockChest
}

object Items {
  lazy val sigil_building         = new ItemBuilding
  lazy val sigil_destruction      =     ItemDestruction
  lazy val sigil_interdiction     = new ItemInterdiction
  lazy val sigil_mob_net          = new ItemMobNet
  lazy val sigil_rebuild          = new ItemRebuilding

  lazy val bauble_feathered_knife =     FeatheredKnife
  lazy val bauble_liquid_summoner =     LiquidSummoner
  lazy val bauble_stone_summoner  =     StoneSummoner

  lazy val drop_orb               =     ItemDropOrb
}

object Enchantments {
  lazy val cutting                =     Cutting
}

@Mod.EventBusSubscriber(modid = Helpers.MOD_ID)
object Registrar {
  val blocks = Lists.newArrayList[Block]().asScala
  val items = Lists.newArrayList[Item]().asScala
  val enchantments = Lists.newArrayList[Enchantment]().asScala

  @SubscribeEvent
  def registerBlocks(event: RegistryEvent.Register[Block]): Unit = {
    import Blocks._
    blocks += advanced_mrs
    blocks += warded_mrs
    blocks += warded_rs
    blocks += chest

    event.getRegistry.registerAll(blocks.toArray: _*)

    registerTEs()
  }

  @SubscribeEvent
  def registerItems(event: RegistryEvent.Register[Item]): Unit = {
    import Items._
    for (block <- blocks) {
      if (block.isInstanceOf[IBMBlock])
        items += block.asInstanceOf[IBMBlock].getItem.setRegistryName(block.getRegistryName)
    }

    items += sigil_building
    items += sigil_destruction
    items += sigil_interdiction
    items += sigil_mob_net
    items += sigil_rebuild

    items += bauble_feathered_knife
    items += bauble_liquid_summoner
    items += bauble_stone_summoner

    items += drop_orb

    event.getRegistry.registerAll(items.toArray: _*)
  }
  
  @SubscribeEvent
  def registerEnchantments(event: RegistryEvent.Register[Enchantment]): Unit = {
    import Enchantments._
    
    enchantments += cutting
    
    event.getRegistry.registerAll(enchantments.toArray: _*)
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def registerRenders(event: ModelRegistryEvent): Unit = {
    for (item <- items)
      breakable {
        item match {
          case i: IVariantProvider =>
            val map = new Int2ObjectOpenHashMap[String]
            i.gatherVariants(map)
            for (pair <- map.asScala)
              ModelLoader.setCustomModelResourceLocation(item, pair._1, new ModelResourceLocation(item.getRegistryName, pair._2))
            break
          case i: IMeshProvider =>
            var loc = i.getCustomLocation
            if (loc == null)
              loc = i.getRegistryName
              i.gatherVariants((variant: String) => {ModelBakery.registerItemVariants(item, new ModelResourceLocation(loc, variant))})

            ModelLoader.setCustomMeshDefinition(i, i.getMeshDefinition)
            break
          case i: ItemBlock if i.getBlock.isInstanceOf[IVariantProvider] =>
            val map = new Int2ObjectOpenHashMap[String]
            i.getBlock.asInstanceOf[IVariantProvider].gatherVariants(map)
            for ((meta, variant) <- map.asScala)
              ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName, variant))
            break
          case i: ItemBlock =>
              ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName, null))
            break
          case _ =>
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName, null))
        }
      }
  }

  def registerTEs(): Unit = {
    GameRegistry.registerTileEntity(classOf[TEAdvancedMasterStone], Blocks.advanced_mrs.getRegistryName.toString)
    GameRegistry.registerTileEntity(classOf[TEWardedMasterStone],   Blocks.warded_mrs.getRegistryName.toString)
    GameRegistry.registerTileEntity(classOf[TEWardedRitualStone],   Blocks.warded_rs.getRegistryName.toString)
    GameRegistry.registerTileEntity(classOf[TileChest],             Blocks.chest.getRegistryName.toString)
  }

  implicit def toConsumer[T](in: T ⇒ Unit): Consumer[T] = new Consumer[T] {
    override def accept(t: T): Unit = in(t)
  }
}
