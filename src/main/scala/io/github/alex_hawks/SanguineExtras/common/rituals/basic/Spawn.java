package io.github.alex_hawks.SanguineExtras.common.rituals.basic;

import WayofTime.bloodmagic.api.altar.IBloodAltar;
import WayofTime.bloodmagic.api.ritual.IMasterRitualStone;
import WayofTime.bloodmagic.api.ritual.Ritual;
import WayofTime.bloodmagic.api.ritual.RitualComponent;
import io.github.alex_hawks.SanguineExtras.api.sigil.MobNet;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsMobNet;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

import static WayofTime.bloodmagic.api.ritual.EnumRuneType.*;

public class Spawn extends Ritual
{
    public static final String name = "SE001Spawner";

    public Spawn()
    {
        super(name, 2, 250000, "ritual." + Constants.MetaData.MOD_ID + ".spawn");
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
    public ArrayList<RitualComponent> getComponents()
    {
        ArrayList<RitualComponent> ls = new ArrayList<RitualComponent>();

        this.addCornerRunes(ls, 1, -1, AIR);
        this.addCornerRunes(ls, 2, -2, DUSK);
        this.addCornerRunes(ls, 2, -3, FIRE);
        this.addCornerRunes(ls, 2, -4, WATER);
        this.addCornerRunes(ls, 2, -5, DUSK);
        this.addCornerRunes(ls, 2, -6, EARTH);
        this.addCornerRunes(ls, 1, -6, FIRE);
        this.addCornerRunes(ls, 1, -6, EARTH);

        return ls;
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
            if (sigilStack != null && sigilStack.getItem() instanceof ItemMobNet)
            {
                final int baseCost = SanguineExtras.spawnLpPerHealth;
                int cost = baseCost;

//                boolean hasTerrae = this.canDrainReagent(stone, ReagentRegistry.terraeReagent, Drain.terrae, false);
//                boolean hasOrbisTerrae = this.canDrainReagent(stone, ReagentRegistry.orbisTerraeReagent, Drain.orbisTerrae, false);
//                boolean hasSanctus = this.canDrainReagent(stone, ReagentRegistry.sanctusReagent, Drain.sanctus, false);
//
//                cost += hasTerrae ? hasOrbisTerrae ? 0 : baseCost / 2 : hasOrbisTerrae ? baseCost / 2 : baseCost;
//
//                cost *= hasSanctus ? 2 : 1;
//
//                EntityLivingBase ent = hasSanctus ? UtilsMobNet.createCopiedEntity(sigilStack, w) : UtilsMobNet.createNewEntity(sigilStack, w);
                EntityLivingBase ent = UtilsMobNet.createNewEntity(sigilStack, w);

                if (ent == null)
                    return;

                if (MobNet.isSpawnBlacklisted(ent.getClass()))
                    return;

                if (w.getEntitiesWithinAABB(ent.getClass(), new AxisAlignedBB(pos.add(-2, -4, -2), pos.add(2, 0, 2))).size() > (ent instanceof IBossDisplayData ? SanguineExtras.spawnMaxEntities / 10 : SanguineExtras.spawnMaxEntities))
                {
                    return;
                }

                if (ent instanceof IBossDisplayData && !SanguineExtras.spawnableBossMobs)
                    return;

                if (BloodUtils.drainSoulNetworkWithNausea(UUID.fromString(stone.getOwner()), (int) (ent.getMaxHealth() + ent.getMaxHealth() - ent.getHealth()) * cost * (ent instanceof IBossDisplayData ? 10 : 1), null))
                {
//                    this.canDrainReagent(stone, ReagentRegistry.terraeReagent, Drain.terrae, true);
//                    this.canDrainReagent(stone, ReagentRegistry.orbisTerraeReagent, Drain.orbisTerrae, true);
//                    this.canDrainReagent(stone, ReagentRegistry.sanctusReagent, Drain.sanctus, true);


                    ent.setPosition(pos.getX() + 0.5, pos.getY() - 3, pos.getZ() + +0.5);
                    w.spawnEntityInWorld(ent);

//                    if (this.canDrainReagent(stone, ReagentRegistry.potentiaReagent, Drain.potentia, true))
//                    {
//                        stone.setCooldown(20);
//                    }
                }
                return;
            }
        }
    }

    @Override
    public Ritual getNewCopy()
    {
        return new Spawn();
    }

}
