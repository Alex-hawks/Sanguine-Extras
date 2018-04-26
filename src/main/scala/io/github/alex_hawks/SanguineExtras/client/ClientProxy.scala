package io.github.alex_hawks.SanguineExtras.client

import io.github.alex_hawks.SanguineExtras.client.constructs.{GuiChest, RenderChest}
import io.github.alex_hawks.SanguineExtras.client.sigil_utils.UtilsBuilding
import io.github.alex_hawks.SanguineExtras.common.CommonProxy
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry

object ClientProxy {
}

class ClientProxy extends CommonProxy {

  override def registerClientStuff() {
    MinecraftForge.EVENT_BUS.register(new UtilsBuilding)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileChest], RenderChest)
  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ID match {
    case 0 =>
      new GuiChest(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileChest])
    case _ =>
      null
  }
}
