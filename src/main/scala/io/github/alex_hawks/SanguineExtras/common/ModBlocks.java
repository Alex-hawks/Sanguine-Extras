package io.github.alex_hawks.SanguineExtras.common;

import io.github.alex_hawks.SanguineExtras.common.constructs.BlockChest;
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.BlockWardedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.ItemBlockWardedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.warded.TEWardedRitualStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.BlockAdvancedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.advanced.TEAdvancedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.BlockWardedMasterStone;
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.master.warded.TEWardedMasterStone;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockAdvancedMasterStone AdvancedMRS;
    public static BlockWardedMasterStone WardedMRS;
    public static BlockWardedRitualStone WardedRitualStone;
    public static BlockChest Chest;

    public static void initBlocks()
    {
        AdvancedMRS = new BlockAdvancedMasterStone();
        GameRegistry.registerBlock(AdvancedMRS, "AdvancedMasterStone");
        GameRegistry.registerTileEntity(TEAdvancedMasterStone.class, "AdvancedMasterStone");


        WardedMRS = new BlockWardedMasterStone();
        GameRegistry.registerBlock(WardedMRS, "WardedMasterStone");
        GameRegistry.registerTileEntity(TEWardedMasterStone.class, "WardedMasterStone");


        WardedRitualStone = new BlockWardedRitualStone();
        GameRegistry.registerBlock(WardedRitualStone, ItemBlockWardedRitualStone.class, "WardedRitualStone");
        GameRegistry.registerTileEntity(TEWardedRitualStone.class, "WardedRitualStone");


        Chest = new BlockChest();
        GameRegistry.registerBlock(Chest, "SanguineChest");
        GameRegistry.registerTileEntity(TileChest.class, "SanguineChest");


        System.out.println("Initializing Blocks");
    }
}
