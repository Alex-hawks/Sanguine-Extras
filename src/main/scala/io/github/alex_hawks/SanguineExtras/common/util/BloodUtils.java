package io.github.alex_hawks.SanguineExtras.common.util;

import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import net.minecraft.entity.player.EntityPlayer;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import static WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler.hurtPlayer;;

public class BloodUtils
{
    public static boolean drainSoulNetwork(String player, int amount)
    {
        return SoulNetworkHandler.syphonFromNetwork(player, amount) >= amount;
    }
    
    public static boolean drainSoulNetworkWithNausea(String player, int amount)
    {
        boolean b = SoulNetworkHandler.syphonFromNetwork(player, amount) >= amount;
        
        if (!b)
            SoulNetworkHandler.causeNauseaToPlayer(player);
        
        return b;
    }

    public static boolean drainSoulNetworkWithDamage(String ownerName, EntityPlayer player, int damageToBeDone)
    {
        if (player.worldObj.isRemote)
        {
            return false;
        }

        int amount = SoulNetworkHandler.syphonFromNetwork(ownerName, damageToBeDone);

        hurtPlayer(player, damageToBeDone - amount);

        return true;
    }

    /**
     * @return the highest tier of orb available, -1 if there is no orb at all
     */
    public static int getHighestTierOrb()
    {
        int i = -1;

        for (AltarRecipe recipe: AltarRecipeRegistry.altarRecipes)
        {
            if (recipe.canBeFilled) // is an orb
            {
                IBloodOrb orb = (IBloodOrb) recipe.requiredItem.getItem();
                i = Math.max(i, orb.getOrbLevel());
            }
        }
        return i;
    }

    public static RitualEffect getEffectFromString(String name)
    {
        Rituals ritual = Rituals.ritualMap.get(name);

        if (ritual == null)
            return null;

        return ritual.effect;
    }
}
