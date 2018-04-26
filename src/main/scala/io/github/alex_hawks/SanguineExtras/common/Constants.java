package io.github.alex_hawks.SanguineExtras.common;

import net.minecraftforge.fml.common.Loader;

public final class Constants
{
    public static final class Metadata
    {
        public static final String MOD_ID = "sanguineextras"; // There is a copy of this in ModStuff.scala
        public static final String NAME = "Sanguine Utilities";

        public static final String BAUBLES_ID = "baubles";
        public static final String MCMP_ID = "mcmultipart";
        public static final String BOTANIA_ID = "botania";
        public static final String FORESTRY_ID = "forestry";
    }

    public static final class HardLimits
    {
        public static final int BUILDERS_SIGIL_COUNT = 9; //TODO make this into a config and a tier system. See: Better Builders Wands
    }

    public static final class Loaded
    {
        public static final boolean BAUBLES = Loader.isModLoaded(Metadata.BAUBLES_ID);
        public static final boolean BOTANIA = Loader.isModLoaded(Metadata.BOTANIA_ID);
        public static final boolean FORESTRY = Loader.isModLoaded(Metadata.FORESTRY_ID);
        public static final boolean MCMP = Loader.isModLoaded(Metadata.MCMP_ID);
    }
}
