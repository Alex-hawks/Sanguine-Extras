package io.github.alex_hawks.SanguineExtras.common
package constructs

import java.util.Locale
import javax.annotation.Nullable

import io.github.alex_hawks.SanguineExtras.common.constructs.Chest._
import io.github.alex_hawks.SanguineExtras.common.util.{PlayerUtils, SanguineExtrasCreativeTab}
import io.github.alex_hawks.util.minecraft.common.WorldUtils.dropItem
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World

object Chest {
  val maxRows = 9
  val maxCols = 12
  val maxChestSize = maxRows * maxCols
  val maxTier = 4
  val slotsPerTier = 18

  val textureLocGui = new ResourceLocation(Constants.MetaData.MOD_ID.toLowerCase, "textures/gui/chest.png")
  val heightChange = 0.000625f
  val maxLidAngle = 1f
  val minLidAngle = 0f
  val maxHeightChange = 0.02f
  val lidMotion = 1f / 10f
}

object BlockChest extends BlockContainer(Material.ROCK) {

  this.setRegistryName(Constants.MetaData.MOD_ID.toLowerCase(Locale.ROOT), "sanguineChest");
  this.setUnlocalizedName("sanguineChest")
  setCreativeTab(SanguineExtrasCreativeTab.Instance);

  override def createNewTileEntity(world: World, meta: Int) = new TileChest(meta)

  override def isOpaqueCube(state: IBlockState): Boolean = false

  override def isBlockNormalCube(state: IBlockState): Boolean = false

  override def onBlockActivated(w: World, pos: BlockPos, st: IBlockState, p: EntityPlayer, h: EnumHand, @Nullable item: ItemStack, s: EnumFacing, x: Float, y: Float, z: Float) = {
    p.openGui(SanguineExtras.INSTANCE, 0, w, pos.getX, pos.getY, pos.getZ)
    true
  }

  override def breakBlock(w: World, pos: BlockPos, state: IBlockState) = {
    val te = w.getTileEntity(pos).asInstanceOf[TileChest]

    for (item <- te.chestContents)
      dropItem(w, item, pos.getX, pos.getY, pos.getZ)

    super.breakBlock(w, pos, state)
  }
}

class TileChest(var tier: Int) extends TileEntity with IInventory with ITickable {
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

  def this() = this(0)

  // Begin calculations
  def actInvSize = (tier + 2) * slotsPerTier

  // Begin TileEntity overrides
  def update = {
    prevLidAngle = lidAngle

    rotation += 1
    if (rotation >= 360)
      rotation %= 360

    if (height > 0)
      motion -= heightChange

    if (height < 0)
      motion += heightChange

    height += Math.min(motion, maxHeightChange)

    worldObj.markBlockRangeForRenderUpdate(pos, pos)

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
    super.writeToNBT(tag)
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
    tag.setInteger("tier", tier)
    tag
  }

  override def readFromNBT(tag: NBTTagCompound) = {
    super.readFromNBT(tag)
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
    tier = tag.getInteger("tier")
    tag
  }

  override def getUpdatePacket: SPacketUpdateTileEntity = {
    val tag = writeSyncData(new NBTTagCompound)
    new SPacketUpdateTileEntity(this.pos, 1, tag)
  }

  override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) = {
    readSyncData(pkt.getNbtCompound)
  }

  // Begin IInventory
  override def closeInventory(player: EntityPlayer): Unit = {
    this.numPlayersUsing -= 1
    if (this.numPlayersUsing < 0)
      this.numPlayersUsing = 0;
    this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing)
  }

  override def decrStackSize(slot: Int, qty: Int): ItemStack = {
    if (chestContents(slot) == null || chestContents(slot).getItem == null)
      null;
    else {
      val y = chestContents(slot).copy
      y.stackSize = Math.max(Math.min(chestContents(slot).stackSize, qty), 0)
      if (chestContents(slot).stackSize == y.stackSize)
        chestContents(slot) = null
      else
        chestContents(slot).stackSize -= y.stackSize
      markDirty
      y
    }

  }

  override def getName: String = "container.sanguine_chest"

  override def getInventoryStackLimit: Int = 64

  override def getSizeInventory: Int = actInvSize

  override def getStackInSlot(slot: Int): ItemStack = chestContents(slot)

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = slot < actInvSize && slot >= 0

  override def isUseableByPlayer(player: EntityPlayer): Boolean = true

  override def openInventory(player: EntityPlayer): Unit = {
    this.numPlayersUsing += 1
    this.worldObj.addBlockEvent(this.pos, this.getBlockType, 1, this.numPlayersUsing)
  }

  override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = {
    chestContents(slot) = stack;
    markDirty
  }

  override def removeStackFromSlot(index: Int): ItemStack = {
    if (index > -1 && index < maxChestSize && chestContents(index) != null) {
      val s = chestContents(index)
      chestContents(index) = null
      s
    } else
      null
  }

  override def clear(): Unit = for (x <- 0 until chestContents.length) chestContents(x) = null

  override def getFieldCount: Int = 0

  override def getField(id: Int): Int = 0

  override def setField(id: Int, value: Int): Unit = {}

  override def getDisplayName: ITextComponent = null

  override def hasCustomName: Boolean = false;
}

class ContainerChest(val player: EntityPlayer, val chest: TileChest) extends Container {

  chest.openInventory(player)
  for (i <- 0 until maxRows)
    for (j <- 0 until maxCols)
      this.addSlotToContainer(new SlotChest(chest, i * maxCols + j, 12 + j * 18, 8 + i * 18))

  for (playerInvRow <- 0 until 3)
    for (playerInvCol <- 0 until 9)
      addSlotToContainer(new Slot(player.inventory, playerInvCol + playerInvRow * 9 + 9, 39 + playerInvCol * 18, 174 + playerInvRow * 18));

  for (playerInvCol <- 0 until 9)
    addSlotToContainer(new Slot(player.inventory, playerInvCol, 39 + playerInvCol * 18, 232));

  override def canInteractWith(player: EntityPlayer): Boolean = PlayerUtils.isNotFakePlayer(player)

  override def transferStackInSlot(player: EntityPlayer, slotID: Int): ItemStack = {
    var itemstack: ItemStack = null
    val slot = this.inventorySlots.get(slotID)

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

    itemstack
  }

  override def onContainerClosed(player: EntityPlayer) = chest.closeInventory(player)
}

class SlotChest(val inv: TileChest, index: Int, x: Int, y: Int) extends Slot(inv, index, x, y) {

  override def isItemValid(stack: ItemStack): Boolean = inv.actInvSize > index
}

