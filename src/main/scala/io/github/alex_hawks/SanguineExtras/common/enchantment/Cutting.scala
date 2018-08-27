package io.github.alex_hawks.SanguineExtras.common.enchantment

import io.github.alex_hawks.SanguineExtras.common.Constants
import net.minecraft.enchantment.{Enchantment, EnchantmentLootBonus, EnchantmentUntouching}
import net.minecraft.enchantment.Enchantment.Rarity
import net.minecraft.inventory.EntityEquipmentSlot

/**
  * This [[Enchantment]] is just a marker to make the Cutting Handler work
  */
object Cutting extends Enchantment(Rarity.RARE, TOOL_ENCH_TYPE, Array(EntityEquipmentSlot.MAINHAND)) {
  this.setName(s"${Constants.Metadata.MOD_ID}:cutting")
  this.setRegistryName(this.getName)

  override def canApplyTogether(ench: Enchantment): Boolean = ench match {
    case _: EnchantmentLootBonus => false   //  Fortune and Looting
    case _: EnchantmentUntouching => false  //  Silk Touch
    case _ => true
  }
  
  override def getMinEnchantability(enchantmentLevel: Int): Int = 50
  
  override def getMaxEnchantability(enchantmentLevel: Int): Int = super.getMinEnchantability(enchantmentLevel) + 50
}
