package io.github.alex_hawks.SanguineExtras.common;

import net.minecraftforge.fml.common.Loader;

public class Constants
{
    public static final class MetaData
    {
        public static final String MOD_ID = "SanguineExtras";
        public static final String NAME = "Sanguine Utilities";

        public static final String DESCRIPTION = "Blood Pact: How far are you willing to go for convenience?";

        public static final String BAUBLES_ID = "Baubles";
        public static final String MCMP_ID = "mcmultipart";
        public static final String BOTANIA_ID = "Botania";
        public static final String FORESTRY_ID = "forestry";
    }

    public static final class HardLimits
    {
        public static final int BUILDERS_SIGIL_COUNT = 9;
    }

    public static final class Loaded
    {
        public static final boolean BAUBLES = Loader.isModLoaded(MetaData.BAUBLES_ID);
        public static final boolean BOTANIA = Loader.isModLoaded(MetaData.BOTANIA_ID);
        public static final boolean FORESTRY = Loader.isModLoaded(MetaData.FORESTRY_ID);
        public static final boolean MCMP = Loader.isModLoaded(MetaData.MCMP_ID);
    }
}
