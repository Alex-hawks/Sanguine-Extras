package io.github.alex_hawks.SanguineExtras.common.util

import java.util

import WayofTime.bloodmagic.api.event.BloodMagicCraftedEvent.{AlchemyTable ⇒ AlchemyTableCraftEvent, Altar ⇒ AltarCraftEvent}
import WayofTime.bloodmagic.api.impl.BloodMagicAPI
import WayofTime.bloodmagic.ritual.IRitualStone
import WayofTime.bloodmagic.ritual.types.RitualCrushing
import WayofTime.bloodmagic.soul.EnumDemonWillType.CORROSIVE
import WayofTime.bloodmagic.soul.PlayerDemonWillHandler.{consumeDemonWill ⇒ consume, getTotalDemonWill ⇒ getWill}
import io.github.alex_hawks.SanguineExtras.common.Helpers
import io.github.alex_hawks.SanguineExtras.common.constructs.{Chest, ItemBlockChest}
import io.github.alex_hawks.SanguineExtras.common.enchantment.Cutting
import javax.annotation.ParametersAreNonnullByDefault
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

import scala.annotation.meta.setter
import scala.collection.JavaConverters._

@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = Helpers.MOD_ID)
object SEEventHandler {
  var hand: EnumHand = null
  
  private val cuttingFluidLPMap = RitualCrushing.cuttingFluidLPMap.asScala
  private val cuttingFluidWillMap = RitualCrushing.cuttingFluidWillMap.asScala
  
  @(CapabilityInject@setter)(classOf[IRitualStone.Tile])
  var CapRuneType: Capability[IRitualStone.Tile] = null
  
  /**
    * Handles the [[Cutting]] Enchantment. This has all of the logic, Cutting is just a marker like Fortune and Silk Touch are
    */
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def harvest(e: HarvestDropsEvent): Unit = hand match {
    case null ⇒ // usingSigilInOffhand is only set if one of my sigils is being used for harvesting, or this part has already been reached once and is being called again after usingSigilInOffhand has been set to MAIN_HAND
      hand = EnumHand.MAIN_HAND
      harvest(e)
      hand = null
    case _ ⇒
      if (e.getHarvester == null) // if harvester is null, then there is nothing for the enchantment to be on
        return
      val stack = e.getHarvester.getHeldItem(hand)
      if (EnchantmentHelper.getEnchantmentLevel(Cutting, stack) > 0) {
        val bind = BloodUtils.getOrBind(stack, e.getHarvester)
        val block = e.getState.getBlock
        val will = getWill(CORROSIVE, e.getHarvester)
        
        for ((cutter, cost) ← cuttingFluidWillMap) {
          if (will >= cost) {
            val ls = new util.ArrayList[ItemStack].asScala
            val vec = PlayerUtils.getRayTraceVectors(e.getHarvester)
            // Doing it this way so that stuff like MCMP works
            ls += block.getPickBlock(e.getState, block.collisionRayTrace(e.getState, e.getWorld, e.getPos, vec.getLeft, vec.getRight), e.getWorld, e.getPos, e.getHarvester)
            ls += cutter
            
            val recipe = BloodMagicAPI.INSTANCE.getRecipeRegistrar.getAlchemyTable(ls.asJava)
            
            if (recipe != null) {
              val output = recipe.getOutput
              val evt = new AlchemyTableCraftEvent(output, ls.toArray)
              if (!output.isEmpty && !MinecraftForge.EVENT_BUS.post(evt)) {
                val uuid = if (bind == null) null else bind.getOwnerId
                if (consume(CORROSIVE, e.getHarvester, cost) >= cost && BloodUtils.drainSoulNetworkWithDamage(uuid, cuttingFluidLPMap(cutter), e.getHarvester)) {
                  e.setDropChance(1)
                  e.getDrops.clear()
                  e.getDrops.add(evt.getOutput)
                  return
                }
              }
            }
          }
        }
      }
  }
  
  @SubscribeEvent
  def onAltarCraft(e: AltarCraftEvent): Unit = {
    val inputStack = e.getInputs()(0)
    inputStack.getItem match {
      case ItemBlockChest =>
        val outputStack = e.getOutput
        val inputTag = inputStack.getOrCreateSubCompound("BlockEntityTag")
        val outputTag = outputStack.getOrCreateSubCompound("BlockEntityTag")
        
        if (!inputTag.hasKey("inventory"))
          return
        
        outputTag.setTag("inventory", inputTag.getTag("inventory"))
        outputTag.getCompoundTag("inventory").setInteger("tier", Chest.getActInvSize(Chest.getTier(outputStack)))
    }
  }
}
