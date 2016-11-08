package io.github.alex_hawks.SanguineExtras.client

import WayofTime.bloodmagic.api.ritual.EnumRuneType
import WayofTime.bloodmagic.util.helper.InventoryRenderHelperV2
import io.github.alex_hawks.SanguineExtras.client.ClientProxy._
import io.github.alex_hawks.SanguineExtras.client.constructs.{GuiChest, RenderChest}
import io.github.alex_hawks.SanguineExtras.client.sigil_utils.UtilsBuilding
import io.github.alex_hawks.SanguineExtras.common.Blocks._
import io.github.alex_hawks.SanguineExtras.common.Items._
import io.github.alex_hawks.SanguineExtras.common.constructs.{BlockChest, Chest, TileChest}
import io.github.alex_hawks.SanguineExtras.common.{CommonProxy, Constants}
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader

object ClientProxy {
  val V2: InventoryRenderHelperV2 = new InventoryRenderHelperV2(Constants.MetaData.MOD_ID)
}

class ClientProxy extends CommonProxy {

  override def registerClientStuff {
    MinecraftForge.EVENT_BUS.register(new UtilsBuilding)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileChest], RenderChest)
    registerRender
  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    ID match {
      case 0 =>
        return new GuiChest(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileChest])
    }
    return null
  }

  def registerRender {
    V2.registerRender(SigilBuilding, "normal")
    V2.registerRender(SigilRebuild, "normal")
    V2.registerRender(SigilInterdiction, 0, "active")
    V2.registerRender(SigilInterdiction, 1, "passive")
    V2.registerRender(SigilMobNet, 0, "empty")
    V2.registerRender(SigilMobNet, 1, "full")
    V2.registerRender(DropOrb, "normal")
//    registerOneTexture(SigilDestruction)

    V2.registerRender(WardedMRS, "normal")
    V2.registerRender(AdvancedMRS, "normal")
    for (rune <- EnumRuneType.values)
      V2.registerRender(Item.getItemFromBlock(WardedRitualStone), rune.ordinal, "WardedRitualStone", rune.getName)
    for (tier <- 0 to Chest.maxTier)
      V2.registerRender(Item.getItemFromBlock(BlockChest), tier, "SanguineChest", s"${tier}")
    for (i <- 0 to SigilDestruction.getMaxTier)
      V2.registerRender(SigilDestruction, i, "normal")

    for (rune <- EnumRuneType.values)
      V2.registerRender(MicroRitualStone, rune.ordinal, rune.getName)

    if (Loader.isModLoaded(Constants.MetaData.BAUBLES_ID)) {
      V2.registerRender(BaubleLiquidSummoner, 0, "water")
      V2.registerRender(BaubleLiquidSummoner, 1, "lava")

      V2.registerRender(BaubleStoneSummoner, 0, "cobble")
      V2.registerRender(BaubleStoneSummoner, 1, "stone")
      V2.registerRender(BaubleStoneSummoner, 2, "netherrack")
      V2.registerRender(BaubleStoneSummoner, 3, "obsidian")
      V2.registerRender(BaubleStoneSummoner, 4, "sand")
    }

    System.out.println("Done Registering Sanguine Extras Item Renderers")
  }

  private def registerOneTexture(item: Item) {
    ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
      def getModelLocation(stack: ItemStack): ModelResourceLocation = {
        return new ModelResourceLocation(item.getRegistryName, "inventory")
      }
    })
  }
}
