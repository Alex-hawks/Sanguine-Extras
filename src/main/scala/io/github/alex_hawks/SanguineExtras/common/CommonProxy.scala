package io.github.alex_hawks.SanguineExtras.common

import io.github.alex_hawks.SanguineExtras.common.constructs.{ContainerChest, TileChest}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class CommonProxy extends IGuiHandler {
  def registerClientStuff(): Unit = {}

  def getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = null

  def getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Object = id match {
    case 0 => new ContainerChest(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileChest])
  }
}