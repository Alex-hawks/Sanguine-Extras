package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import lombok.NonNull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItemWithDrop;
import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.takeItem;

public class UtilsRebuilding
{
    public static Map<Integer, Set<Vector3>> find(BlockPos pos, World world)
    {
        List<Vector3> solidBlocks = new ArrayList<Vector3>();
        List<Vector3> airBlocks = new ArrayList<Vector3>();
        List<Vector3> currentSolidBlocks = new ArrayList<Vector3>();
        List<Vector3> currentairBlocks = new ArrayList<Vector3>();

        Map<Integer, Set<Vector3>> toReturn = new HashMap<>();

        IBlockState b;

        IBlockState original = world.getBlockState(pos);
        solidBlocks.add(new Vector3(pos));
        toReturn.put(0, new HashSet<>(solidBlocks));

        BlockPos p;
        for (EnumFacing d : EnumFacing.values())
        {
            p = pos.add(d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ());
            b = world.getBlockState(p);

            if (!b.getMaterial().isSolid())
            {
                airBlocks.add(new Vector3(p));
            }
        }

        for (int i = 1; i <= SanguineExtras.pathfindIterations; i++)
        {
            Set<Vector3> set = new HashSet<>();
            toReturn.put(i, set);

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

                    if (b.getMaterial().isSolid() && b.equals(original))
                    {
                        solidBlocks.add(new Vector3(p));
                        set.add(new Vector3(p));
                    } else
                    {
                        airBlocks.add(new Vector3(p));
                    }
                }
            }
            for (Vector3 v : currentairBlocks)
            {
                for (EnumFacing d : EnumFacing.values())
                {
                    p = pos.add(d.getFrontOffsetX(), d.getFrontOffsetY(), d.getFrontOffsetZ());
                    b = world.getBlockState(p);

                    if (!b.getMaterial().isSolid())
                    {
                        airBlocks.add(new Vector3(p));
                    }
                }
            }
        }
//
//        Vector3 vec3;
//        block:
//        for (Vector3 v : currentSolidBlocks)
//        {
//            for (EnumFacing d : EnumFacing.values())
//            {
//                for (Vector3 air : airBlocks)
//                {
//                    vec3 = new Vector3(v.x() + d.getFrontOffsetX(), v.y() + d.getFrontOffsetY(), v.z() + d.getFrontOffsetZ());
//
//                    if (air.equals(vec3))
//                    {
//                        toReturn.add(v);
//                        continue block;
//                    }
//                }
//            }
//        }

        return toReturn;
    }

    public static Map<Integer, Set<Vector3>> find(Vector3 coords, World w)
    {
        return find(coords.toPos(), w);
    }

    public static void doReplace(EntityPlayer player, String sigilOwner, Vector3 ls, World w, IBlockState oldBlock, IBlockState newBlock)
    {
        Map<Integer, Set<Vector3>> tmp = new HashMap<>();
        tmp.put(0, new HashSet<>());
        tmp.get(0).add(ls);
        doReplace(player, sigilOwner, tmp, w, oldBlock, newBlock);
    }

    public static void doReplace(@NonNull EntityPlayer player, String sigilOwner, @NonNull final Map<Integer, Set<Vector3>> map, World w, IBlockState oldBlock, IBlockState newBlock)
    {
        MinecraftForge.EVENT_BUS.register(new Object()
        {
            Iterator<Map.Entry<Integer, Set<Vector3>>> it = map.entrySet().iterator();
            int ticks = 0;
            BreakEvent e;
            PlaceEvent e2;
            BlockSnapshot s;
            IBlockState t;

            @SubscribeEvent
            public void onWorldTick(TickEvent.WorldTickEvent event)
            {
                ticks++;
                if (ticks % 20 == 0)
                {
                    for (Vector3 v : it.next().getValue())
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
                                    if (BloodUtils.drainSoulNetwork(sigilOwner, SanguineExtras.rebuildSigilCost, player) && takeItem(player, new ItemStack(newBlock.getBlock(), 1, newBlock.getBlock().getMetaFromState(newBlock))))
                                    {
                                        putItemWithDrop(player, oldBlock.getBlock().getDrops(w, v.toPos(), oldBlock, 0).toArray(new ItemStack[0]));
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

                    if (!it.hasNext())
                        MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });
    }
}
