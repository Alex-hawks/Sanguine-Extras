package io.github.alex_hawks.SanguineExtras.common
package constructs

import java.util.function.Consumer

import javax.annotation.Nullable
import WayofTime.bloodmagic.block.IBMBlock
import WayofTime.bloodmagic.client.IMeshProvider
import com.google.common.base.Strings
import io.github.alex_hawks.SanguineExtras.common.constructs.Chest._
import io.github.alex_hawks.SanguineExtras.common.util.{PlayerUtils, SanguineExtrasCreativeTab}
import io.github.alex_hawks.util.minecraft.common.Implicit.iItemStack
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.{IProperty, PropertyInteger}
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.inventory.{Container, Slot}
import net.minecraft.item.ItemStack.EMPTY
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType.ENTITYBLOCK_ANIMATED
import net.minecraft.util._
import net.minecraft.util.math.{BlockPos, RayTraceResult}
import net.minecraft.util.text.{ITextComponent, TextComponentString}
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.{ChunkCache, IBlockAccess, World}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent
import net.minecraftforge.items.{IItemHandler, ItemStackHandler, SlotItemHandler}

import scala.annotation.meta.setter
import scala.collection.JavaConverters._


object Chest {
  val maxRows = 9
  val maxCols = 12
  val maxChestSize = maxRows * maxCols
  val maxTier = 4 // 0 indexed: this is the max tier, not the number of tiers
  val slotsPerTier = 18

  val textureLocGui = new ResourceLocation(Constants.Metadata.MOD_ID, "textures/gui/chest.png")
  val heightChange = 0.000625f
  val maxLidAngle = 1f
  val minLidAngle = 0f
  val maxHeightChange = 0.02f
  val lidMotion = 1f / 10f

  @(CapabilityInject @setter)(classOf[IItemHandler])
  var CapInv: Capability[IItemHandler] = null

  val PropTier: PropertyInteger = PropertyInteger.create("tier", 0, 4)

  def getTier(is: ItemStack): Int = {
    val tag = is.getSubCompound("BlockEntityTag")
    if (tag != null)
      return tag.getInteger("tier")
    0
  }

  def getActInvSize(tier: Int): Int = (tier + 2) * slotsPerTier
}

object BlockChest extends Block(Material.ROCK) with IBMBlock {
  this.setRegistryName(Constants.Metadata.MOD_ID, "sanguine_chest")
  this.setUnlocalizedName("sanguine_chest")
  this.setDefaultState(getDefaultState.withProperty[Integer,Integer](PropTier, 0))


  override def hasTileEntity(state: IBlockState) = true

  override def createTileEntity(world: World, state: IBlockState) = new TileChest()

  override def isOpaqueCube(state: IBlockState): Boolean = false

  override def isBlockNormalCube(state: IBlockState): Boolean = false

  override def getRenderType(state: IBlockState): EnumBlockRenderType = ENTITYBLOCK_ANIMATED

  override def onBlockActivated(w: World, pos: BlockPos, st: IBlockState, p: EntityPlayer, h: EnumHand, s: EnumFacing, x: Float, y: Float, z: Float): Boolean = {
    p.openGui(SanguineExtras.INSTANCE, 0, w, pos.getX, pos.getY, pos.getZ)
    true
  }

  override def getPickBlock(state: IBlockState, target: RayTraceResult, w: World, pos: BlockPos, player: EntityPlayer): ItemStack = {
    if (player.isSneaking && player.isCreative) {
      val is = new ItemStack(getItem)
      val chest = w.getTileEntity(pos).asInstanceOf[TileChest]
      val tag = chest.save(new NBTTagCompound)
      if (!tag.hasNoTags)
        is.setTagInfo("BlockEntityTag", tag)
      if (chest.hasCustomName)
        is.setStackDisplayName(chest.getCustomName)
      is
    } else {
      val chest = w.getTileEntity(pos).asInstanceOf[TileChest]
      new ItemStack(getItem).withNBT("BlockEntityTag::tier", chest.tier)
    }
  }

  override def getDrops(drops: NonNullList[ItemStack], w: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int): Unit = {
    // still necessary due to how the Destruction Sigil is implemented.

    val te = w.getTileEntity(pos)

    if (te.isInstanceOf[TileChest]) {
      SanguineExtras.LOG.fatal("Is a chest")
      val is = new ItemStack(getItem)
      val chest = te.asInstanceOf[TileChest]
      val tag = chest.save(new NBTTagCompound)
      if (!tag.hasNoTags)
        is.setTagInfo("BlockEntityTag", tag)
      if (chest.hasCustomName)
        is.setStackDisplayName(chest.getCustomName)

      drops.add(is)
    }
  }

  override def harvestBlock(w: World, p: EntityPlayer, pos: BlockPos, state: IBlockState, @Nullable te: TileEntity, stack: ItemStack): Unit = {
    // Can't call super due to the logic in that method, so I need to do some things here that it does there
    // This needs to exist, as it's the easiest point in the chain to override such that I still get the TileEntity to work with
    p.addStat(StatList.getBlockStats(this))
    p.addExhaustion(0.005F)

    if (te.isInstanceOf[TileChest]) {
      val is = new ItemStack(getItem)
      val chest = te.asInstanceOf[TileChest]
      val tag = chest.save(new NBTTagCompound)
      if (!tag.hasNoTags)
        is.setTagInfo("BlockEntityTag", tag)
      if (chest.hasCustomName)
        is.setStackDisplayName(chest.getCustomName)

      val fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack)
      val silky = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) != 0

      val event = new HarvestDropsEvent(w, pos, state, fortune, 1.0F, List(is).asJava, p, silky)
      if (!MinecraftForge.EVENT_BUS.post(event)) { // I'm aware that this is a possible exception, I'm being polite and conforming to standards
        for(item <- event.getDrops.asScala)
          if (w.rand.nextDouble <= event.getDropChance)
            Block.spawnAsEntity(w, pos, item)
      }
    }
  }

  override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit = {
    if (stack.hasDisplayName) {
      val te = worldIn.getTileEntity(pos)
      if (te.isInstanceOf[TileChest])
        te.asInstanceOf[TileChest].setCustomName(stack.getDisplayName)
    }
  }

  override val getItem = ItemBlockChest

  override def getSubBlocks(itemIn: CreativeTabs, items: NonNullList[ItemStack]): Unit = { /* no-op */ }

  override def createBlockState() = new BlockStateContainer(this, Array[IProperty[_ <: Comparable[_]]](PropTier):_*)

  override def getMetaFromState(state: IBlockState) = 0

  override def getStateFromMeta(meta: Int): IBlockState = this.getDefaultState

  override def getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
    val te = world match {
      case cache: ChunkCache => cache.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
      case _ => world.getTileEntity(pos)
    }
    if(te.isInstanceOf[TileChest])
      state.withProperty[Integer,Integer](PropTier, te.asInstanceOf[TileChest].tier)
    else
      state
  }
}

object ItemBlockChest extends ItemBlock(BlockChest) with IMeshProvider {
  this.setCreativeTab(SanguineExtrasCreativeTab.Instance)


  override def gatherVariants(ls: Consumer[String]): Unit = {
    for (i <- 0 until maxTier)
      ls.accept(s"tier=$i")
  }

  override def getMeshDefinition: ItemMeshDefinition = new ItemMeshDefinition {
    override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
      if (stack.getSubCompound("BlockEntityTag") != null && stack.getSubCompound("BlockEntityTag").hasKey("tier"))
        new ModelResourceLocation(ItemBlockChest.this.getRegistryName, "tier=" + stack.getSubCompound("BlockEntityTag").getInteger("tier"))
      else
        new ModelResourceLocation(ItemBlockChest.this.getRegistryName, "tier=0")
    }
  }

  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]): Unit = {
    if (tab == SanguineExtrasCreativeTab.Instance)
      for (i <- 0 to maxTier)
        items.add(new ItemStack(this).withNBT("BlockEntityTag::tier", i))
  }
}

class TileChest(var tier: Int, var name: String) extends TileEntity with ITickable {
  def this() = this(0, "")

  lazy val inv = new ItemStackHandler(actInvSize) {

    override def getStackInSlot(slot: Int): ItemStack = {
      if (0 <= slot && slot < stacks.size)
        return super.getStackInSlot(slot)
      ItemStack.EMPTY
    }

    override def setStackInSlot(slot: Int, stack: ItemStack): Unit = {
      if (0 <= slot && slot < stacks.size)
        super.setStackInSlot(slot, stack)
    }

    override def insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = {
      if (0 <= slot && slot < stacks.size)
        return super.insertItem(slot, stack, simulate)
      stack
    }

    override def extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = {
      if (0 <= slot && slot < stacks.size)
        return super.extractItem(slot, amount, simulate)
      ItemStack.EMPTY
    }

    override def getSlots = maxChestSize
  }

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
  def actInvSize = getActInvSize(this.tier)

  // Begin TileEntity overrides
  override def update(): Unit = {
    prevLidAngle = lidAngle

    rotation += 1
    if (rotation >= 360)
      rotation %= 360

    if (height > 0)
      motion -= heightChange

    if (height < 0)
      motion += heightChange

    height += Math.min(motion, maxHeightChange)

    getWorld.markBlockRangeForRenderUpdate(pos, pos)

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
    save(tag)

    tag
  }

  def save(tag: NBTTagCompound): NBTTagCompound = {
    val tag2 = inv.serializeNBT()

    if (!tag2.getTagList("Items", 10).hasNoTags)
      tag.setTag("inventory", tag2)

    tag.setInteger("tier", tier)
    if (!Strings.isNullOrEmpty(name))
      tag.setString("name", name)

    tag
  }

  def writeSyncData(tag: NBTTagCompound): NBTTagCompound = {
    tag.setFloat("rotation", rotation)
    tag.setFloat("height", height)
    tag.setFloat("motion", motion)
    tag.setInteger("tier", tier)
    if (!Strings.isNullOrEmpty(name))
      tag.setString("name", name)
    tag
  }

  override def readFromNBT(tag: NBTTagCompound): Unit = {
    super.readFromNBT(tag)
    tier = tag.getInteger("tier")
    name = tag.getString("name")

    val tag2 = tag.getCompoundTag("inventory")
    load(tag2)
  }

  def load(tag: NBTTagCompound): Unit = {
    if (tag != null) {
      if (tag.getInteger("Size") <= actInvSize)
        tag.setInteger("Size", actInvSize)
      inv.deserializeNBT(tag)
    }
  }

  def readSyncData(tag: NBTTagCompound): NBTTagCompound = {
    rotation = tag.getFloat("rotation")
    height = tag.getFloat("height")
    motion = tag.getFloat("motion")
    tier = tag.getInteger("tier")
    name = tag.getString("name")
    tag
  }

  override def getUpdatePacket: SPacketUpdateTileEntity = {
    val tag = writeSyncData(new NBTTagCompound)
    new SPacketUpdateTileEntity(this.pos, 1, tag)
  }

  override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity): Unit = {
    readSyncData(pkt.getNbtCompound)
  }

  override def hasCapability(cap: Capability[_], dir: EnumFacing): Boolean = {
    if (cap == CapInv)
      return true
    super.hasCapability(cap, dir)
  }

  override def getCapability[T](cap: Capability[T], dir: EnumFacing): T = {
    if (cap == CapInv)
      CapInv.cast(inv)
    super.getCapability(cap, dir)
  }

  def openInventory(p: EntityPlayer): Unit = {
    numPlayersUsing += 1
  }

  def closeInventory(p: EntityPlayer): Unit = {
    numPlayersUsing -= 1
  }

  override def getDisplayName: ITextComponent = new TextComponentString(if (hasCustomName) getCustomName else "container.sanguine_chest")

  def hasCustomName: Boolean = !(null == name || "".equals(name))

  def getCustomName: String = name

  def setCustomName(n: String): Unit = name = n
}

class ContainerChest(val player: EntityPlayer, val chest: TileChest) extends Container {

  chest.openInventory(player)
  for (i <- 0 until maxRows)
    for (j <- 0 until maxCols)
      this.addSlotToContainer(new SlotItemHandler(chest.inv, i * maxCols + j, 12 + j * 18, 8 + i * 18))

  for (playerInvRow <- 0 until 3)
    for (playerInvCol <- 0 until 9)
      addSlotToContainer(new Slot(player.inventory, playerInvCol + playerInvRow * 9 + 9, 39 + playerInvCol * 18, 174 + playerInvRow * 18));

  for (playerInvCol <- 0 until 9)
    addSlotToContainer(new Slot(player.inventory, playerInvCol, 39 + playerInvCol * 18, 232));

  override def canInteractWith(player: EntityPlayer): Boolean = PlayerUtils.isRealPlayer(player)

  override def transferStackInSlot(player: EntityPlayer, slotID: Int): ItemStack = {
    var ret: ItemStack = EMPTY
    val slot = this.inventorySlots.get(slotID)

    if (slot.getHasStack) {
      val moved = slot.getStack
      ret = moved.copy
      val size = chest.actInvSize

      if (slotID < size) {
        if (!this.mergeItemStack(moved, size, this.inventorySlots.size(), true)) {
          return EMPTY
        }
      } else if (!this.mergeItemStack(moved, 0, size, false)) {
        return EMPTY
      }

      if (moved.isEmpty) {
        slot.putStack(EMPTY)
      } else {
        slot.onSlotChanged()
      }
    }

    ret
  }

  override def onContainerClosed(player: EntityPlayer): Unit = chest.closeInventory(player)
}