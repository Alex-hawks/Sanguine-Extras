package io.github.alex_hawks.SanguineExtras.common.rituals;

import io.github.alex_hawks.SanguineExtras.api.MobNetBlacklist;
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
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;

public class Spawn extends RitualEffect 
{
    @Override
    public int getCostPerRefresh()
    {
        return 0;
    }

    @Override
    public List<RitualComponent> getRitualComponentList()
    {
        List<RitualComponent> ls = new LinkedList<RitualComponent>();
        
        ls.add(new RitualComponent( 1,  0,  1, RitualComponent.AIR));
        ls.add(new RitualComponent( 2,  0,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -1,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -2,  2, RitualComponent.WATER));
        ls.add(new RitualComponent( 2, -3,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -4,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 1, -4,  1, RitualComponent.EARTH));

        ls.add(new RitualComponent( 1,  0, -1, RitualComponent.AIR));
        ls.add(new RitualComponent( 2,  0, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 2, -1, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -2, -2, RitualComponent.WATER));
        ls.add(new RitualComponent( 2, -3, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent( 2, -4, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent( 1, -4, -1, RitualComponent.EARTH));

        ls.add(new RitualComponent(-1,  0, -1, RitualComponent.AIR));
        ls.add(new RitualComponent(-2,  0, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -1, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -2, -2, RitualComponent.WATER));
        ls.add(new RitualComponent(-2, -3, -2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -4, -2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-1, -4, -1, RitualComponent.EARTH));

        ls.add(new RitualComponent(-1,  0,  1, RitualComponent.AIR));
        ls.add(new RitualComponent(-2,  0,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-2, -1,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -2,  2, RitualComponent.WATER));
        ls.add(new RitualComponent(-2, -3,  2, RitualComponent.FIRE));
        ls.add(new RitualComponent(-2, -4,  2, RitualComponent.DUSK));
        ls.add(new RitualComponent(-1, -4,  1, RitualComponent.EARTH));

        ls.add(new RitualComponent(0, -4,  0, RitualComponent.DUSK));

//        ls.add(new RitualComponent( 1,  0,  0, RitualComponent.BLANK));
//        ls.add(new RitualComponent( 0,  0,  1, RitualComponent.BLANK));
//        ls.add(new RitualComponent(-1,  0,  0, RitualComponent.BLANK));
//        ls.add(new RitualComponent( 0,  0, -1, RitualComponent.BLANK));
        
        ls.add(new RitualComponent( 1, -4,  0, RitualComponent.BLANK));
        ls.add(new RitualComponent( 0, -4,  1, RitualComponent.BLANK));
        ls.add(new RitualComponent(-1, -4,  0, RitualComponent.BLANK));
        ls.add(new RitualComponent( 0, -4, -1, RitualComponent.BLANK));
        
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
        
        World w = stone.getWorld();
        int x = stone.getXCoord(), y = stone.getYCoord(), z = stone.getZCoord();
        
        if(w.isBlockIndirectlyGettingPowered(x, y, z))
            return;
        
        TileEntity te = w.getTileEntity(x, y+1, z);
        
        if (w.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x-2, y-4, z-2, x+2, y, z+2)).size() > 20)
        {
            stone.setCooldown(200);
            return;
        }
        
        if (te instanceof IBloodAltar)
        {
            ItemStack sigilStack = ((IInventory) te).getStackInSlot(0);
            if (sigilStack != null && sigilStack.getItem() instanceof ItemMobNet)
            {
                EntityLivingBase ent = ItemMobNet.createNewEntity(sigilStack, w);
                
                if (ent == null)
                {
                    stone.setCooldown(200);
                    return;
                }

                if (MobNetBlacklist.isSpawnBlacklisted(ent.getClass()))
                    return;
                
                if (BloodUtils.drainSoulNetworkWithNausea(stone.getOwner(), (int) (ent.getMaxHealth() + ent.getMaxHealth() - ent.getHealth()) * (ent instanceof IBossDisplayData ? 3000 : 300)))
                {
                    ent.setPosition(x + 0.5, y - 3, z +  + 0.5);
                    w.spawnEntityInWorld(ent);
                }
                stone.setCooldown(10);
                return;
            }
        }
        stone.setCooldown(200);
    }

}
