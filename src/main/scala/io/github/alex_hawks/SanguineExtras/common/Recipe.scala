package io.github.alex_hawks.SanguineExtras.common

import WayofTime.bloodmagic.api.IBloodMagicRecipeRegistrar
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks._
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems._
import WayofTime.bloodmagic.core.recipe.IngredientBloodOrb
import WayofTime.bloodmagic.orb.IBloodOrb
import io.github.alex_hawks.SanguineExtras.common.{Blocks ⇒ ModBlocks, Items ⇒ ModItems}
import io.github.alex_hawks.util.minecraft.common.Implicit.{block, item}
import io.github.alex_hawks.util.minecraft.common.{IngredientPartNBT ⇒ INBT}
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

import scala.language.implicitConversions

object Recipe {
  def register(implicit registrar: IBloodMagicRecipeRegistrar): Unit = {
    registerSmelting
    registerAltar
    registerAlchemyTable
    registerTartaricForge
    registerAlchemyArray
  }

  implicit def isToIngredient(is: ItemStack): Ingredient = is match {
    case _ if is.getItem.isInstanceOf[IBloodOrb] && is.hasTagCompound => new INBT(new IngredientBloodOrb(is.getItem.asInstanceOf[IBloodOrb].getOrb(is)), is.getTagCompound)
    case _ if is.getItem.isInstanceOf[IBloodOrb] => new IngredientBloodOrb(is.getItem.asInstanceOf[IBloodOrb].getOrb(is))
    case _ if is.hasTagCompound => new INBT(is)
    case _ => Ingredient.fromStacks(Array(is):_*)
  }

  def registerCrafting(): Unit = {
////    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 0), "!@#", "$$$", "$%$", c("!"), MCItems.WOODEN_SHOVEL, c("@"), MCItems.WOODEN_PICKAXE, c("#"), MCItems.WOODEN_AXE, c("$"), Slate(0), c("%"), OrbRegistry.getOrbStack(ORB_WEAK)))
//    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 1), "!@#", "$^$", "$%$", c("!"), MCItems.STONE_SHOVEL, c("@"), MCItems.STONE_PICKAXE, c("#"), MCItems.STONE_AXE, c("$"), Slate(1), c("%"), OrbRegistry.getOrbStack(ORB_APPRENTICE), c("^"), s(RegistrarBloodMagicItems.SigilDestruction, 0)))
//    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 2), "!@#", "$^$", "$%$", c("!"), MCItems.IRON_SHOVEL, c("@"), MCItems.IRON_PICKAXE, c("#"), MCItems.IRON_AXE, c("$"), Slate(2), c("%"), OrbRegistry.getOrbStack(ORB_MAGICIAN), c("^"), s(ModItems.SigilDestruction, 1)))
//    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 3), "!@#", "$^$", "$%$", c("!"), SENTIENT_SHOVEL, c("@"), SENTIENT_PICKAXE, c("#"), SENTIENT_AXE, c("$"), Slate(3), c("%"), OrbRegistry.getOrbStack(ORB_MASTER), c("^"), s(ModItems.SigilDestruction, 2)))
//    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 4), "!@#", "$^$", "$%$", c("!"), MCItems.DIAMOND_SHOVEL, c("@"), MCItems.DIAMOND_PICKAXE, c("#"), MCItems.DIAMOND_AXE, c("$"), Slate(4), c("%"), OrbRegistry.getOrbStack(ORB_ARCHMAGE), c("^"), s(ModItems.SigilDestruction, 3)))
//    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_destruction, 5), "!@#", "$^$", "$%$", c("!"), BOUND_SHOVEL, c("@"), BOUND_PICKAXE, c("#"), BOUND_AXE, c("$"), Slate(5), c("%"), OrbRegistry.getOrbStack(ORB_TRANSCENDENT), c("^"), s(ModItems.SigilDestruction, 4)))
////    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.sigil_mob_net), "!!!", "!@!", "!#!", c("!"), Slate(1), c("@"), MCItems.ENDER_PEARL, c("#"), OrbRegistry.getOrbStack(ORB_MAGICIAN)))
//
////    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedRitualStone, 4), "!@!", "@#@", "!@!", c("!"), Slate(1), c("@"), RITUAL_STONE, c("#"), OrbRegistry.getOrbStack(ORB_MASTER)))
////    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedMRS), "!@!", "@#@", "!@!", c("!"), Slate(3), c("@"), OrbRegistry.getOrbStack(ORB_MASTER), c("#"), RITUAL_CONTROLLER))
  }

  def registerSmelting(): Unit = {
    // no-op
  }

  def registerAltar(implicit registrar: IBloodMagicRecipeRegistrar): Unit = {
    import registrar._
    // check out io.github.alex_hawks.util.minecraft.common.Implicit for why these work
    addBloodAltar(ModBlocks.chest.withNBT("BlockEntityTag::tier", 0), ModBlocks.chest.withNBT("BlockEntityTag::tier", 1), 1, 16000,   40,   40)
    addBloodAltar(ModBlocks.chest.withNBT("BlockEntityTag::tier", 1), ModBlocks.chest.withNBT("BlockEntityTag::tier", 2), 2, 40000,   80,   80)
    addBloodAltar(ModBlocks.chest.withNBT("BlockEntityTag::tier", 2), ModBlocks.chest.withNBT("BlockEntityTag::tier", 3), 3, 120000,  160,  160)
    addBloodAltar(ModBlocks.chest.withNBT("BlockEntityTag::tier", 3), ModBlocks.chest.withNBT("BlockEntityTag::tier", 4), 4, 240000,  320,  320)

    addBloodAltar(SIGIL_PHANTOM_BRIDGE(),                             ModItems.sigil_building(),                          2, 5000,    40,   10)
    addBloodAltar(ModItems.sigil_building(),                          ModItems.sigil_rebuild(),                           3, 15000,   40,   10)
    addBloodAltar(SIGIL_WHIRLWIND(),                                  ModItems.sigil_interdiction(),                      3, 30000,   40,   10)

    addBloodAltar(RITUAL_CONTROLLER(),                                ModBlocks.advanced_mrs(),                           3, 30000,   40,   10)
  }

  def registerAlchemyTable(implicit registrar: IBloodMagicRecipeRegistrar): Unit = {
    // no-op
  }

  def registerTartaricForge(implicit registrar: IBloodMagicRecipeRegistrar): Unit = {
    // no-op
  }

  def registerAlchemyArray(implicit registrar: IBloodMagicRecipeRegistrar): Unit = {
    // no-op
  }
}