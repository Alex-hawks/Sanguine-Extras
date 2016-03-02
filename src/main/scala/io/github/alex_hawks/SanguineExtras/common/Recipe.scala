package io.github.alex_hawks.SanguineExtras.common

import WayofTime.bloodmagic.api.altar.EnumAltarTier._
import WayofTime.bloodmagic.api.recipe.ShapedBloodOrbRecipe
import WayofTime.bloodmagic.api.registry.AltarRecipeRegistry.AltarRecipe
import WayofTime.bloodmagic.api.registry.{AltarRecipeRegistry, OrbRegistry}
import WayofTime.bloodmagic.registry.ModBlocks._
import WayofTime.bloodmagic.registry.ModItems._
import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.GameRegistry

object Recipe {
  def register {
    registerAltar
    registerCrafting
    registerAlchemy
  }

  def registerCrafting {
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 0), "!@#", "$$$", "$%$", c("!"), Items.wooden_shovel, c("@"), Items.wooden_pickaxe, c("#"), Items.wooden_axe, c("$"), Slate(0), c("%"), OrbRegistry.getOrbStack(orbWeak)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1), "!@#", "$^$", "$%$", c("!"), Items.stone_shovel, c("@"), Items.stone_pickaxe, c("#"), Items.stone_axe, c("$"), Slate(1), c("%"), OrbRegistry.getOrbStack(orbApprentice), c("^"), s(ModItems.SigilDestruction, 0)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 2), "!@#", "$^$", "$%$", c("!"), Items.iron_shovel, c("@"), Items.iron_shovel, c("#"), Items.iron_axe, c("$"), Slate(2), c("%"), OrbRegistry.getOrbStack(orbMagician), c("^"), s(ModItems.SigilDestruction, 1)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 3), "!@#", "$^$", "$%$", c("!"), Items.diamond_shovel, c("@"), Items.diamond_pickaxe, c("#"), Items.diamond_axe, c("$"), Slate(3), c("%"), OrbRegistry.getOrbStack(orbMaster), c("^"), s(ModItems.SigilDestruction, 2)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 4), "!@#", "$^$", "$%$", c("!"), boundShovel, c("@"), boundPickaxe, c("#"), boundAxe, c("$"), s(bloodShard, 1), c("%"), OrbRegistry.getOrbStack(orbArchmage), c("^"), s(ModItems.SigilDestruction, 3)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 5), "!@#", "$^$", "$%$", c("!"), boundShovel, c("@"), boundPickaxe, c("#"), boundAxe, c("$"), s(bloodShard, 1), c("%"), OrbRegistry.getOrbStack(orbTranscendent), c("^"), s(ModItems.SigilDestruction, 4)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilMobNet), "!!!", "!@!", "!#!", c("!"), Slate(1), c("@"), Items.ender_pearl, c("#"), OrbRegistry.getOrbStack(orbMagician)))

    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedRitualStone, 4), "!@!", "@#@", "!@!", c("!"), Slate(1), c("@"), ritualStone, c("#"), OrbRegistry.getOrbStack(orbMaster)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedMRS), "!@!", "@#@", "!@!", c("!"), Slate(3), c("@"), OrbRegistry.getOrbStack(orbMaster), c("#"), ritualController))

    //    if (Loader.isModLoaded("ForgeMultipart")) {
    //      GameRegistry.addRecipe(new ShapelessOreRecipe(s(ModItems.MicroStone, qty = 8), s(ModItems.StableRitualStone), s(GameData.getItemRegistry().getObject("ForgeMicroblock:sawDiamond"))))
    //    }

    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 0), "!!!", "!@!", "!!!", c("!"), Slate(0), c("@"), OrbRegistry.getOrbStack(orbApprentice)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1), "!!!", "!@!", "!!!", c("!"), Slate(1), c("@"), OrbRegistry.getOrbStack(orbApprentice)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 2), "!!!", "!@!", "!!!", c("!"), Slate(2), c("@"), OrbRegistry.getOrbStack(orbApprentice)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 3), "!!!", "!@!", "!!!", c("!"), Slate(3), c("@"), OrbRegistry.getOrbStack(orbApprentice)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 4), "!!!", "!@!", "!!!", c("!"), Slate(4), c("@"), OrbRegistry.getOrbStack(orbApprentice)))
  }

  def registerAltar {
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModBlocks.AdvancedMRS), s(ritualController), FOUR, 30000, 40, 10, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModItems.SigilInterdiction), s(sigilAir), FOUR, 30000, 40, 10, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModItems.SigilBuilding), s(sigilPhantomBridge), THREE, 5000, 30, 10, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModItems.SigilRebuild), s(ModItems.SigilBuilding), THREE, 15000, 30, 10, false))

    //    if (Loader.isModLoaded("ForgeMultipart")) {
    //      AltarRecipeRegistry.registerRecipe(s(ModItems.MicroRitualStone), s(ModItems.MicroStone), 4, 5000, 100, 40, false)
    //    }

    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModBlocks.Chest, 1), s(ModBlocks.Chest, 0), TWO, 16000, 40, 40, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModBlocks.Chest, 2), s(ModBlocks.Chest, 1), THREE, 40000, 120, 80, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModBlocks.Chest, 3), s(ModBlocks.Chest, 2), FOUR, 120000, 160, 160, false))
    AltarRecipeRegistry.registerRecipe(new AltarRecipe(s(ModBlocks.Chest, 4), s(ModBlocks.Chest, 3), FIVE, 240000, 320, 800, false))
  }

  def registerAlchemy {
    if (Loader.isModLoaded("ForgeMultipart")) {
      //      AlchemyRecipeRegistry.registerRecipe(s(ModItems.StableRitualStone), 50, Array[ItemStack](s(ritualStone), s(incendium), s(aquasalus), s(terrae), s(aether)), 4)
    }
  }

  def s(f: AnyRef, meta: Int = 0): ItemStack = f match {
    case x: Item => new ItemStack(x, 1, meta)
    case x: Block => new ItemStack(x, 1, meta)
  }

  def c(c: Char): Character = new Character(c)

  def c(c: String): Character = if (c.length < 1) throw new IllegalArgumentException("input too short") else if (c.length > 1) throw new IllegalArgumentException("input too long") else c(0)

  def Slate(level: Int): ItemStack = new ItemStack(slate, 1, level)
}