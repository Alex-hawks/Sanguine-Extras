package io.github.alex_hawks.SanguineExtras.common.constructs

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer

class BlockChest extends Block(Material.rock) with ITileEntityProvider {

  override def createNewTileEntity(world: World, meta: Int) = new TileChest(meta)
}

class TileChest(val meta: Int) extends TileEntity with IInventory {
  val chestContents = new Array[ItemStack]((meta + 4) * 9);

  override def closeInventory: Unit = {}
  override def decrStackSize(slot: Int, qty: Int): ItemStack = {
    if (chestContents(slot) == null || chestContents(slot).getItem() == null)
      null;
    else {
      val y = chestContents(slot).copy()
      y.stackSize = Math.max(Math.min(chestContents(slot).stackSize, qty), 0)
      markDirty()
      y
    }

  }
  override def getInventoryName: String = ???
  override def getInventoryStackLimit: Int = 64
  override def getSizeInventory: Int = chestContents.length
  override def getStackInSlot(slot: Int): ItemStack = chestContents(slot)
  override def getStackInSlotOnClosing(slot: Int): ItemStack = chestContents(slot)
  override def hasCustomInventoryName: Boolean = false
  override def isItemValidForSlot(Slot: Int, stack: ItemStack): Boolean = true
  override def isUseableByPlayer(player: EntityPlayer): Boolean = true
  override def openInventory: Unit = {}
  override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = { chestContents(slot) = stack; markDirty() }
}