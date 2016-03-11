package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItem;

public class UtilsDestruction
{
    public static List<Vector3> find(BlockPos pos, World world, EnumFacing side, int length)
    {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        System.out.println(side);
        List<Vector3> toReturn = new ArrayList<Vector3>();
        switch (side)
        {
            case UP:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x + i, y - k, z + j));
                break;
            case DOWN:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x + i, y + k, z + j));
                break;
            case EAST:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x - k, y + i, z + j));
                break;
            case NORTH:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x + i, y + j, z + k));
                break;
            case SOUTH:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x + i, y + j, z - k));
                break;
            case WEST:
                for (int i = -1; i < 2; i++)
                    for (int j = -1; j < 2; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new Vector3(x + k, y + i, z + j));
                break;
            default:
                throw new IllegalArgumentException("What am I supposed to do with this direction?");
        }
        return toReturn;
    }

    public static void doDrops(EntityPlayer p, UUID sigilOwner, List<Vector3> list, World w)
    {
        doDrops(p, sigilOwner, list.toArray(new Vector3[0]), w);
    }

    public static void doDrops(EntityPlayer p, UUID sigilOwner, Vector3[] list, World w)
    {
        int blocks = 0;

        for (Vector3 v : list)
        {
            if (v.y() > 255 || v.y() < 0)
                continue;

            IBlockState b = w.getBlockState(v.toPos());

            if (b.getBlock().getBlockHardness(w, v.toPos()) < 0 || b.getBlock().isAir(w, v.toPos()))
                continue;

            BreakEvent e = new BreakEvent(w, v.toPos(), b, p);

            if (!MinecraftForge.EVENT_BUS.post(e))
            {
                if (BloodUtils.drainSoulNetwork(sigilOwner, ++blocks, p))
                {
                    List<ItemStack> drops = b.getBlock().getDrops(w, v.toPos(), b, 0);
                    w.setBlockToAir(v.toPos());

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
