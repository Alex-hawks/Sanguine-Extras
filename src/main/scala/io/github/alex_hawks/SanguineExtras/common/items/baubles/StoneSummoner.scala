package io.github.alex_hawks.SanguineExtras.common
package items.baubles

import java.util
import javax.annotation.{Nonnull, Nullable}

import WayofTime.bloodmagic.client.IVariantProvider
import WayofTime.bloodmagic.registry.ModRituals
import WayofTime.bloodmagic.util.helper.{NBTHelper, TextHelper}
import baubles.api.BaubleType
import io.github.alex_hawks.SanguineExtras.common.compat.Bauble
import io.github.alex_hawks.SanguineExtras.common.items.baubles.StoneConstants._
import io.github.alex_hawks.SanguineExtras.common.util.{BloodUtils, PlayerUtils, SanguineExtrasCreativeTab}
import io.github.alex_hawks.util.minecraft.common.Implicit._
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Blocks._
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumHand, NonNullList}
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.BlockSnapshot
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import vazkii.botania.api.item.IBlockProvider

object StoneConstants {

  object stacks {
    // These are defs. They are methods
    val empty = ItemStack.EMPTY
    def cobble = new ItemStack(COBBLESTONE, 64)
    def stone = new ItemStack(STONE, 64)
    def netherrack = new ItemStack(NETHERRACK, 64)
    def obsidian = new ItemStack(OBSIDIAN, 64)
    def sand = new ItemStack(SAND, 64)

    val array = Array(COBBLESTONE, STONE, NETHERRACK, OBSIDIAN, SAND)
  }

  object costs {
    val empty = 0
    val cobble = ModRituals.lavaRitual.getRefreshCost
    val stone = ModRituals.lavaRitual.getRefreshCost
    val netherrack = ModRituals.lavaRitual.getRefreshCost
    val obsidian = ModRituals.lavaRitual.getRefreshCost * 64
    val sand = ModRituals.lavaRitual.getRefreshCost

    val array = Array(cobble, stone, netherrack, obsidian, sand)
  }

}

@Optional.Interface(iface = "vazkii.botania.api.item.IBlockProvider", modid = "Botania", striprefs = true)
object StoneSummoner extends BaubleBase with IBlockProvider with IVariantProvider {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName("bauble_stone_summoner")
  this.setRegistryName("bauble_stone_summoner")
  this.setHasSubtypes(true)

  override def getBaubleType(itemStack: ItemStack): BaubleType = BaubleType.CHARM

  override def getUnlocalizedName(is: ItemStack): String = super.getUnlocalizedName + "." + is.getItemDamage

  override def addInformation(stack: ItemStack, @Nullable worldIn: World, tooltip: util.List[String], flagIn: ITooltipFlag) {
    NBTHelper.checkNBT(stack)

    val binding = getBinding(stack)
    if (binding != null) tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentOwner", binding.getOwnerName))
    else tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"))
  }

  @SideOnly(Side.CLIENT)
  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]) {
    if (tab == SanguineExtrasCreativeTab.Instance)
    for (i <- costs.array.indices)
      items.add(new ItemStack(this, 1, i))
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
    case (Blocks.STONE, _, 1) => -1 //  this lets it do variants of stone as well, like andesite and diorite
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
    return BloodUtils.drainSoulNetworkWithDamage(BloodUtils.getOrBind(thisStack, player).getOwnerId, drain, player)
  }

  override def gatherVariants(@Nonnull ls: Int2ObjectMap[String]): Unit = {
    var index = -1
    for (mat <- stacks.array)
      ls.put({index += 1; index}, s"material=${Block.REGISTRY.getNameForObject(mat)}")
  }
}

object StoneSummonHandler {
  val bauble = new ItemStack(StoneSummoner)

  @SubscribeEvent(priority = EventPriority.LOW)
  def rightClick(e: PlayerInteractEvent.RightClickBlock) {
    if (e.getWorld.isRemote)
      return
    val w = e.getEntityPlayer.getEntityWorld
    if (e.getEntityPlayer.isSneaking && e.getItemStack.isEmpty) {
      val (has, stack) = Bauble.isWearing(e.getEntityPlayer, bauble, true)

      if (has && BloodUtils.drainSoulNetwork(BloodUtils.getOrBind(stack, e.getEntityPlayer).getOwnerId, StoneSummoner.getCost(stack), e.getEntityPlayer)
        && e.getHand == EnumHand.MAIN_HAND) {
        PlayerUtils.putItemWithDrop(e.getEntityPlayer, StoneSummoner.getStacks(stack))
        e.setCanceled(true)
      }
    }
    if (e.getItemStack == null || e.getItemStack.isEmpty) {
      val (has, stack) = Bauble.isWearing(e.getEntityPlayer, bauble, true)
      if (has && e.getHand == EnumHand.MAIN_HAND) {
        val t = e.getEntityPlayer.getEntityWorld.getBlockState(e.getPos)
        val s = new BlockSnapshot(e.getEntityPlayer.getEntityWorld, e.getPos.shift(e.getFace), t)
        val be = new BlockEvent.PlaceEvent(s, t, e.getEntityPlayer, EnumHand.MAIN_HAND)
        if (!MinecraftForge.EVENT_BUS.post(be) && PlayerUtils.takeItem(e.getEntityPlayer, StoneSummoner.getStacks(stack).copyWithCount(1), stack))
          w.setBlockState(e.getPos, t, 0x3)
      }
    }
  }
}
