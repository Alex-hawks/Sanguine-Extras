package io.github.alex_hawks.SanguineExtras.common;

import cpw.mods.fml.common.registry.GameRegistry;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemDestruction;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemRebuilding;

public final class ModItems
{
    public static ItemDestruction SigilDestruction;
    public static ItemRebuilding SigilRebuild;
    public static ItemMobNet SigilMobNet;
    
    public static void initItems()
    {
        SigilDestruction = new ItemDestruction();
        GameRegistry.registerItem(SigilDestruction, "SigilDestruction");
        
        SigilRebuild = new ItemRebuilding();
        GameRegistry.registerItem(SigilRebuild, "SigilRebuilding");

        SigilMobNet = new ItemMobNet();
        GameRegistry.registerItem(SigilMobNet, "SigilMobNet");
        
        System.out.println("Initializing Items");
    }
}
