package io.github.alex_hawks.SanguineExtras.common

import WayofTime.alchemicalWizardry.ModBlocks._
import WayofTime.alchemicalWizardry.ModItems._
import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipeRegistry
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry
import WayofTime.alchemicalWizardry.api.items.ShapedBloodOrbRecipe
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import cpw.mods.fml.common.Loader
import WayofTime.alchemicalWizardry.api.items.ShapelessBloodOrbRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe
import codechicken.microblock.handler.MicroblockMod
import codechicken.microblock.MicroRecipe
import cpw.mods.fml.common.registry.GameData

object Recipe {
  def register {
    registerAltar
    registerCrafting
    registerAlchemy
  }

  def registerCrafting {
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1, 0), "!@#", "$$$", "$%$", c("!"), Items.wooden_shovel, c("@"), Items.wooden_pickaxe, c("#"), Items.wooden_axe, c("$"), blankSlate, c("%"), weakBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1, 1), "!@#", "$^$", "$%$", c("!"), Items.stone_shovel, c("@"), Items.stone_pickaxe, c("#"), Items.stone_axe, c("$"), reinforcedSlate, c("%"), apprenticeBloodOrb, c("^"), s(ModItems.SigilDestruction, 0)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1, 2), "!@#", "$^$", "$%$", c("!"), Items.iron_shovel, c("@"), Items.iron_shovel, c("#"), Items.iron_axe, c("$"), imbuedSlate, c("%"), magicianBloodOrb, c("^"), s(ModItems.SigilDestruction, 1)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1, 3), "!@#", "$^$", "$%$", c("!"), Items.diamond_shovel, c("@"), Items.diamond_pickaxe, c("#"), Items.diamond_axe, c("$"), demonicSlate, c("%"), masterBloodOrb, c("^"), s(ModItems.SigilDestruction, 2)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilDestruction, 1, 4), "!@#", "$^$", "$%$", c("!"), boundShovel, c("@"), boundPickaxe, c("#"), boundAxe, c("$"), demonBloodShard, c("%"), archmageBloodOrb, c("^"), s(ModItems.SigilDestruction, 3)))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModItems.SigilMobNet), "!!!", "!@!", "!#!", c("!"), reinforcedSlate, c("@"), Items.ender_pearl, c("#"), magicianBloodOrb))

    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedRitualStone, 4), "!@!", "@#@", "!@!", c("!"), reinforcedSlate, c("@"), ritualStone, c("#"), masterBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.WardedMRS), "!@!", "@#@", "!@!", c("!"), demonicSlate, c("@"), masterBloodOrb, c("#"), blockMasterStone))
    
    if (Loader.isModLoaded("ForgeMultipart")) {
      GameRegistry.addRecipe(new ShapelessOreRecipe (s(ModItems.MicroStone, qty = 8), s(ModItems.StableRitualStone), s(GameData.getItemRegistry().getObject("ForgeMicroblock:sawDiamond"))))
    }

    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1, 0), "!!!", "!@!", "!!!", c("!"), blankSlate, c("@"), weakBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1, 1), "!!!", "!@!", "!!!", c("!"), reinforcedSlate, c("@"), weakBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1, 2), "!!!", "!@!", "!!!", c("!"), imbuedSlate, c("@"), weakBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1, 3), "!!!", "!@!", "!!!", c("!"), demonicSlate, c("@"), weakBloodOrb))
    GameRegistry.addRecipe(new ShapedBloodOrbRecipe(s(ModBlocks.Chest, 1, 4), "!!!", "!@!", "!!!", c("!"), s(baseItems,1,27), c("@"), weakBloodOrb))
  }

  def registerAltar {
    AltarRecipeRegistry.registerAltarRecipe(s(ModBlocks.AdvancedMRS), s(blockMasterStone), 4, 30000, 40, 10, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModItems.SigilInterdiction), s(sigilOfWind), 4, 30000, 40, 10, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModItems.SigilBuilding), s(sigilOfTheBridge), 3, 5000, 30, 10, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModItems.SigilRebuild), s(ModItems.SigilBuilding), 3, 15000, 30, 10, false)
    
    if (Loader.isModLoaded("ForgeMultipart")) {
      AltarRecipeRegistry.registerAltarRecipe(s(ModItems.MicroRitualStone), s(ModItems.MicroStone), 4, 5000, 100, 40, false)
    }

    AltarRecipeRegistry.registerAltarRecipe(s(ModBlocks.Chest,1,1), s(ModBlocks.Chest,1,0), 2,  16000,  40,  40, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModBlocks.Chest,1,2), s(ModBlocks.Chest,1,1), 2,  40000, 120,  80, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModBlocks.Chest,1,3), s(ModBlocks.Chest,1,2), 2, 120000, 160, 160, false)
    AltarRecipeRegistry.registerAltarRecipe(s(ModBlocks.Chest,1,4), s(ModBlocks.Chest,1,3), 2, 240000, 320, 800, false)
  }

  def registerAlchemy {
    if (Loader.isModLoaded("ForgeMultipart")) {
      AlchemyRecipeRegistry.registerRecipe(s(ModItems.StableRitualStone), 50, Array[ItemStack](s(ritualStone), s(incendium), s(aquasalus), s(terrae), s(aether)), 4)
    }
  }

  def s(f: AnyRef, qty: Int = 1, meta: Int = 0): ItemStack = f match {
    case x: Item => new ItemStack(x, qty, meta)
    case x: Block => new ItemStack(x, qty, meta)
  }

  def c(c: Char): Character = new Character(c)

  def c(c: String): Character = if (c.length < 1) throw new IllegalArgumentException("intput too short") else if (c.length > 1) throw new IllegalArgumentException("intput too long") else c(0)
}