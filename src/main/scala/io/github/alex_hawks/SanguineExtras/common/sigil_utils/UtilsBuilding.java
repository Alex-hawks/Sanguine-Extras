package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.EnumFacing.*;

public class UtilsBuilding
{
    static final int QUARTER_CIRCLE = 90;
    static final EnumFacing[] YAW_ROTATION = {SOUTH, WEST, NORTH, EAST};

    public static Set<Vector3> getBlocksForBuild(World w, Vector3 v, EnumFacing d, EntityPlayer player, int limit)
    {
        Set<Vector3> ls = new HashSet<Vector3>();

        IBlockState state = w.getBlockState(v.toPos());

        EnumSet<EnumFacing> dirs = EnumSet.allOf(EnumFacing.class);
        dirs.removeAll(EnumSet.of(d, d.getOpposite()));

        if (player.isSneaking() && dirs.containsAll(EnumSet.of(UP, DOWN)))
        {
            dirs.removeAll(EnumSet.of(UP, DOWN));
        }

        float yaw = player.rotationYaw;

        if (player.isSneaking() && dirs.size() > 2)
        {
            while (yaw < 0)
                yaw += (4 * QUARTER_CIRCLE);
            yaw %= (4 * QUARTER_CIRCLE);

            int val = (int) (yaw / QUARTER_CIRCLE + 0.5);

            dirs.removeAll(EnumSet.of(getDir(val), getDir(val).getOpposite()));
        }

        ls.add(new Vector3(v.x() + d.getFrontOffsetX(), v.y() + d.getFrontOffsetY(), v.z() + d.getFrontOffsetZ()));

        IBlockState origin, check;

        Set<Vector3> buffer;

        for (int i = 0; i < limit; i++)
        {
            buffer = new HashSet<Vector3>(ls);

            for (Vector3 v3 : buffer)
            {
                for (EnumFacing dir : dirs)
                {
                    check = w.getBlockState(v3.shift(dir).toPos());
                    origin = w.getBlockState(v3.shift(dir).shift(d.getOpposite()).toPos());

                    if (origin.equals(state) && check.getBlock().isReplaceable(w, v3.shift(dir).toPos()))
                    {
                        ls.add(v3.shift(dir));
                        if (ls.size() >= limit)
                            return ls;
                    }
                }
            }
        }

        return ls;
    }

    public static EnumFacing getDir(int i)
    {
        return YAW_ROTATION[i % YAW_ROTATION.length];
    }
}
