package io.github.alex_hawks.SanguineExtras.common.items.baubles

import java.util
import java.util.function.Consumer

import WayofTime.bloodmagic.block.BlockLifeEssence
import WayofTime.bloodmagic.client.IMeshProvider
import WayofTime.bloodmagic.registry.ModRituals
import WayofTime.bloodmagic.util.helper.{NBTHelper, TextHelper}
import baubles.api.BaubleType
import com.google.common.base.Strings
import io.github.alex_hawks.SanguineExtras.common.compat.Bauble
import io.github.alex_hawks.SanguineExtras.common.items.baubles.LiquidSummonerHelper._
import io.github.alex_hawks.SanguineExtras.common.util.{BloodUtils, PlayerUtils, SanguineExtrasCreativeTab}
import io.github.alex_hawks.util.minecraft.common.Implicit.iItemStack
import javax.annotation.Nonnull
import net.minecraft.block.Block
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack, FluidUtil}
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import vazkii.botania.api.item.IBlockProvider

import scala.collection.mutable

object LiquidSummonerHelper {
  val KEY_FLUID = "fluid"
  
  lazy val fluid = new mutable.HashMap[String, () => Int] {
    put("water", { () => ModRituals.waterRitual.getRefreshCost })
    put("lava", { () => ModRituals.lavaRitual.getRefreshCost })
    put(BlockLifeEssence.getLifeEssence.getName, Fluid.BUCKET_VOLUME)
    
    
    val UNKNOWN = ( { () => 0 }, new ItemStack(Items.BUCKET))
    
    
    def getCost(key: String): Int = this.getOrElse(key, UNKNOWN._1)()
    
    def getFull(key: String): ItemStack = {
      if (Strings.isNullOrEmpty(key))
        return UNKNOWN._2
      FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid(key), Fluid.BUCKET_VOLUME))
    }
    
    def getFluid(key: String): Fluid = {
      FluidRegistry.getFluid(key)
    }
    
    def put(key: String, value: Int): Unit = {
      this.put(key, { () => value })
    }
  }
  
  def getType(is: ItemStack): String = {
    if (is.isEmpty || is.getTagCompound == null)
      ""
    else {
      is.getTagCompound.getString(KEY_FLUID) match {
        case null => ""
        case "" => ""
        case x => x
      }
    }
  }
}

@Optional.Interface(iface = "vazkii.botania.api.item.IBlockProvider", modid = "Botania", striprefs = true)
object LiquidSummoner extends BaubleBase with IBlockProvider with IMeshProvider {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName("bauble_liquid_summoner")
  this.setRegistryName("bauble_liquid_summoner")
  this.setHasSubtypes(true)
  
  override def getBaubleType(itemStack: ItemStack): BaubleType = BaubleType.AMULET
  
  override def getUnlocalizedName(is: ItemStack): String = {
    if (is.hasTagCompound)
      super.getUnlocalizedName + "." + is.getTagCompound.getString(KEY_FLUID)
    else
      super.getUnlocalizedName + "..bugged" // The ".." is in case anyone names their fluid "bugged". Not sure why they'd do that though, but just covering all bases
  }
  
  override def addInformation(stack: ItemStack, world: World, tooltip: util.List[String], flag: ITooltipFlag) {
    NBTHelper.checkNBT(stack)
    
    val binding = getBinding(stack)
    if (binding != null) tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentOwner", binding.getOwnerName))
    else tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"))
  }
  
  @SideOnly(Side.CLIENT)
  override def getSubItems(tab: CreativeTabs, items: NonNullList[ItemStack]) {
    if (tab == SanguineExtrasCreativeTab.Instance)
      for ((mat, _) <- fluid)
        items.add(new ItemStack(this).withNBT(KEY_FLUID, mat))
  }
  
  def getFilledBucket(@Nonnull stack: ItemStack): ItemStack = {
    fluid.getFull(stack.getTagCompound.getString(KEY_FLUID))
  }
  
  def getFillCost(@Nonnull stack: ItemStack): Int = {
    fluid.getCost(stack.getTagCompound.getString(KEY_FLUID))
  }
  
  override def getBlockCount(player: EntityPlayer, requester: ItemStack, thisStack: ItemStack, block: Block, i: Int): Int = {
    if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && thisStack.getTagCompound.getString(KEY_FLUID) == "water")
      -1
    else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) && thisStack.getTagCompound.getString(KEY_FLUID) == "lava")
      -1
    else if (block == fluid.getFluid(thisStack.getTagCompound.getString(KEY_FLUID)).getBlock)
      -1
    else
      0
  }
  
  override def provideBlock(player: EntityPlayer, requester: ItemStack, thisStack: ItemStack, block: Block, i: Int, b: Boolean): Boolean = {
    val drain = getFillCost(thisStack)
    if (!b)
      true
    else
      BloodUtils.drainSoulNetworkWithDamage(BloodUtils.getOrBind(thisStack, player).getOwnerId, drain, player)
  }
  
  override def gatherVariants(ls: Consumer[String]): Unit = {
    for ((mat, _) <- fluid)
      ls.accept("fluid=" + mat)
  }
  
  override val getMeshDefinition: ItemMeshDefinition = new ItemMeshDefinition {
    override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
      if (stack.hasTagCompound)
        new ModelResourceLocation(LiquidSummoner.this.getRegistryName, "fluid=" + stack.getTagCompound.getString(KEY_FLUID))
      else
        new ModelResourceLocation(LiquidSummoner.this.getRegistryName, "fluid=water")
    }
  }
}

object LiquidSummonHandler {
  val bauble = new ItemStack(LiquidSummoner)
  
  @SubscribeEvent(priority = EventPriority.LOW)
  def rightClick(e: PlayerInteractEvent.RightClickItem) {
    if (e.getWorld.isRemote)
      return
    if (e.getEntityPlayer.isSneaking && e.getItemStack.getItem.equals(Items.BUCKET)) {
      val (has, stack) = Bauble.isWearing(e.getEntityPlayer, bauble, ignoreMeta = true)
      
      if (has && BloodUtils.drainSoulNetworkWithDamage(BloodUtils.getOrBind(stack, e.getEntityPlayer).getOwnerId, LiquidSummoner.getFillCost(stack), e.getEntityPlayer)
        && PlayerUtils.takeItem(e.getEntityPlayer, fluid.UNKNOWN._2, stack)) {
        PlayerUtils.putItemWithDrop(e.getEntityPlayer, LiquidSummoner.getFilledBucket(stack))
        e.setCanceled(true)
      }
    }
  }
}
