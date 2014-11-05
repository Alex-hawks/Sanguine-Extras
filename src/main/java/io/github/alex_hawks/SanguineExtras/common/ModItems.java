package io.github.alex_hawks.SanguineExtras.common;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemDestruction;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemInterdiction;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemRebuilding;

public final class ModItems
{
    public static ItemDestruction SigilDestruction;
    public static ItemRebuilding SigilRebuild;
    public static ItemMobNet SigilMobNet;
    public static ItemInterdiction SigilInterdiction;
    
    public static void initItems()
    {
        SigilDestruction = new ItemDestruction();
        GameRegistry.registerItem(SigilDestruction, "SigilDestruction");
        
        SigilRebuild = new ItemRebuilding();
        GameRegistry.registerItem(SigilRebuild, "SigilRebuilding");

        SigilMobNet = new ItemMobNet();
        GameRegistry.registerItem(SigilMobNet, "SigilMobNet");
        
        SigilInterdiction = new ItemInterdiction();
        GameRegistry.registerItem(SigilInterdiction, "SigilInterdiction");
        
        System.out.println("Initializing Items");
    }
}
