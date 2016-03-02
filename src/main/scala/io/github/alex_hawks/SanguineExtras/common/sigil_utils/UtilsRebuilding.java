package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.util.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItem;
import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.takeItem;

public class UtilsRebuilding
{
    public static List<Vector3> find(BlockPos pos, World world)
    {
        List<Vector3> solidBlocks = new ArrayList<Vector3>();
        List<Vector3> airBlocks = new ArrayList<Vector3>();
        List<Vector3> currentSolidBlocks = new ArrayList<Vector3>();
        List<Vector3> currentairBlocks = new ArrayList<Vector3>();

        List<Vector3> toReturn = new ArrayList<Vector3>();

        IBlockState b;

        IBlockState original = world.getBlockState(pos);
        solidBlocks.add(new Vector3(pos));

        BlockPos p;
        for (EnumFacing d : EnumFacing.values())
        {
            p = pos.add(d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ());
            b = world.getBlockState(p);

            if (!b.getBlock().getMaterial().isSolid())
            {
                airBlocks.add(new Vector3(p));
            }
        }

        for (int i = 0; i < SanguineExtras.pathfindIterations; i++)
        {
            //  Going to do this properly
            currentSolidBlocks.clear();
            currentSolidBlocks.addAll(solidBlocks);
            currentairBlocks.clear();
            currentairBlocks.addAll(airBlocks);

            for (Vector3 v : currentSolidBlocks)
            {
                for (EnumFacing d : EnumFacing.values())
                {
                    p = pos.add(d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ());
                    b = world.getBlockState(p);

                    if (b.getBlock().getMaterial().isSolid() && b.equals(original))
                    {
                        solidBlocks.add(new Vector3(pos));
                    } else
                    {
                        airBlocks.add(new Vector3(pos));
                    }
                }
            }
            for (Vector3 v : currentairBlocks)
            {
                for (EnumFacing d : EnumFacing.values())
                {
                    p = pos.add(d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ());
                    b = world.getBlockState(p);

                    if (!b.getBlock().getMaterial().isSolid())
                    {
                        airBlocks.add(new Vector3(p));
                    }
                }
            }
        }

        Vector3 vec3;
        block:
        for (Vector3 v : currentSolidBlocks)
        {
            for (EnumFacing d : EnumFacing.values())
            {
                for (Vector3 air : airBlocks)
                {
                    vec3 = new Vector3(v.x() + d.getFrontOffsetX(), v.y() + d.getFrontOffsetY(), v.z() + d.getFrontOffsetZ());

                    if (air.equals(vec3))
                    {
                        toReturn.add(v);
                        continue block;
                    }
                }
            }
        }

        return toReturn;
    }

    public static List<Vector3> find(Vector3 coords, World w)
    {
        return find(coords.toPos(), w);
    }

    public static void doReplace(EntityPlayer player, UUID sigilOwner, List<Vector3> list, World w, IBlockState oldBlock, IBlockState newBlock)
    {
        doReplace(player, sigilOwner, list.toArray(new Vector3[0]), w, oldBlock, newBlock);
    }

    public static void doReplace(EntityPlayer player, UUID sigilOwner, Vector3[] list, World w, IBlockState oldBlock, IBlockState newBlock)
    {
        BreakEvent e;
        PlaceEvent e2;
        BlockSnapshot s;
        for (Vector3 v : list)
        {
            if (w.getBlockState(v.toPos()).equals(oldBlock))
            {
                e = new BreakEvent(w, v.toPos(), oldBlock, player);
                s = new BlockSnapshot(w, v.toPos(), newBlock);
                e2 = new PlaceEvent(s, newBlock, player);
                if (!MinecraftForge.EVENT_BUS.post(e))
                {
                    if (!MinecraftForge.EVENT_BUS.post(e2))
                    {
                        if (BloodUtils.drainSoulNetwork(sigilOwner, SanguineExtras.rebuildSigilCost) && takeItem(player, new ItemStack(newBlock.getBlock(), 1, newBlock.getBlock().getMetaFromState(newBlock))))
                        {
                            putItem(player, oldBlock.getBlock().getDrops(w, v.toPos(), oldBlock, 0).toArray(new ItemStack[0]));
                            w.setBlockState(v.toPos(), newBlock, 0x3);

                            if (e.getExpToDrop() > 0)
                                w.spawnEntityInWorld(new EntityXPOrb(w, player.posX, player.posY, player.posZ, e.getExpToDrop()));
                        } else
                        {
                            break;
                        }
                    }
                }
            }
        }
    }
}
