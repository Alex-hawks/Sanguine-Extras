package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

import static io.github.alex_hawks.util.minecraft.common.Implicit.iBlockPos;
import static net.minecraft.util.EnumFacing.*;

public class UtilsBuilding
{
    static final int QUARTER_CIRCLE = 90;
    static final EnumFacing[] YAW_ROTATION = {SOUTH, WEST, NORTH, EAST};

    public static Map<Integer, Set<BlockPos>> getBlocksForBuild(World w, BlockPos v, EnumFacing d, EntityPlayer player, int limit, boolean horizontal)
    {
        Set<BlockPos> ls = new HashSet<>();

        IBlockState state = w.getBlockState(v);

        EnumSet<EnumFacing> dirs = EnumSet.allOf(EnumFacing.class);
        dirs.removeAll(EnumSet.of(d, d.getOpposite()));

        if (player.isSneaking())
        {
            if (dirs.containsAll(EnumSet.of(UP, DOWN)))
            {
                if (horizontal)
                    dirs.removeAll(EnumSet.of(UP, DOWN));
                else
                    dirs = EnumSet.of(UP, DOWN);
            }
            else
            {
                float yaw = player.rotationYaw;

                while (yaw < 0)
                    yaw += (4 * QUARTER_CIRCLE);
                yaw %= (4 * QUARTER_CIRCLE);

                int val = (int) (yaw / QUARTER_CIRCLE + 0.5);

                if (horizontal)
                    dirs.removeAll(EnumSet.of(getDir(val), getDir(val).getOpposite()));
                else
                    dirs = EnumSet.of(getDir(val), getDir(val).getOpposite());
            }
        }

        ls.add(plus(v, d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ()));

        IBlockState origin, check;

        Set<BlockPos> buffer;

        Map<Integer, Set<BlockPos>> m = new HashMap<>();
        m.put(0, new HashSet<>(ls));

        for (int i = 1; i <= limit; i++)
        {
            buffer = new HashSet<>(ls);
            m.put(i, new HashSet<>());

            for (BlockPos v3 : buffer)
            {
                for (EnumFacing dir : dirs)
                {
                    check = w.getBlockState(shift(v3, dir));
                    origin = w.getBlockState(shift(shift(v3, dir), (d.getOpposite())));

                    if (origin.equals(state) && check.getBlock().isReplaceable(w, shift(v3, dir)))
                    {
                        ls.add(shift(v3, dir));
                        m.get(i).add(shift(v3, dir));

                        if (ls.size() >= limit)
                            return m;
                    }
                }
            }
        }

        return m;
    }

    public static EnumFacing getDir(int i)
    {
        return YAW_ROTATION[i % YAW_ROTATION.length];
    }
    public static BlockPos shift(BlockPos pos, EnumFacing face)
    {
        return iBlockPos(pos).shift(face);
    }
    public static BlockPos plus(BlockPos pos, int x, int y ,int z)
    {
        return iBlockPos(pos).$plus(x,y,z);
    }
}
