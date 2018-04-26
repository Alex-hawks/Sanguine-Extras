package io.github.alex_hawks.SanguineExtras.common.util.config;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.push_handlers.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.*;
import java.util.function.Function;

import static io.github.alex_hawks.SanguineExtras.common.util.config.Base.*;

/**
 * Order:
 * <ol>
 * <li>new (to load all the config files and set up the data structures)</li>
 * <li>handleIMC (for other mods to set their own defaults)</li>
 * <li>handleDefaults (apply all remaining defaults to the data structures)</li>
 * <li>applyConfig (to allow the user/packmaker to override the defaults)</li>
 * </ol>
 */
@ParametersAreNonnullByDefault
public final class Overrides
{
    /**
     *  called in {@link SanguineExtras#preInit(FMLPreInitializationEvent)}
      */
    public static void initOverrides(File configDir)
    {
        new Interdiction(configDir);
        new Capture(configDir);
        new Spawn(configDir);
    }

    /**
     *  called in {@link SanguineExtras#handleIMC(FMLInterModComms.IMCEvent)} )}
     */
    public static void handleIMC(ImmutableList<FMLInterModComms.IMCMessage> msgs)
    {
        if (!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION))
        {   /* Startup */
            for (FMLInterModComms.IMCMessage msg : msgs)
            {
                switch (msg.key)
                {
                    case "interdiction":
                        Interdiction.INSTANCE.handleIMC(msg);
                        break;
                    case "capture":
                        Capture.INSTANCE.handleIMC(msg);
                        break;
                    case "spawn":
                        Spawn.INSTANCE.handleIMC(msg);
                        break;
                    default:
                        break;
                }
            }
        }
//        else { /* Runtime */ } // Nothing needs this yet
    }

    /**
     *  called in {@link SanguineExtras#postInit(FMLPostInitializationEvent)}
     */
    public static void handleDefaults()
    {
        Interdiction.INSTANCE.handleDefaults();
        Capture.INSTANCE.handleDefaults();
        Spawn.INSTANCE.handleDefaults();
    }

    /**
     *  called in {@link SanguineExtras#postInit(FMLPostInitializationEvent)}
     */
    public static void applyConfig()
    {
        Interdiction.INSTANCE.applyConfig();
        Capture.INSTANCE.applyConfig();
        Spawn.INSTANCE.applyConfig();
    }

    public static ResourceLocation getEntityID(Entity ent)
    {
        return EntityList.getKey(ent);
    }

    public static class Interdiction
    {
        public static Interdiction INSTANCE;

        private Interdiction(File configDir)
        {
            if (INSTANCE != null)
                throw new IllegalStateException("A config class (Interdiction) has be instantiated twice");

            INSTANCE = this;
            file = new Configuration(new File(configDir, "Interdiction Overrides.cfg" ));

            IPushCondition tmp = new Default();
            filters.put(tmp.getName().toLowerCase(Locale.ROOT), tmp);

            tmp = new Tamable();
            filters.put(tmp.getName().toLowerCase(Locale.ROOT), tmp);

            tmp = new BotaniaThrowableCopy();
            filters.put(tmp.getName().toLowerCase(Locale.ROOT), tmp);

            tmp = new BotaniaManaBurst();
            filters.put(tmp.getName().toLowerCase(Locale.ROOT), tmp);
        }

        private final Configuration file;
        private final HashMap<ResourceLocation, InterdictionEntry> data = new HashMap<>();
        private final HashBiMap<String, IPushCondition> filters = HashBiMap.create(); // to map filters to an easy to configure name

        void handleIMC(FMLInterModComms.IMCMessage msg)
        {
            if(msg.isFunctionMessage())
            {
                Optional<Function<Object, IPushCondition>> fun = msg.getFunctionValue(null, IPushCondition.class);
                if(fun.isPresent())
                {
                    IPushCondition filter;
                    filter = fun.get().apply(null);
                    if (filters.putIfAbsent(filter.getName().toLowerCase(Locale.ROOT), filter) != null)
                        SanguineExtras.LOG.fatal("Someone ({}) attempted to register an Interdiction handler ({}) when it was already registered. Using old data", msg.getSender(), filter.getName());
                }
            }
        }

        void handleDefaults()
        {
            // no-op
        }

        void applyConfig()
        {
            file.load();

            Set<String> categories = file.getCategoryNames();

            for (String category : categories)
            {
                ConfigCategory cat      = file.getCategory(category);
                Property generated      = cat.get("generated").     setDefaultValue(true);
                Property enabledFilters = cat.get("enabledFilters").setDefaultValues(filters.keySet().toArray(new String[0]));

                generated.      setComment("Set to \"false\" to use these values for the mob instead of the default, which is \"all of them\"");
                enabledFilters. setComment("Enabled filters will be listed here. Remove an entry to do disable that filter for the Entity.");

                if(!generated.getBoolean())
                {
                    InterdictionEntry entry = new InterdictionEntry(false, enabledFilters.getStringList(), category);
                    data.put(new ResourceLocation(category), entry);
                }
            }

            for(Map.Entry<ResourceLocation, InterdictionEntry> entry : data.entrySet())
            {
                if (entry.getValue().generated)
                {
                    Property generated      = file.get(entry.getKey().toString(), "generated", true);
                    Property enabledFilters = file.get(entry.getKey().toString(), "enabledFilters", filters.keySet().toArray(new String[0]));

                    generated.      setComment("Set to \"false\" to use these values for the mob instead of the default, which is \"all of them\"");
                    enabledFilters. setComment("Enabled filters will be listed here. Remove an entry to do disable that filter for the Entity.");

                    generated.      set(entry.getValue().generated);
                    enabledFilters. set(entry.getValue().enabledFilters);
                }
            }

            if(file.hasChanged())
                file.save();

        }

        public Map<ResourceLocation, InterdictionEntry> getData()
        {
            return ImmutableMap.copyOf(data);
        }

        public Map<String, IPushCondition> getFilters()
        {
            return ImmutableMap.copyOf(filters);
        }

        @ToString
        @EqualsAndHashCode
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class InterdictionEntry
        {
                    public final boolean        generated;      // if false, do not override the other values on change to the defaults. never override this
                    public final String[]       enabledFilters; // all the filters that this mob can respond to, defaults to all of them. If empty, the mob cannot be pushed
            @Wither public final String         entityID;       // the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance

            private InterdictionEntry(String[] enabledFilters, String entityID)
            {
                this(true, enabledFilters, entityID);
            }
        }
    }

    public static class Capture
    {
        public static Capture INSTANCE;
        static final CaptureEntry defaultDisabled = new CaptureEntry(false, 0, 100.0, true, "");
        static final CaptureEntry defaultEnabled = new CaptureEntry(true, sigil.holding.cost, sigil.holding.maxHealth, sigil.holding.maxHealthIsPercentage, "");

        private Capture(File configDir)
        {
            if (INSTANCE != null)
                throw new IllegalStateException("A config class (Capture) has be instantiated twice");

            INSTANCE = this;
            file = new Configuration(new File(configDir, "Capture Overrides.cfg" ));
        }

        private final Configuration file;
        private HashMap<ResourceLocation, CaptureEntry> data = new HashMap<>();

        void handleIMC(FMLInterModComms.IMCMessage msg)
        {
            if (msg.isNBTMessage())
            {
                NBTTagCompound tag = msg.getNBTValue();
                boolean     enabled         = tag.getBoolean("enabled");
                int         cost            = tag.getInteger("cost");
                double      maxHealth       = tag.getDouble("maxHealth");
                boolean     isPercentage    = tag.getBoolean("isPercentage");
                String      entityID        = tag.getString("entityID");

                CaptureEntry entry = new CaptureEntry(enabled, cost, maxHealth, isPercentage, entityID);

                if (data.putIfAbsent(new ResourceLocation(entityID), entry) != null)
                    SanguineExtras.LOG.fatal("Someone ({}) attempted to register Capture data for {} when it was already registered. Using old data", msg.getSender(), entityID);
            }
        }

        void handleDefaults()
        {
            data.putIfAbsent(new ResourceLocation("minecraft:witch"), defaultEnabled.withEntityID("minecraft:witch").multiplyCost(10));
            data.putIfAbsent(new ResourceLocation("thaumcraft:golem"), defaultDisabled.withEntityID("thaumcraft:golem"));
            data.putIfAbsent(new ResourceLocation("botania:manaBurst"), defaultDisabled.withEntityID("botania:manaBurst"));

            for (EntityEntry e : ForgeRegistries.ENTITIES)
            {
                if (EntityLivingBase.class.isAssignableFrom(e.getEntityClass()))
                    data.putIfAbsent(e.getRegistryName(), defaultEnabled.withEntityID(e.getRegistryName().toString()));
            }
        }

        void applyConfig()
        {
            file.load();

            Set<String> categories = file.getCategoryNames();

            for (String category : categories)
            {
                ConfigCategory cat      = file.getCategory(category);
                Property generated      = cat.get("generated").     setDefaultValue(true);
                Property enabled        = cat.get("enabled").       setDefaultValue(true);
                Property cost           = cat.get("cost").          setDefaultValue(sigil.holding.cost).setMinValue(0);
                Property maxHealth      = cat.get("maxHealth").     setDefaultValue(sigil.holding.maxHealth).setMinValue(1);
                Property isPercentage   = cat.get("isPercentage").  setDefaultValue(true);

                generated.      setComment("Set to \"false\" to use these values for the mob instead of the defaults as set in \"Base.cfg\"");
                enabled.        setComment("If this is a boss, this will be ignored if \"Capturable Bosses\" is \"false\" in \"Base.cfg\"");
                cost.           setComment("If this is a boss, this number will be multiplied by 10");
                maxHealth.      setComment("See the comments on \"Max Health\" in \"Base.cfg\"");
                isPercentage.   setComment("See the comments on \"Max Health is a Percentage\" in \"Base.cfg\"");

                if(!generated.getBoolean())
                {
                    CaptureEntry entry = new CaptureEntry(false, enabled.getBoolean(), cost.getInt(), maxHealth.getInt(), isPercentage.getBoolean(), category);
                    data.put(new ResourceLocation(category), entry);
                }
            }

            for(Map.Entry<ResourceLocation, CaptureEntry> entry : data.entrySet())
            {
                if (entry.getValue().generated)
                {
                    Property generated      = file.get(entry.getKey().toString(), "generated",      true);
                    Property enabled        = file.get(entry.getKey().toString(), "enabled",        true);
                    Property cost           = file.get(entry.getKey().toString(), "cost",           sigil.holding.cost).setMinValue(0);
                    Property maxHealth      = file.get(entry.getKey().toString(), "maxHealth",      sigil.holding.maxHealth).setMinValue(1);
                    Property isPercentage   = file.get(entry.getKey().toString(), "isPercentage",   true);

                    generated.      setComment("Set to \"false\" to use these values for the mob instead of the defaults as set in \"Base.cfg\"");
                    enabled.        setComment("If this is a boss, this will be ignored if \"Capturable Bosses\" is \"false\" in \"Base.cfg\"");
                    cost.           setComment("If this is a boss, this number will be multiplied by 10");
                    maxHealth.      setComment("See the comments on \"Max Health\" in \"Base.cfg\"");
                    isPercentage.   setComment("See the comments on \"Max Health is a Percentage\" in \"Base.cfg\"");

                    generated.      set(entry.getValue().generated);
                    enabled.        set(entry.getValue().enabled);
                    cost.           set(entry.getValue().cost);
                    maxHealth.      set(entry.getValue().maxHealth);
                    isPercentage.   set(entry.getValue().isPercentage);
                }
            }

            if(file.hasChanged())
                file.save();
        }

        public Map<ResourceLocation, CaptureEntry> getData()
        {
            return ImmutableMap.copyOf(data);
        }

        @ToString
        @EqualsAndHashCode
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class CaptureEntry
        {
                    public final boolean    generated;      // if false, do not override the other values on change to the defaults. never override this
                    public final boolean    enabled;        // if false, this mob cannot be captured
            @Wither public final int        cost;           // see Base.Sigil.Holding.cost
            @Wither public final double     maxHealth;      // see Base.Sigil.Holding.maxHealth
            @Wither public final boolean    isPercentage;   // see Base.Sigil.Holding.maxHealthIsPercentage
            @Wither public final String     entityID;       // the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance

            private CaptureEntry(boolean enabled, int cost, double maxHealth, boolean isPercentage, String entityID)
            {
                this(true, enabled, cost, maxHealth, isPercentage, entityID);
            }

            public CaptureEntry multiplyCost(double mul)
            {
                double multi = cost * mul;
                return this.withCost((int) multi);
            }
        }
    }

    public static class Spawn
    {
        public static Spawn INSTANCE;
        static final SpawnEntry defaultDisabled = new SpawnEntry(false, 0, -1, "");
        static final SpawnEntry defaultEnabled = new SpawnEntry(true, ritual.spawn.lpMultiplier, ritual.spawn.maxEntities, "");

        private Spawn(File configDir)
        {
            if (INSTANCE != null)
                throw new IllegalStateException("A config class (Spawn) has be instantiated twice");

            INSTANCE = this;
            file = new Configuration(new File(configDir, "Spawn Overrides.cfg" ));
        }

        private final Configuration file;
        private HashMap<ResourceLocation, SpawnEntry> data = new HashMap<>();

        void handleIMC(FMLInterModComms.IMCMessage msg)
        {
            if (msg.isNBTMessage())
            {
                NBTTagCompound tag = msg.getNBTValue();
                boolean     enabled         = tag.getBoolean("enabled");
                int         lpMultiplier    = tag.getInteger("lpMultiplier");
                int         maxEntities     = tag.getInteger("maxEntities");
                String      entityID        = tag.getString("entityID");

                SpawnEntry entry = new SpawnEntry(enabled, lpMultiplier, maxEntities, entityID);

                if (data.putIfAbsent(new ResourceLocation(entityID), entry) != null)
                    SanguineExtras.LOG.fatal("Someone ({}) attempted to register Spawn data for {} when it was already registered. Using old data", msg.getSender(), entityID);
            }
        }

        void handleDefaults()
        {
            data.putIfAbsent(new ResourceLocation("minecraft:witch"), defaultEnabled.withEntityID("minecraft:witch").multiplyCost(2.5));
            data.putIfAbsent(new ResourceLocation("thaumcraft:golem"), defaultDisabled.withEntityID("thaumcraft:golem"));
            data.putIfAbsent(new ResourceLocation("botania:manaBurst"), defaultDisabled.withEntityID("botania:manaBurst"));

            for (EntityEntry e : ForgeRegistries.ENTITIES)
            {
                if (EntityLivingBase.class.isAssignableFrom(e.getEntityClass()))
                    data.putIfAbsent(e.getRegistryName(), defaultEnabled.withEntityID(e.getRegistryName().toString()));
            }
        }

        void applyConfig()
        {
            file.load();

            Set<String> categories = file.getCategoryNames();

            for (String category : categories)
            {
                ConfigCategory cat      = file.getCategory(category);
                Property generated      = cat.get("generated").     setDefaultValue(true);
                Property enabled        = cat.get("enabled").       setDefaultValue(true);
                Property lpMultiplier   = cat.get("lpMultiplier").  setDefaultValue(ritual.spawn.lpMultiplier).setMinValue(Base.Helper.getActualMinCost());
                Property maxEntities    = cat.get("maxEntities").   setDefaultValue(ritual.spawn.maxEntities).setMinValue(1).setMaxValue(50);

                generated.      setComment("Set to \"false\" to use these values for the mob instead of the defaults as set in \"Base.cfg\"");
                enabled.        setComment("If this is a boss, this will be ignored if \"Spawnable Bosses\" is \"false\" in \"Base.cfg\"");
                lpMultiplier.   setComment("If this is a boss, this number will be multiplied by 10");
                maxEntities.    setComment("If this is a boss, this number will be divided by 10");

                if(!generated.getBoolean())
                {
                    SpawnEntry entry = new SpawnEntry(false, enabled.getBoolean(), lpMultiplier.getInt(), maxEntities.getInt(), category);
                    data.put(new ResourceLocation(category), entry);
                }
            }

            for(Map.Entry<ResourceLocation, SpawnEntry> entry : data.entrySet())
            {
                if (entry.getValue().generated)
                {
                    Property generated      = file.get(entry.getKey().toString(), "generated",      true);
                    Property enabled        = file.get(entry.getKey().toString(), "enabled",        true);
                    Property lpMultiplier   = file.get(entry.getKey().toString(), "lpMultiplier",   ritual.spawn.lpMultiplier).setMinValue(Base.Helper.getActualMinCost());
                    Property maxEntities    = file.get(entry.getKey().toString(), "maxEntities",    ritual.spawn.lpMultiplier).setMinValue(1).setMaxValue(50);

                    generated.      setComment("Set to \"false\" to use these values for the mob instead of the defaults as set in \"Base.cfg\"");
                    enabled.        setComment("If this is a boss, this will be ignored if \"Spawnable Bosses\" is \"false\" in \"Base.cfg\"");
                    lpMultiplier.   setComment("If this is a boss, this number will be multiplied by 10");
                    maxEntities.    setComment("If this is a boss, this number will be divided by 10");

                    generated.      set(entry.getValue().generated);
                    enabled.        set(entry.getValue().enabled);
                    lpMultiplier.   set(entry.getValue().lpMultiplier);
                    maxEntities.    set(entry.getValue().maxEntities);
                }
            }

            if(file.hasChanged())
                file.save();
        }

        public Map<ResourceLocation, SpawnEntry> getData()
        {
            return ImmutableMap.copyOf(data);
        }

        @ToString
        @EqualsAndHashCode
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class SpawnEntry
        {
                    public final boolean    generated;      // if false, do not override the other values on change to the defaults. never override this
                    public final boolean    enabled;        // if false, this mob cannot be spawned
            @Wither public final int        lpMultiplier;   // see Base.Ritual.Spawn.lpMultiplier
            @Wither public final int        maxEntities;    // see Base.Ritual.Spawn.maxEntities. This is before the /10 is done for bosses
            @Wither public final String     entityID;       // the EntityID in the Forge EntityRegistry for the mob this is for. Should be the same as the key in the map for this instance

            private SpawnEntry(boolean enabled, int lpMultiplier, int maxEntities, String entityID)
            {
                this(true, enabled, lpMultiplier, maxEntities, entityID);
            }

            SpawnEntry multiplyCost(double mul)
            {
                double multi = lpMultiplier * mul;
                return this.withLpMultiplier((int) multi);
            }
        }
    }
}
