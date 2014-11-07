package io.github.alex_hawks.SanguineExtras.common;

import cpw.mods.fml.common.registry.GameRegistry;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.items.ShapedBloodOrbRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import static WayofTime.alchemicalWizardry.ModItems.*;
import static WayofTime.alchemicalWizardry.ModBlocks.*;

public class Recipes
{
    public static void register()
    {
        registerAltar();
        registerCrafting();
    }
    
    public static void registerCrafting()
    {
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilDestruction, 1, 0), new Object[] {"!@#", "$$$", "$%$", '!', Items.wooden_shovel, '@', Items.wooden_pickaxe, '#', Items.wooden_axe, '$', blankSlate, '%', weakBloodOrb}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilDestruction, 1, 1), new Object[] {"!@#", "$^$", "$%$", '!', Items.stone_shovel, '@', Items.stone_pickaxe, '#', Items.stone_axe, '$', reinforcedSlate, '%', apprenticeBloodOrb, '^', new ItemStack(ModItems.SigilDestruction, 0)}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilDestruction, 1, 2), new Object[] {"!@#", "$^$", "$%$", '!', Items.iron_shovel, '@', Items.iron_shovel, '#', Items.iron_axe, '$', imbuedSlate, '%', magicianBloodOrb, '^', new ItemStack(ModItems.SigilDestruction, 1)}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilDestruction, 1, 3), new Object[] {"!@#", "$^$", "$%$", '!', Items.diamond_shovel, '@', Items.diamond_pickaxe, '#', Items.diamond_axe, '$', demonicSlate, '%', masterBloodOrb, '^', new ItemStack(ModItems.SigilDestruction, 2)}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilDestruction, 1, 4), new Object[] {"!@#", "$^$", "$%$", '!', boundShovel, '@', boundPickaxe, '#', boundAxe, '$', demonBloodShard, '%', archmageBloodOrb, '^', new ItemStack(ModItems.SigilDestruction, 3)}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModItems.SigilMobNet), new Object[] {"!!!", "!@!", "!#!", '!', reinforcedSlate, '@', Items.ender_pearl, '#', magicianBloodOrb}));

        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModBlocks.WardedRitualStone, 4), new Object[] {"!@!", "@#@", "!@!", '!', reinforcedSlate, '@', ritualStone, '#', masterBloodOrb}));
        GameRegistry.addRecipe(new ShapedBloodOrbRecipe(new ItemStack(ModBlocks.WardedMRS), new Object[] {"!@!", "@#@", "!@!", '!', demonicSlate, '@', masterBloodOrb, '#', blockMasterStone}));
    }
    
    public static void registerAltar()
    {
        AltarRecipeRegistry.registerAltarRecipe(new ItemStack(ModBlocks.AdvancedMRS), new ItemStack(WayofTime.alchemicalWizardry.ModBlocks.blockMasterStone), 4, 30000, 40, 10, false);
        AltarRecipeRegistry.registerAltarRecipe(new ItemStack(ModItems.SigilInterdiction), new ItemStack(sigilOfWind), 4, 30000, 40, 10, false);
        AltarRecipeRegistry.registerAltarRecipe(new ItemStack(ModItems.SigilRebuild), new ItemStack(sigilOfTheBridge), 3, 10000, 30, 10, false);
    }
}
