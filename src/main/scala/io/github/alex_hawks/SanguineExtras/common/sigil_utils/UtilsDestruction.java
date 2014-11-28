package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItem;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.util.Vector3;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.FMLCommonHandler;

public class UtilsDestruction
{
    public static List<Vector3> find(int x, int y, int z, World world, int side, int length)
    {
        return find(x, y, z, world, ForgeDirection.getOrientation(side), length);
    }

    public static List<Vector3> find(int x, int y, int z, World world, ForgeDirection side, int length)
    {
        System.out.println(side);
        List<Vector3> toReturn = new ArrayList<Vector3>();
        switch (side)
        {
        case UP: 
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x + i, y - k, z + j));
            break;
        case DOWN:
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x + i, y + k, z + j));
            break;
        case EAST:
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x - k, y + i, z + j));
            break;
        case NORTH:
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x + i, y + j, z + k));
            break;
        case SOUTH:
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x + i, y + j, z - k));
            break;
        case WEST:
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    for (int k = 0;  k < length; k++)
                        toReturn.add(new Vector3(x + k, y + i, z + j));
            break;
        default:
            //throw new IllegalArgumentException("What am I supposed to do with this direction?");
        }
        return toReturn;
    }

    public static void doDrops(EntityPlayer p, String sigilOwner, List<Vector3> list, World w)
    {
        doDrops(p, sigilOwner, list.toArray(new Vector3[0]), w);
    }
    
    public static void doDrops(EntityPlayer p, String sigilOwner, Vector3[] list, World w)
    {
        int blocks = 0;
        
        for(Vector3 v : list)
        {
            if (v.y > 255 || v.y < 0 )
                continue;
            
            Block b = w.getBlock(v.x, v.y, v.z);
            int meta = w.getBlockMetadata(v.x, v.y, v.z);
            
            if (b.getBlockHardness(w, v.x, v.y, v.z) < 0 || b.isAir(w, v.x, v.y, v.z))
                continue;
            
            BreakEvent e = new BreakEvent(v.x, v.y, v.z, w, b, meta, p);

            if (!FMLCommonHandler.instance().bus().post(e))
            {
                if (BloodUtils.drainSoulNetwork(sigilOwner, ++blocks))
                {
                    List<ItemStack> drops = b.getDrops(w, v.x, v.y, v.z, meta, 0);
                    w.setBlock(v.x, v.y, v.z, Blocks.air);

                    for (ItemStack drop : drops)
                    {
                        putItem(p, drop);
                        if (e.getExpToDrop() > 0)
                            w.spawnEntityInWorld(new EntityXPOrb(w, p.posX, p.posY, p.posZ, e.getExpToDrop()));
                    }
                }
            }
        }
    }
}
