package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import io.github.alex_hawks.util.Vector3;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class UtilsBuilding
{
    static final int QUARTER_CIRCLE = 90;
    static final ForgeDirection[] YAW_ROTATION = { SOUTH, WEST, NORTH, EAST };
    
    public static Set<Vector3> getBlocksForBuild(World w, Vector3 v, ForgeDirection d, EntityPlayer player, int limit)
    {
        Set<Vector3> ls = new HashSet<Vector3>();
        
        Block b = w.getBlock(v.x, v.y, v.z);
        int meta = w.getBlockMetadata(v.x, v.y, v.z);
        
        EnumSet<ForgeDirection> dirs = EnumSet.allOf(ForgeDirection.class);
        dirs.removeAll(EnumSet.of(d, d.getOpposite(), UNKNOWN));
        
        if (player.isSneaking() && dirs.containsAll(EnumSet.of(UP, DOWN)))
        {
            dirs.removeAll(EnumSet.of(UP, DOWN));
        }
        
        float yaw = player.rotationYaw;
        
        if (player.isSneaking())
        {
            while (yaw < 0)
                yaw += (4 * QUARTER_CIRCLE);
            yaw %= (4 * QUARTER_CIRCLE);
            
            int val = (int) (yaw / QUARTER_CIRCLE + 0.5);
            
            dirs.removeAll(EnumSet.of(getDir(val), getDir(val).getOpposite()));
        }
        
        ls.add(new Vector3(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ));

        Block b2, b3;
        int m;
        
        Set<Vector3> buffer;
        
        for (int i = 0; i < limit; i++)
        {
            buffer = new HashSet<Vector3>(ls);
            
            for (Vector3 v3 : buffer)
            {
                for (ForgeDirection dir : dirs)
                {
                    b2 = w.getBlock(v3.x + dir.offsetX - d.offsetX, v3.y + dir.offsetY - d.offsetY, v3.z + dir.offsetZ - d.offsetZ);
                    m = w.getBlockMetadata(v3.x + dir.offsetX - d.offsetX, v3.y + dir.offsetY - d.offsetY, v3.z + dir.offsetZ - d.offsetZ);
                    
                    b3 = w.getBlock(v3.x + dir.offsetX, v3.y + dir.offsetY, v3.z + dir.offsetZ);

                    if (b2.equals(b) && m == meta && b3.isReplaceable(w, v3.x + dir.offsetX, v3.y + dir.offsetY, v3.z + dir.offsetZ))
                    {
                        ls.add(new Vector3(v3.x + dir.offsetX, v3.y + dir.offsetY, v3.z + dir.offsetZ));
                        if (ls.size() >= limit)
                            return ls;
                        if (Math.abs(v.x - v3.x) > (limit / 2 + 1))
                            return ls;
                        if (Math.abs(v.y - v3.y) > (limit / 2 + 1))
                            return ls;
                        if (Math.abs(v.z - v3.z) > (limit / 2 + 1))
                            return ls;
                    }
                }
            }
        }
        
        return ls;
    }
    
    public static ForgeDirection getDir(int i)
    {
        return YAW_ROTATION[i % YAW_ROTATION.length];
    }
}
