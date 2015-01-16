package io.github.alex_hawks.SanguineExtras.common;

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
    
    @SuppressWarnings("unchecked")
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
            try 
            {
                Class<? extends Item> clazz;
                clazz = (Class<? extends Item>) Class.forName("io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroRitualStone");
                MicroRitualStone = (Item) clazz.newInstance();
                GameRegistry.registerItem(MicroRitualStone, "MicroRitualStone");
                
                
                clazz = (Class<? extends Item>) Class.forName("io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemStabilisedRitualStone");
                StableRitualStone = (Item) clazz.newInstance();
                GameRegistry.registerItem(StableRitualStone, "StableRitualStone");
                
                
                clazz = (Class<? extends Item>) Class.forName("io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroStone");
                MicroStone = (Item) clazz.newInstance();
                GameRegistry.registerItem(MicroStone, "MicroStone");
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            finally 
            {
                
            }
        }
        
        System.out.println("Initializing Items");
    }
}
