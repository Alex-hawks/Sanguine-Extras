package io.github.alex_hawks.SanguineExtras.common.util.config;

import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.altar.ComponentType;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Config;

import static net.minecraftforge.common.config.Config.*;

@Config(modid = "sanguineextras", name = "sanguineextras/Base", category = "")
public class Base
{
    @Comment("Sigil settings")
    public static Sigil sigil = new Sigil();
    @Comment("Ritual settings")
    public static Ritual ritual = new Ritual();

    public static class Sigil
    {
        @Comment("Sigil of Holding settings")
        public Holding holding = new Holding();
        @Comment("Interdiction Sigil settings")
        public Interdiction interdiction = new Interdiction();
        @Comment("Rebuilding Sigil settings")
        public Rebuild rebuild = new Rebuild();
        @Comment("Builder's Sigil settings")
        public Building build = new Building();


        public static class Holding
        {
            @Name("Cost to Capture")
            @Comment("Multiply by 10 if the target you are capturing is a boss")
            public int cost = 1000;

            @Name("Capturable Bosses")
            @Comment({  "Set to \"true\" if you want to be able to capture boss mobs at 10 times the LP cost",
                        "Setting this to \"false\" won't empty any Sigils"})
            public boolean trappableBosses = true;

            @Name("Max Health")
            @RangeDouble(min = 1.0)
            @Comment({  "The maximum health a mob can have and still be captured. This is by default a percentage",
                        "If this is a percentage, values over 100 shall be treated as if they are 100"})
            public double maxHealth = 100.0;

            @Name("Max Health is a Percentage")
            @Comment({  "If this is \"true\" then \"Max Health\" has a minimum of 1, and a maximum of 100",
                        "If this is \"false\" then \"Max Health\" has a minimum of 1, and no maximum"})
            public boolean maxHealthIsPercentage = true;
        }
        public static class Interdiction
        {
            @Name("Cost per 10 seconds")
            @Comment("This is drained every 10 seconds. If it fails, the sigil deactivates itself")
            public int cost = 200;

            @RangeDouble(min = 0.5, max = 10)
            @Comment("Entities will be pushed away from you if they are closer than this many blocks, calculated using Pythagorean theorem")
            public double range = 5.0;
        }

        public static class Rebuild
        {
            @RangeInt(min = 0)
            @Name("Cost Per Block Replaced")
            @Comment("The LP cost of replacing one block using the Sigil Of Rebuilding")
            public int cost = 25;

            @RangeInt(min = 1, max = 50)
            @Name("Iterations")
            @Comment({  "The number of times that the sigil will iterate when used to replace more than one block at a time.",
                        "Functions similarly to range, but isn't quite the same.",
                        "Beware: this can add up quickly." })
            public int iterations = 5;
        }

        public static class Building
        {
            @RangeInt(min = 0)
            @Name("Cost Per Block Pplaced")
            @Comment("The LP cost of placing one block using the Sigil Of Building")
            public int cost = 25;
        }
    }

    public static class Ritual
    {
        @Comment("Set the value to \"true\" to enable the ritual, and \"false\" to disable the ritual")
        public Enabled enabled = new Enabled();
        @Comment("Settings for the Ritual of Re-creation")
        public Spawn spawn = new Spawn();
        @Comment("Settings related to the Ritual Stones, both Marker and Master")
        public Stones stones = new Stones();

        public static class Enabled
        {
            @LangKey("ritual.sanguineextras.spawn")
            public boolean spawn = true;

            @LangKey("ritual.sanguineextras.test")
            @Comment("Defaults to \"false\" when not in a development environment")
            public boolean test = (Boolean) Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", false);

            @LangKey("ritual.sanguineextras.forge")
            public boolean forge = true;

            @LangKey("ritual.sanguineextras.bees")
            @Comment("Ignores this, and disables the Ritual, if Forestry is not loaded.")
            public boolean bees = true;
        }

        public static class Spawn
        {
            @Name("Spawnable Bosses")
            @Comment("Set to \"true\" if you want to be able to spawn boss mobs at 10 times the LP cost")
            public boolean spawnableBosses = true;

            @Name("Cost per Health")
            @RangeInt(min = 194) // Magic number is also present in Overrides.Spawn#applyConfig()
            @Comment({  "This is the multiplier to calculate the cost of spawning mobs. You can try, but it is coded to prevent exploits, and the only way to get more LP out than you put in is to use something that regenerates health",
                        "This is overridden by the values in \"Spawn Overrides.cfg\" on entries with \"generated=false\""})
            public int lpMultiplier = 200;

            @Name("Max Entities")
            @RangeInt(min = 1, max = 50) // Magic numbers are also present in Overrides.Spawn#applyConfig()
            @Comment("The maximum number of entities inside the spawner's area of effect, before it gives up on spawning more. It only counts what it is currently spawning. Divide by 10 if the mob in question is a boss.")
            public int maxEntities = 20;
        }

        public static class Stones
        {
            @Name("Ops can break Warded Blocks")
            @Comment("Set this to true if you want ops to be able to break the warded blocks when most others can't.")
            public boolean opsCanBreakWardedBlocks = false;
        }
    }

    static class Helper
    {
        public static final int runesInMaxTier = (int) getRunesInMaxTier();
        @SuppressWarnings("unused")
        private static long getRunesInMaxTier()
        {
            AltarTier[] tiers = AltarTier.values();
            AltarTier max = tiers[tiers.length - 1];
            return max.getAltarComponents().stream().filter(p -> p.getComponent() != ComponentType.BLOODRUNE).count();
        }

        public static int getActualMinCost()
        {
            return runesInMaxTier + 10;
        }
    }
}
