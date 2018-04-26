package io.github.alex_hawks.SanguineExtras.common.util;

import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.event.ItemBindEvent;
import WayofTime.bloodmagic.iface.IBindable;
import WayofTime.bloodmagic.orb.BloodOrb;
import WayofTime.bloodmagic.orb.IBloodOrb;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualRegistry;
import WayofTime.bloodmagic.util.DamageSourceBloodMagic;
import WayofTime.bloodmagic.util.helper.BindableHelper;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;


public class BloodUtils
{
    public static boolean drainSoulNetwork(UUID owner, int amount, EntityPlayer user)
    {
        if (user == null)
            return NetworkHelper.getSoulNetwork(owner).syphon(amount) >= amount;
        else
            return user.capabilities.isCreativeMode || NetworkHelper.getSoulNetwork(user).syphon(amount) >= amount;
    }

    public static boolean drainSoulNetworkWithNausea(UUID owner, int amount, @Nullable EntityPlayer user)
    {
        if (user == null || user.capabilities.isCreativeMode)
            return true;

        SoulNetwork n = NetworkHelper.getSoulNetwork(owner);

        if (n == null)
            return false;
        else if (n.syphon(amount) != amount)
        {
            n.causeNausea();
            return false;
        }
        else
            return true;
    }

    public static boolean drainSoulNetworkWithDamage(@Nullable UUID owner, int amount, @Nonnull EntityPlayer user)
    {
        if (user.capabilities.isCreativeMode)
            return true;
        if (user.getEntityWorld().isRemote)
            return false;

        SoulNetwork n = owner == null
                ? null
                : NetworkHelper.getSoulNetwork(owner);

        if (n == null)
            hurtPlayer(user, amount);
        else
            n.syphonAndDamage(user, amount);

        return true;
    }

    /**
     * @return the highest tier of orb available, -1 if there is no orb at all
     */
    public static int getHighestTierOrb()
    {
        return AltarTier.MAXTIERS;
    }

    public static Ritual getEffectFromString(String name)
    {
        Ritual ritual = RitualRegistry.getRitualForId(name);

        if (ritual == null)
            return null;

        return ritual;
    }

    public static Binding getOrBind(ItemStack stack, @Nullable EntityPlayer player)
    {
        if (PlayerHelper.isFakePlayer(player))
            return null;

        Binding binding = null;

        if (!stack.isEmpty() && stack.getItem() instanceof IBindable)
        {
            IBindable bindable = (IBindable) stack.getItem();
            binding = bindable.getBinding(stack);

            if (player == null)
                return binding;

            if (binding == null) {
                if (bindable.onBind(player, stack))
                {
                    ItemBindEvent toPost = new ItemBindEvent(player, stack);
                    if (MinecraftForge.EVENT_BUS.post(toPost))
                        return null;

                    binding = new Binding(player.getGameProfile().getId(), player.getGameProfile().getName());
                    BindableHelper.applyBinding(stack, binding);
                }
            }
            else if (binding.getOwnerId().equals(player.getGameProfile().getId()) && !binding.getOwnerName().equals(player.getGameProfile().getName()))
            {
                binding.setOwnerName(player.getGameProfile().getName());
                BindableHelper.applyBinding(stack, binding);
            }
        }

        if (!stack.isEmpty() && stack.getItem() instanceof IBloodOrb && player != null)
        {
            IBloodOrb bloodOrb = (IBloodOrb) stack.getItem();
            SoulNetwork network = NetworkHelper.getSoulNetwork(player);

            BloodOrb orb = bloodOrb.getOrb(stack);
            if (orb == null)
                return null;

            if (orb.getTier() > network.getOrbTier())
                network.setOrbTier(orb.getTier());
        }
        return binding;
    }

    public static void hurtPlayer(EntityPlayer user, float syphon) {
        if (user != null) {
            if (syphon < 100 && syphon > 0) {
                if (!user.capabilities.isCreativeMode) {
                    user.hurtResistantTime = 0;
                    user.attackEntityFrom(DamageSourceBloodMagic.INSTANCE, 1.0F);
                }

            } else if (syphon >= 100) {
                if (!user.capabilities.isCreativeMode) {
                    for (int i = 0; i < ((syphon + 99) / 100); i++) {
                        user.hurtResistantTime = 0;
                        user.attackEntityFrom(DamageSourceBloodMagic.INSTANCE, 1.0F);
                    }
                }
            }
        }
    }
}
