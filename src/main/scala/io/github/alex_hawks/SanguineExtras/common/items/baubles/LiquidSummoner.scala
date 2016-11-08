package io.github.alex_hawks.SanguineExtras.common.items.baubles

import javax.annotation.Nonnull

import WayofTime.bloodmagic.api.util.helper.{PlayerHelper, NBTHelper}
import WayofTime.bloodmagic.block.BlockLifeEssence
import WayofTime.bloodmagic.registry.ModRituals
import WayofTime.bloodmagic.util.helper.TextHelper
import baubles.api.BaubleType
import com.google.common.base.Strings
import io.github.alex_hawks.SanguineExtras.common.util.{SanguineExtrasCreativeTab, BloodUtils, PlayerUtils, Bauble}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids.{Fluid, FluidStack}
import net.minecraftforge.fml.common.eventhandler.{SubscribeEvent, EventPriority}

import LiquidConstants._
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

object LiquidConstants {
  private val lp = {
    val stack = new ItemStack(ForgeModContainer.getInstance.universalBucket)
    ForgeModContainer.getInstance.universalBucket.fill(stack, new FluidStack(BlockLifeEssence.getLifeEssence, Fluid.BUCKET_VOLUME), true)
    stack
  }
  object buckets {
    def empty = new ItemStack(Items.BUCKET)
    def water = new ItemStack(Items.WATER_BUCKET)
    def lava = new ItemStack(Items.LAVA_BUCKET)
    def lifeEssence = lp.copy
  }
  object costs {
    val empty = 0
    val water = ModRituals.waterRitual.getRefreshCost
    val lava = ModRituals.lavaRitual.getRefreshCost
    val lifeEssence = 1000
  }
}

object LiquidSummoner extends BaubleBase {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName("baubleLiquidSummoner")
  this.setRegistryName("baubleLiquidSummoner")
  this.setHasSubtypes(true)

  override def getBaubleType(itemStack: ItemStack): BaubleType = BaubleType.AMULET

  override def getUnlocalizedName(is: ItemStack): String = super.getUnlocalizedName + "." + is.getItemDamage

  override def addInformation(stack: ItemStack, par2EntityPlayer: EntityPlayer, tooltip: java.util.List[String], par4: Boolean) {
    NBTHelper.checkNBT(stack)

    if (!Strings.isNullOrEmpty(getOwnerUUID(stack)))
      tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)))
    else tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"))
  }

  @SideOnly(Side.CLIENT)
  override def getSubItems(item: Item, tab: CreativeTabs, items: java.util.List[ItemStack]) {
    for(i <- 0 to 2)
      items.add(new ItemStack(item, 1, i))
  }

  def getFilledBucket(@Nonnull stack: ItemStack): ItemStack = stack.getItemDamage match {
    case 0 => buckets.water
    case 1 => buckets.lava
    case 2 => buckets.lifeEssence
    case _ => buckets.empty
  }

  def getFillCost(@Nonnull stack: ItemStack): Int = stack.getItemDamage match {
    case 0 => costs.water
    case 1 => costs.lava
    case 2 => costs.lifeEssence
    case _ => costs.empty
  }
}
object LiquidSummonHandler {
  val bauble = new ItemStack(LiquidSummoner)

  @SubscribeEvent(priority = EventPriority.LOW)
  def rightClick(e: PlayerInteractEvent.RightClickItem) {
    if (e.getWorld.isRemote)
      return;
    if (e.getEntityPlayer.isSneaking && e.getItemStack.getItem.equals(Items.BUCKET)) {
      val (has, stack)= Bauble.isWearing(e.getEntityPlayer, bauble, true)

      if(has && BloodUtils.drainSoulNetworkWithDamage(BloodUtils.getOrBind(stack, e.getEntityPlayer), e.getEntityPlayer, LiquidSummoner.getFillCost(stack))
        && PlayerUtils.takeItem(e.getEntityPlayer, buckets.empty)) {
        PlayerUtils.putItemWithDrop(e.getEntityPlayer, LiquidSummoner.getFilledBucket(stack))
        e.setCanceled(true)
      }
    }
  }
}
