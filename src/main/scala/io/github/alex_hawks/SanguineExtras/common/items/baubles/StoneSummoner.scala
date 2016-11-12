package io.github.alex_hawks.SanguineExtras.common.items.baubles

import javax.annotation.Nonnull

import WayofTime.bloodmagic.api.util.helper.{PlayerHelper, NBTHelper}
import WayofTime.bloodmagic.registry.{ModBlocks, ModRituals}
import WayofTime.bloodmagic.util.helper.TextHelper
import baubles.api.BaubleType
import com.google.common.base.Strings
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.util.{PlayerUtils, BloodUtils, Bauble, SanguineExtrasCreativeTab}
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumHand
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import StoneConstants._
import vazkii.botania.api.item.IBlockProvider

object StoneConstants {
  object stacks {
    def empty = null
    def cobble = new ItemStack(Blocks.COBBLESTONE, 64)
    def stone = new ItemStack(Blocks.STONE, 64)
    def netherrack = new ItemStack(Blocks.NETHERRACK, 64)
    def obsidian = new ItemStack(Blocks.OBSIDIAN, 64)
    def sand = new ItemStack(Blocks.SAND, 64)
  }
  object costs {
    val empty = 0
    val cobble = ModRituals.lavaRitual.getRefreshCost
    val stone = ModRituals.lavaRitual.getRefreshCost
    val netherrack = ModRituals.lavaRitual.getRefreshCost
    val obsidian = ModRituals.lavaRitual.getRefreshCost * 64
    val sand = ModRituals.lavaRitual.getRefreshCost
  }
}

@Optional.Interface(iface = "vazkii.botania.api.item.IBlockProvider", modid = "Botania", striprefs = true)
object StoneSummoner extends BaubleBase with IBlockProvider {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName("baubleStoneSummoner")
  this.setRegistryName("baubleStoneSummoner")
  this.setHasSubtypes(true)

  override def getBaubleType(itemStack: ItemStack): BaubleType = BaubleType.CHARM

  override def getUnlocalizedName(is: ItemStack): String = super.getUnlocalizedName + "." + is.getItemDamage

  override def addInformation(stack: ItemStack, par2EntityPlayer: EntityPlayer, tooltip: java.util.List[String], par4: Boolean) {
    NBTHelper.checkNBT(stack)

    if (!Strings.isNullOrEmpty(getOwnerUUID(stack)))
      tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)))
    else tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"))
  }

  @SideOnly(Side.CLIENT)
  override def getSubItems(item: Item, tab: CreativeTabs, items: java.util.List[ItemStack]) {
    for(i <- 0 until 5)
      items.add(new ItemStack(item, 1, i))
  }

  def getStacks(@Nonnull stack: ItemStack): ItemStack = stack.getItemDamage match {
    case 0 => stacks.cobble
    case 1 => stacks.stone
    case 2 => stacks.netherrack
    case 3 => stacks.obsidian
    case 4 => stacks.sand
    case _ => stacks.empty
  }

  def getCost(@Nonnull stack: ItemStack): Int = stack.getItemDamage match {
    case 0 => costs.cobble
    case 1 => costs.stone
    case 2 => costs.netherrack
    case 3 => costs.obsidian
    case 4 => costs.sand
    case _ => costs.empty
  }

  override def getBlockCount(player: EntityPlayer, requester: ItemStack, thisStack: ItemStack, block: Block, i: Int): Int = (block, i, thisStack.getMetadata) match {
    case (Blocks.COBBLESTONE, _, 0) => -1
    case (Blocks.STONE, _, 1) => -1
    case (Blocks.NETHERRACK, _, 2) => -1
    case (Blocks.OBSIDIAN, _, 3) => -1
    case (Blocks.SAND, _, 4) => -1
    case _ => 0
  }

  override def provideBlock(player: EntityPlayer, requester: ItemStack, thisStack: ItemStack, block: Block, i: Int, b: Boolean): Boolean = {
    var drain = getCost(thisStack)
    if (drain % 64 == 0)
      drain /= 64
    else {
      drain /= 64
      drain += 1
    }
    if (!b)
      return true
    return BloodUtils.drainSoulNetworkWithDamage(BloodUtils.getOrBind(thisStack, player), player, drain)
  }
}
object StoneSummonHandler {
  val bauble = new ItemStack(StoneSummoner)

  @SubscribeEvent(priority = EventPriority.LOW)
  def rightClick(e: PlayerInteractEvent.RightClickBlock) {
    if (e.getWorld.isRemote)
      return;
    if (e.getEntityPlayer.isSneaking && e.getItemStack == null) {
      val (has, stack)= Bauble.isWearing(e.getEntityPlayer, bauble, true)

      if(has && BloodUtils.drainSoulNetwork(BloodUtils.getOrBind(stack, e.getEntityPlayer), StoneSummoner.getCost(stack), e.getEntityPlayer)
        && e.getHand == EnumHand.MAIN_HAND ) {
        PlayerUtils.putItemWithDrop(e.getEntityPlayer, StoneSummoner.getStacks(stack))
        e.setCanceled(true)
      }
    }
  }
}
