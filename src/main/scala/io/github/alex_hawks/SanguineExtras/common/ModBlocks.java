package io.github.alex_hawks.SanguineExtras.common;

import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.BlockWardedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.TEWardedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.BlockAdvancedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.TEAdvancedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.BlockWardedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.TEWardedMasterStone;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockAdvancedMasterStone AdvancedMRS;
    public static BlockWardedMasterStone WardedMRS;
    public static BlockWardedRitualStone WardedRitualStone;

    public static void initBlocks()
    {
        AdvancedMRS = new BlockAdvancedMasterStone();
        GameRegistry.registerBlock(AdvancedMRS, "AdvancedMasterStone");
        GameRegistry.registerTileEntity(TEAdvancedMasterStone.class, "AdvancedMasterStone");
        

        WardedMRS = new BlockWardedMasterStone();
        GameRegistry.registerBlock(WardedMRS, "WardedMasterStone");
        GameRegistry.registerTileEntity(TEWardedMasterStone.class, "WardedMasterStone");
        

        WardedRitualStone = new BlockWardedRitualStone();
        GameRegistry.registerBlock(WardedRitualStone, "WardedRitualStone");
        GameRegistry.registerTileEntity(TEWardedRitualStone.class, "WardedRitualStone");
        
        System.out.println("Initializing Blocks");
    }
}
