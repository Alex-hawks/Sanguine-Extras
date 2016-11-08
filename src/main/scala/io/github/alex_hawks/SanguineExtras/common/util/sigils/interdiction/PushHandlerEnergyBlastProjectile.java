package io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction;

import io.github.alex_hawks.SanguineExtras.api.sigil.IPushCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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

    /**
     * just so this class can compile until the Energy Bazooka is readded
     */
    private abstract class EnergyBlastProjectile extends Entity
    {
        public Entity shootingEntity;

        public EnergyBlastProjectile(World worldIn)
        {
            super(worldIn);
        }
    }
}
