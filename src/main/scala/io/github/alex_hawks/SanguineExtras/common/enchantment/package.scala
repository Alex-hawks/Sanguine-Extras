package io.github.alex_hawks.SanguineExtras.common

import com.google.common.base.Predicate
import io.github.alex_hawks.SanguineExtras.common.items.sigils.{ItemDestruction, ItemRebuilding}
import net.minecraft.enchantment.EnumEnchantmentType
import net.minecraft.item.{Item, ItemSword, ItemTool}
import net.minecraftforge.common.util.EnumHelper

package object enchantment {
  val TOOL_ENCH_TYPE: EnumEnchantmentType = EnumHelper.addEnchantmentType("SETools", new Predicate[Item] {
    override def apply(item: Item): Boolean = item match {
      case _: ItemTool => true
      case _: ItemSword => true
      case ItemDestruction => true
      case _: ItemRebuilding => true
      case _ => false
    }
  })
}
