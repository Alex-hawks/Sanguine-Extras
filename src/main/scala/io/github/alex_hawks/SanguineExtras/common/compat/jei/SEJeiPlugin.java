package io.github.alex_hawks.SanguineExtras.common.compat.jei;

import io.github.alex_hawks.SanguineExtras.common.Blocks;
import io.github.alex_hawks.SanguineExtras.common.Items;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class SEJeiPlugin implements IModPlugin
{
    @Override
    public void registerItemSubtypes(ISubtypeRegistry sr) {
        sr.registerSubtypeInterpreter(Blocks.chest().getItem(),         SESubtypeInterpreter$.MODULE$);
        sr.registerSubtypeInterpreter(Items.sigil_destruction(),        SESubtypeInterpreter$.MODULE$);
        sr.registerSubtypeInterpreter(Items.bauble_liquid_summoner(),   SESubtypeInterpreter$.MODULE$);
        // TODO add other NBT sensitive items here as well as in SESubtypeInterpreter
    }

    @Override
    public void register(IModRegistry registry)
    {
//        registry.
    }
}
