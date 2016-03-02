package io.github.alex_hawks.SanguineExtras.common;

import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroStone;
import io.github.alex_hawks.SanguineExtras.common.sigils.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModItems
{
    public static ItemBuilding SigilBuilding;
    public static ItemDestruction SigilDestruction;
    public static ItemInterdiction SigilInterdiction;
    public static ItemMobNet SigilMobNet;
    public static ItemRebuilding SigilRebuild;
    public static Item MicroRitualStone;

    @SuppressWarnings("unchecked")
    public static void initItems()
    {
        SigilBuilding = new ItemBuilding();
        GameRegistry.registerItem(SigilBuilding, "sigilBuilding");

        SigilDestruction = new ItemDestruction();
        GameRegistry.registerItem(SigilDestruction, "sigilDestruction");

        SigilInterdiction = new ItemInterdiction();
        GameRegistry.registerItem(SigilInterdiction, "sigilInterdiction");

        SigilMobNet = new ItemMobNet();
        GameRegistry.registerItem(SigilMobNet, "sigilMobNet");

        SigilRebuild = new ItemRebuilding();
        GameRegistry.registerItem(SigilRebuild, "sigilRebuilding");

        if (Loader.isModLoaded("mcmultipart"))
        {
            MicroRitualStone = new ItemMicroStone();
            GameRegistry.registerItem(MicroRitualStone, "microRitualStone");
        }

        System.out.println("Initializing Items");
    }
}
