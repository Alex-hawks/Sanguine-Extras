package io.github.alex_hawks.SanguineExtras.common.sigil_utils.interdiction;

import WayofTime.alchemicalWizardry.common.entity.projectile.EnergyBlastProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;

public class PushHandlerEnergyBlastProjectile implements IPushCondition
{
    @Override
    public boolean canPush(Entity pushedEntity, EntityPlayer pusher)
    {
        if (pushedEntity instanceof EnergyBlastProjectile)
        {
            return !((EnergyBlastProjectile) pushedEntity).shootingEntity.equals(pusher);
        }
        return false;
    }
}
