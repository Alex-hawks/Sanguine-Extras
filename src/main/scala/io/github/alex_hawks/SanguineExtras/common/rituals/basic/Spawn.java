package io.github.alex_hawks.SanguineExtras.common.rituals.basic;

import io.github.alex_hawks.SanguineExtras.api.sigil.MobNet;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsMobNet;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemMobNet;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import WayofTime.alchemicalWizardry.api.alchemy.energy.ReagentRegistry;
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;

public class Spawn extends RitualEffect 
{
    public static final class Drain
    {
        public static final int potentia = 20;
        public static final int terrae = 100;
        public static final int orbisTerrae = 100;
        public static final int sanctus = 100;
    }
    
    @Override
    public int getCostPerRefresh()
    {
        return 0;
    }

    @Override
    public List<RitualComponent> getRitualComponentList()
    {
        List<RitualComponent> ls = new LinkedList<RitualComponent>();
        
        ls.add(new RitualComponent( 1, -1,  1, RitualComponent.AIR));
        ls.add(new RitualComponent( 2, -2,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -3,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -4,  2, RitualComponent.WATER));
        ls.add(new RitualComponent( 2, -5,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -6,  2, RitualComponent.EARTH));
        ls.add(new RitualComponent( 1, -6,  1, RitualComponent.FIRE));

        ls.add(new RitualComponent( 1, -1, -1, RitualComponent.AIR));
        ls.add(new RitualComponent( 2, -2, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -3, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -4, -2, RitualComponent.WATER));
        ls.add(new RitualComponent( 2, -5, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -6, -2, RitualComponent.EARTH));
        ls.add(new RitualComponent( 1, -6, -1, RitualComponent.FIRE));

        ls.add(new RitualComponent(-1, -1, -1, RitualComponent.AIR));
        ls.add(new RitualComponent(-2, -2, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -3, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -4, -2, RitualComponent.WATER));
        ls.add(new RitualComponent(-2, -5, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -6, -2, RitualComponent.EARTH));
        ls.add(new RitualComponent(-1, -6, -1, RitualComponent.FIRE));

        ls.add(new RitualComponent(-1, -1,  1, RitualComponent.AIR));
        ls.add(new RitualComponent(-2, -2,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -3,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -4,  2, RitualComponent.WATER));
        ls.add(new RitualComponent(-2, -5,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -6,  2, RitualComponent.EARTH));
        ls.add(new RitualComponent(-1, -6,  1, RitualComponent.FIRE));

//        ls.add(new RitualComponent(0, -4,  0, RitualComponent.DUSK));

//        ls.add(new RitualComponent( 1,  0,  0, RitualComponent.BLANK));
//        ls.add(new RitualComponent( 0,  0,  1, RitualComponent.BLANK));
//        ls.add(new RitualComponent(-1,  0,  0, RitualComponent.BLANK));
//        ls.add(new RitualComponent( 0,  0, -1, RitualComponent.BLANK));
        
        ls.add(new RitualComponent( 1, -6,  0, RitualComponent.EARTH));
        ls.add(new RitualComponent( 0, -6,  1, RitualComponent.EARTH));
        ls.add(new RitualComponent(-1, -6,  0, RitualComponent.EARTH));
        ls.add(new RitualComponent( 0, -6, -1, RitualComponent.EARTH));
        
        return ls;
    }

    @Override
    public void performEffect(IMasterRitualStone stone)
    {
        if (stone.getCooldown() > 0)
        {
            stone.setCooldown(stone.getCooldown() -1);
            return;
        }
        stone.setCooldown(200);
        
        World w = stone.getWorld();
        int x = stone.getXCoord(), y = stone.getYCoord(), z = stone.getZCoord();
        
        if(w.isBlockIndirectlyGettingPowered(x, y, z))
            return;
        
        TileEntity te = w.getTileEntity(x, y+1, z);
        
        if (te instanceof IBloodAltar)
        {
            ItemStack sigilStack = ((IInventory) te).getStackInSlot(0);
            if (sigilStack != null && sigilStack.getItem() instanceof ItemMobNet)
            {
                final int baseCost = SanguineExtras.spawnLpPerHealth;
                int cost = baseCost;

                boolean hasTerrae = this.canDrainReagent(stone, ReagentRegistry.terraeReagent, Drain.terrae, false);
                boolean hasOrbisTerrae = this.canDrainReagent(stone, ReagentRegistry.orbisTerraeReagent, Drain.orbisTerrae, false);
                boolean hasSanctus = this.canDrainReagent(stone, ReagentRegistry.sanctusReagent, Drain.sanctus, false);
                
                cost += hasTerrae ? hasOrbisTerrae ? 0 : baseCost / 2 : hasOrbisTerrae ? baseCost / 2 : baseCost;
                
                cost *= hasSanctus ? 2 : 1;
                
                EntityLivingBase ent = hasSanctus ? UtilsMobNet.createCopiedEntity(sigilStack, w) : UtilsMobNet.createNewEntity(sigilStack, w);
                
                if (ent == null)
                    return;

                if (MobNet.isSpawnBlacklisted(ent.getClass()))
                    return;
                
                if (w.getEntitiesWithinAABB(ent.getClass(), AxisAlignedBB.getBoundingBox(x-2, y-4, z-2, x+2, y, z+2)).size() > (ent instanceof IBossDisplayData ? SanguineExtras.spawnMaxEntities / 10 : SanguineExtras.spawnMaxEntities))
                {
                    return;
                }
                
                if (ent instanceof IBossDisplayData && !SanguineExtras.spawnableBossMobs)
                    return;
                
                if (BloodUtils.drainSoulNetworkWithNausea(stone.getOwner(), (int) (ent.getMaxHealth() + ent.getMaxHealth() - ent.getHealth()) * cost * (ent instanceof IBossDisplayData ? 10 : 1)))
                {
                    this.canDrainReagent(stone, ReagentRegistry.terraeReagent, Drain.terrae, true);
                    this.canDrainReagent(stone, ReagentRegistry.orbisTerraeReagent, Drain.orbisTerrae, true);
                    this.canDrainReagent(stone, ReagentRegistry.sanctusReagent, Drain.sanctus, true);
                    
                    
                    ent.setPosition(x + 0.5, y - 3, z +  + 0.5);
                    w.spawnEntityInWorld(ent);
                    
                    if (this.canDrainReagent(stone, ReagentRegistry.potentiaReagent, Drain.potentia, true))
                    {
                        stone.setCooldown(20);
                    }
                }
                return;
            }
        }
    }

}
