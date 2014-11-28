package io.github.alex_hawks.SanguineExtras.common;

import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemStabilisedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemBuilding;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemDestruction;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemInterdiction;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemRebuilding;
import net.minecraft.item.Item;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public final class ModItems
{
    public static ItemBuilding SigilBuilding;
    public static ItemDestruction SigilDestruction;
    public static ItemInterdiction SigilInterdiction;
    public static ItemMobNet SigilMobNet;
    public static ItemRebuilding SigilRebuild;
    public static Item MicroRitualStone;
    public static Item MicroStone;
    public static Item StableRitualStone;
    
    public static void initItems()
    {
        SigilBuilding = new ItemBuilding();
        GameRegistry.registerItem(SigilBuilding, "SigilBuilding");
        
        SigilDestruction = new ItemDestruction();
        GameRegistry.registerItem(SigilDestruction, "SigilDestruction");
        
        SigilInterdiction = new ItemInterdiction();
        GameRegistry.registerItem(SigilInterdiction, "SigilInterdiction");

        SigilMobNet = new ItemMobNet();
        GameRegistry.registerItem(SigilMobNet, "SigilMobNet");
        
        SigilRebuild = new ItemRebuilding();
        GameRegistry.registerItem(SigilRebuild, "SigilRebuilding");
        
        if (Loader.isModLoaded("ForgeMultipart"))
        {
            MicroRitualStone = new ItemMicroRitualStone();
            GameRegistry.registerItem(MicroRitualStone, "MicroRitualStone");
            
            StableRitualStone = new ItemStabilisedRitualStone();
            GameRegistry.registerItem(StableRitualStone, "StableRitualStone");
            
            MicroStone = new ItemMicroStone();
            GameRegistry.registerItem(MicroStone, "MicroStone");
        }
        
        System.out.println("Initializing Items");
    }
}
