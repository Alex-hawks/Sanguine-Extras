package io.github.alex_hawks.SanguineExtras.common
package constructs

import org.lwjgl.opengl.GL11
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper
import cpw.mods.fml.relauncher.SideOnly
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras
import io.github.alex_hawks.SanguineExtras.common.util.WorldUtils.dropItem
import io.github.alex_hawks.SanguineExtras.common.constructs.Chest._
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraft.inventory.IInventory

object Chest {
  val maxChestSize = 108
  val maxRows = 9
  val maxCols = 12

  val textureLocGui = new ResourceLocation(Constants.MetaData.MOD_ID.toLowerCase, "textures/gui/chest.png")
  val heightChange = 0.000625f
  val maxLidAngle = 1f
  val minLidAngle = 0f
  val maxHeightChange = 0.02f
  val lidMotion = 1f / 10f
}

class BlockChest extends BlockContainer(Material.rock) {

  this.setBlockName("blockSanguineChest");
  setCreativeTab(SanguineExtrasCreativeTab.Instance);

  override def getRenderType = -1
  override def createNewTileEntity(world: World, meta: Int) = new TileChest(meta)
  override def isOpaqueCube: Boolean = false
  override def renderAsNormalBlock: Boolean = false
  override def onBlockActivated(w: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, hitX: Float, hitY: Float, hitZ: Float) = {
    player.openGui(SanguineExtras.INSTANCE, 0, w, x, y, z)
    true
  }
  override def breakBlock(w: World, x: Int, y: Int, z: Int, block: Block, meta: Int) = {
    val te = w.getTileEntity(x, y, z).asInstanceOf[TileChest]

    for (item <- te.chestContents)
      dropItem(w, item, x, y, z)

    super.breakBlock(w, x, y, z, block, meta)
  }
}

class TileChest(val meta: Int) extends TileEntity with IInventory {
  val chestContents = new Array[ItemStack](Chest.maxChestSize);

  // the Chest Levitates and slowly rotates
  var lidAngle: Float = _
  var prevLidAngle: Float = _
  var ticksSinceSync: Int = _
  var rotation: Float = _
  var height: Float = _
  var motion: Float = 0.01f
  var lidMoving: Boolean = _
  var numPlayersUsing: Int = _

  // Begin calculations
  def actInvSize = (meta + 2) * 18

  // Begin TileEntity overrides
  override def canUpdate = true
  override def updateEntity = {
    prevLidAngle = lidAngle

    rotation += 1
    if (rotation >= 360)
      rotation %= 360

    if (height > 0)
      motion -= heightChange

    if (height < 0)
      motion += heightChange

    height += Math.min(motion, maxHeightChange)

    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)

    if (lidMoving) {
      if (numPlayersUsing > 0) {
        lidAngle += lidMotion
        if (lidAngle > maxLidAngle) {
          lidMoving = false
          lidAngle = maxLidAngle
        }
      } else if (numPlayersUsing <= 0) {
        lidAngle -= lidMotion
        if (lidAngle < minLidAngle) {
          lidMoving = false
          lidAngle = minLidAngle
        }
      }
    }
  }

  override def receiveClientEvent(eventType: Int, value: Int) = eventType match {
    case 1 =>
      numPlayersUsing = value
      lidMoving = true
      true
    case _ =>
      super.receiveClientEvent(eventType, value)
  }

  override def writeToNBT(tag: NBTTagCompound) = {
    val tag2 = new NBTTagCompound
    for (i <- 0 until maxChestSize)
      if (chestContents(i) != null)
        tag2.setTag("slot" + i, chestContents(i).writeToNBT(new NBTTagCompound))

    tag.setTag("inventory", tag2)
    writeSyncData(tag)
  }

  def writeSyncData(tag: NBTTagCompound): NBTTagCompound = {
    tag.setFloat("rotation", rotation)
    tag.setFloat("height", height)
    tag.setFloat("motion", motion)
    tag
  }

  override def readFromNBT(tag: NBTTagCompound) = {
    val tag2 = tag.getCompoundTag("inventory")
    if (tag2 != null)
      for (i <- 0 until maxChestSize)
        chestContents(i) = ItemStack.loadItemStackFromNBT(tag2.getCompoundTag("slot" + i))

    readSyncData(tag)
  }

  def readSyncData(tag: NBTTagCompound): NBTTagCompound = {
    rotation = tag.getFloat("rotation")
    height = tag.getFloat("height")
    motion = tag.getFloat("motion")
    tag
  }

  override def getDescriptionPacket: Packet = {
    val tag = writeSyncData(new NBTTagCompound)
    new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag)
  }

  override def onDataPacket(net: NetworkManager, pkt: S35PacketUpdateTileEntity) = {
    readSyncData(pkt.func_148857_g)
  }

  // Begin IInventory
  override def closeInventory: Unit = {
    this.numPlayersUsing -= 1
    if (this.numPlayersUsing < 0)
      this.numPlayersUsing = 0;
    this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing)
  }
  override def decrStackSize(slot: Int, qty: Int): ItemStack = {
    if (chestContents(slot) == null || chestContents(slot).getItem == null)
      null;
    else {
      val y = chestContents(slot).copy
      y.stackSize = Math.max(Math.min(chestContents(slot).stackSize, qty), 0)
      markDirty
      y
    }

  }
  override def getInventoryName: String = "container.sanguine_chest"
  override def getInventoryStackLimit: Int = 64
  override def getSizeInventory: Int = actInvSize
  override def getStackInSlot(slot: Int): ItemStack = chestContents(slot)
  override def getStackInSlotOnClosing(slot: Int): ItemStack = chestContents(slot)
  override def hasCustomInventoryName: Boolean = false
  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = slot < actInvSize && slot >= 0
  override def isUseableByPlayer(player: EntityPlayer): Boolean = true
  override def openInventory: Unit = {
    this.numPlayersUsing += 1
    this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType, 1, this.numPlayersUsing)
  }
  override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = { chestContents(slot) = stack; markDirty }
}

class ContainerChest(val player: InventoryPlayer, val chest: TileChest) extends Container {

  chest.openInventory
  for (i <- 0 until maxRows)
    for (j <- 0 until maxCols)
      this.addSlotToContainer(new SlotChest(chest, i * maxCols + j, 12 + j * 18, 8 + i * 18))

  for (playerInvRow <- 0 until 3)
    for (playerInvCol <- 0 until 9)
      addSlotToContainer(new Slot(player, playerInvCol + playerInvRow * 9 + 9, 39 + playerInvCol * 18, 174 + playerInvRow * 18));

  for (playerInvCol <- 0 until 9)
    addSlotToContainer(new Slot(player, playerInvCol, 39 + playerInvCol * 18, 232));

  override def canInteractWith(player: EntityPlayer): Boolean = !SpellHelper.isFakePlayer(player.worldObj, player)

  override def transferStackInSlot(player: EntityPlayer, slotID: Int): ItemStack = {
    var itemstack: ItemStack = null
    var slot = this.inventorySlots.get(slotID).asInstanceOf[Slot]

    if (slot != null && slot.getHasStack()) {
      val itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      val size = chest.actInvSize

      if (slotID < size) {
        if (!this.mergeItemStack(itemstack1, size, this.inventorySlots.size(), true)) {
          return null;
        }
      } else if (!this.mergeItemStack(itemstack1, 0, size, false)) {
        return null;
      }

      if (itemstack1.stackSize == 0) {
        slot.putStack(null);
      } else {
        slot.onSlotChanged();
      }
    }

    return itemstack;
  }

  override def onContainerClosed(player: EntityPlayer) = {
    chest.closeInventory
  }

}

class SlotChest(val inv: TileChest, index: Int, x: Int, y: Int) extends Slot(inv, index, x, y) {

  override def isItemValid(stack: ItemStack): Boolean = {
    inv.actInvSize > index
  }
}

