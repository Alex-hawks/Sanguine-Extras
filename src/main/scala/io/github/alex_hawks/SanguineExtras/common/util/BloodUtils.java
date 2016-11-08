package io.github.alex_hawks.SanguineExtras.common.util;


import WayofTime.bloodmagic.api.altar.EnumAltarTier;
import WayofTime.bloodmagic.api.iface.IBindable;
import WayofTime.bloodmagic.api.registry.RitualRegistry;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.saving.SoulNetwork;
import WayofTime.bloodmagic.api.util.helper.BindableHelper;
import WayofTime.bloodmagic.api.util.helper.NetworkHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import com.google.common.base.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class BloodUtils
{
    public static boolean drainSoulNetwork(String player, int amount, EntityPlayer user)
    {
        if (user == null)
            return NetworkHelper.getSoulNetwork(player.toString()).syphon(amount) >= amount;
        else
            return user.capabilities.isCreativeMode ? true : NetworkHelper.getSoulNetwork(player).syphon(amount) >= amount;
    }

    public static boolean drainSoulNetworkWithNausea(UUID player, int amount, EntityPlayer user)
    {
        if (user != null && user.capabilities.isCreativeMode)
            return true;

        boolean b = NetworkHelper.getSoulNetwork(player.toString()).syphon(amount) >= amount;

        if (!b)
        {
            EntityPlayer owner = PlayerHelper.getPlayerFromUUID(player);
            if (owner != null)
                owner.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 99));
        }

        return b;
    }

    public static boolean drainSoulNetworkWithDamage(String uuid, @NotNull EntityPlayer player, int amount)
    {
        if (player.capabilities.isCreativeMode)
            return true;
        if (player.worldObj.isRemote)
            return false;

        SoulNetwork n = NetworkHelper.getSoulNetwork(uuid);

        n.syphonAndDamage(player, amount);

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

    public static String getOrBind(ItemStack stack, EntityPlayer player)
    {
        if (!(stack.getItem() instanceof IBindable))
            return null;
        String uuid = ((IBindable) stack.getItem()).getOwnerUUID(stack);

        if (Strings.isNullOrEmpty(uuid))
            BindableHelper.checkAndSetItemOwner(stack, player);

        return ((IBindable) stack.getItem()).getOwnerUUID(stack);
    }
}
