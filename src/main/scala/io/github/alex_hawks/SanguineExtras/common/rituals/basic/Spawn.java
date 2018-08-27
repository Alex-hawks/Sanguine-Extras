package io.github.alex_hawks.SanguineExtras.common.rituals.basic;

import java.util.UUID;
import java.util.function.Consumer;

import WayofTime.bloodmagic.altar.IBloodAltar;
import WayofTime.bloodmagic.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.items.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import io.github.alex_hawks.SanguineExtras.common.util.config.Overrides;
import io.github.alex_hawks.SanguineExtras.common.util.config.SpawnEntry;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsMobNet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static WayofTime.bloodmagic.ritual.EnumRuneType.*;

public class Spawn extends Ritual
{
    public static final String name = "SE001Spawner";

    public Spawn()
    {
        super(name, 2, 250000, "ritual." + Constants.Metadata.MOD_ID + ".spawn");
    }

    public static final class Drain
    {
        public static final int potentia = 20;
        public static final int terrae = 100;
        public static final int orbisTerrae = 100;
        public static final int sanctus = 100;
    }

    @Override
    public int getRefreshCost()
    {
        return 0;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> ls)
    {
        this.addCornerRunes(ls, 1, -1, AIR);
        this.addCornerRunes(ls, 2, -2, DUSK);
        this.addCornerRunes(ls, 2, -3, FIRE);
        this.addCornerRunes(ls, 2, -4, WATER);
        this.addCornerRunes(ls, 2, -5, DUSK);
        this.addCornerRunes(ls, 2, -6, EARTH);
        this.addCornerRunes(ls, 1, -6, FIRE);
        this.addCornerRunes(ls, 1, -6, EARTH);
    }

    @Override
    public void performRitual(IMasterRitualStone stone)
    {
        if (stone.getCooldown() > 0)
        {
            stone.setCooldown(stone.getCooldown() - 1);
            return;
        }
        stone.setCooldown(200);

        World w = stone.getWorldObj();
        BlockPos pos = stone.getBlockPos();

        if (w.isBlockIndirectlyGettingPowered(pos) > 0)
            return;

        TileEntity te = w.getTileEntity(pos.add(0, 1, 0));

        if (te instanceof IBloodAltar)
        {
            ItemStack sigilStack = ((IInventory) te).getStackInSlot(0);
            if (sigilStack.getItem() instanceof ItemMobNet)
            {
                // TODO Consume Steadfast Will to make an exact copy
                EntityLivingBase ent = UtilsMobNet.createNewEntity(sigilStack, w);

                if (ent == null)
                    return;

                final SpawnEntry data = Overrides.Spawn.INSTANCE.getData().get(getEntityID(ent));
                if (data == null || !data.enabled())
                    return;
                int cost = data.lpMultiplier() * (ent.isNonBoss() ? 4 : 40);
                // TODO Consume other varieties of will to make it cheaper

                if (w.getEntitiesWithinAABB(ent.getClass(), new AxisAlignedBB(pos.add(-2, -4, -2), pos.add(2, 0, 2))).size() > (ent.isNonBoss() ? Base.ritual.spawn.maxEntities : Math.max(Base.ritual.spawn.maxEntities / 10, 1)))
                    return;

                if (ent.isNonBoss() || Base.ritual.spawn.spawnableBosses)
                    return;

                if (BloodUtils.drainSoulNetworkWithNausea(UUID.fromString(stone.getOwner().toString()), (int) (2 * ent.getMaxHealth() - ent.getHealth()) * cost, null))
                {
//                    this.canDrainReagent(stone, ReagentRegistry.terraeReagent, Drain.terrae, true);
//                    this.canDrainReagent(stone, ReagentRegistry.orbisTerraeReagent, Drain.orbisTerrae, true);
//                    this.canDrainReagent(stone, ReagentRegistry.sanctusReagent, Drain.sanctus, true);


                    ent.setPosition(pos.getX() + 0.5, pos.getY() - 3, pos.getZ() + +0.5);
                    w.spawnEntity(ent);
                    // TODO consume Raw Will to lower the cooldown
//                    if (this.canDrainReagent(stone, ReagentRegistry.potentiaReagent, Drain.potentia, true))
//                    {
//                        stone.setCooldown(20);
//                    }
                }
            }
        }
    }

    @Override
    public Ritual getNewCopy()
    {
        return new Spawn();
    }

    public static ResourceLocation getEntityID(Entity ent)
    {
        return EntityList.getKey(ent);
    }

}
