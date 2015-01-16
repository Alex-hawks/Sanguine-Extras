package io.github.alex_hawks.SanguineExtras.common

import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import io.github.alex_hawks.SanguineExtras.common.constructs.ContainerChest
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest

class CommonProxy extends IGuiHandler {
  def registerClientStuff(): Unit = { }

  def getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = null
  def getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = id match {
    case 0 => new ContainerChest(player.inventory, world.getTileEntity(x, y, z).asInstanceOf[TileChest])
  }
}