package io.github.alex_hawks.SanguineExtras.common.util;


import WayofTime.bloodmagic.api.altar.EnumAltarTier;
import WayofTime.bloodmagic.api.network.SoulNetwork;
import WayofTime.bloodmagic.api.registry.RitualRegistry;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.util.helper.NetworkHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;


public class BloodUtils
{
    public static boolean drainSoulNetwork(UUID player, int amount)
    {
        return NetworkHelper.getSoulNetwork(player.toString()).syphon(amount) >= amount;
    }

    public static boolean drainSoulNetworkWithNausea(UUID player, int amount)
    {
        boolean b = NetworkHelper.getSoulNetwork(player.toString()).syphon(amount) >= amount;

        if (!b)
        {
            EntityPlayer owner = PlayerHelper.getPlayerFromUUID(player);
            if (owner != null)
            {
                owner.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 99));
            }
        }

        return b;
    }

    public static boolean drainSoulNetworkWithDamage(UUID owner, EntityPlayer player, int amount)
    {
        if (player.worldObj.isRemote)
        {
            return false;
        }

        SoulNetwork n = NetworkHelper.getSoulNetwork(owner.toString());

        int remain = n.syphon(amount);

        n.hurtPlayer(player, remain);

        return true;
    }

    /**
     * @return the highest tier of orb available, -1 if there is no orb at all
     */
    public static int getHighestTierOrb()
    {
        return EnumAltarTier.MAXTIERS;
    }

    public static Ritual getEffectFromString(String name)
    {
        Ritual ritual = RitualRegistry.getRitualForId(name);

        if (ritual == null)
            return null;

        return ritual;
    }
}
