package io.github.alex_hawks.util.minecraft.common

import javax.annotation.Nullable

import com.google.gson.{JsonArray, JsonObject, JsonSyntaxException}
import lombok.NonNull
import net.minecraft.item.{ItemStack => IS}
import net.minecraft.item.crafting.Ingredient
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraftforge.common.crafting.{CraftingHelper, IIngredientFactory, JsonContext}
import Implicit.Helper.fromJson

import scala.collection.JavaConversions._

class IngredientPartNBT(@NonNull val ing: Ingredient, val filterNBT: NBTTagCompound) extends Ingredient {
  def this(@NonNull stack: IS) = this(Ingredient.fromStacks(Array(stack):_*), stack.getTagCompound)

  override def apply(@Nullable input: IS): Boolean = {
    if (input == null || input.isEmpty)
      return false

    if (ing.apply(input)) {
      val toCheck = input.getTagCompound

      return isFilterValid(toCheck)
    }
    false
  }

  override def getMatchingStacks: Array[IS] = super.getMatchingStacks.map(stack => stack.copy.getTagCompound match {
    case null =>
      stack.setTagCompound(filterNBT)
      stack
    case x: NBTTagCompound =>
      x.merge(filterNBT)
      stack
  })

  override def isSimple = false

  private def isFilterValid(toCheck: NBTTagCompound, filter: NBTTagCompound = filterNBT): Boolean = {
    if (filter == null || filter.hasNoTags || filter == toCheck)
      return true
    if (toCheck == null)
      return false

    var valid = true

    for (key <- filter.getKeySet) {
      if (!valid)
        return false

      val filterTag = filter.getTag(key)

      if (filterTag.isInstanceOf[NBTTagCompound]) {
        val tag = filterTag.asInstanceOf[NBTTagCompound]
        valid &= isBaseFilterValid(toCheck.getTag(key), tag)
      }
      else
        valid &= filterTag == toCheck.getTag(key)
    }
    valid
  }

  private def isBaseFilterValid(toCheck: NBTBase, filter: NBTTagCompound) = toCheck.isInstanceOf[NBTTagCompound] && isFilterValid(toCheck.asInstanceOf[NBTTagCompound], filter)
}

class FactoryPartNBT extends IIngredientFactory {
  override def parse(context: JsonContext, json: JsonObject): Ingredient = {
    var sub = CraftingHelper.getIngredient(json.get("ingredient"), context)
    val nbt = json.get("nbt")
    val tag = new NBTTagCompound

    nbt match {
      case x: JsonArray =>
        for (elem <- x) elem match {
          case a: JsonObject =>
            tag.merge(a)
        }
      case x: JsonObject =>
        tag.merge(x)
      case x =>
        throw new JsonSyntaxException(s"Expected a JsonArray or a JsonObject, found a ${x.getClass}")
    }
    new IngredientPartNBT(sub, tag)
  }
}
