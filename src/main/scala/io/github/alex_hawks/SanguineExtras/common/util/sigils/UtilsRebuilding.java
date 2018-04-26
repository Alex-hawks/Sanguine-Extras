package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import lombok.NonNull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

import static io.github.alex_hawks.SanguineExtras.common.items.sigils.ItemRebuilding.TICKS_PER_OP;
import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItemWithDrop;
import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.takeItem;
import static io.github.alex_hawks.util.minecraft.common.Implicit.iBlockPos;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;

public class UtilsRebuilding
{
    public static Map<Integer, Set<BlockPos>> find(BlockPos pos, World world)
    {
        List<BlockPos> solidBlocks = new ArrayList<>();
        List<BlockPos> airBlocks = new ArrayList<>();
        List<BlockPos> currentSolidBlocks = new ArrayList<>();
        List<BlockPos> currentAirBlocks = new ArrayList<>();

        Map<Integer, Set<BlockPos>> toReturn = new HashMap<>();

        IBlockState b;

        IBlockState original = world.getBlockState(pos);
        solidBlocks.add(pos);
        toReturn.put(0, new HashSet<>(solidBlocks));

        BlockPos p;

        for (EnumFacing d : EnumFacing.values())
        {
            p = shift(pos, d);
            b = world.getBlockState(p);

            if (!b.getMaterial().isSolid())
            {
                airBlocks.add(p);
            }
        }

        for (int i = 1; i <= Base.sigil.rebuild.iterations; i++)
        {
            Set<BlockPos> set = new HashSet<>();
            toReturn.put(i, set);

            //  Going to do this properly
            currentSolidBlocks.clear();
            currentSolidBlocks.addAll(solidBlocks);
            currentAirBlocks.clear();
            currentAirBlocks.addAll(airBlocks);

            for (BlockPos v : currentSolidBlocks)
            {
                for (EnumFacing d : EnumFacing.values())
                {
                    p = shift(v, d);
                    b = world.getBlockState(p);

                    if (b.getMaterial().isSolid() && b.equals(original))
                    {
                        D2:
                        for (EnumFacing d2 : EnumFacing.values())
                        {
                            if (!world.getBlockState(shift(p, d2)).getMaterial().isSolid())
                            {
                                solidBlocks.add(p);
                                set.add(p);
                                break D2;
                            }
                        }
                    } else if (!b.getMaterial().isSolid())
                    {
                        airBlocks.add(p);
                    }
                }
            }
            for (BlockPos v : currentAirBlocks)
            {
                for (EnumFacing d : EnumFacing.values())
                {
                    p = shift(v, d);
                    b = world.getBlockState(p);

                    if (!b.getMaterial().isSolid())
                    {
                        airBlocks.add(p);
                    }
                }
            }
        }

        return toReturn;
    }

    public static Map<Integer, Set<BlockPos>> find(BlockPos coords, World w, EnumFacing side)
    {
        return find(coords, w);
    }

    public static void doReplace(EntityPlayer player, UUID sigilOwner, BlockPos ls, World w, IBlockState oldBlock, IBlockState newBlock, EnumHand hand)
    {
        Map<Integer, Set<BlockPos>> tmp = new HashMap<>();
        tmp.put(0, new HashSet<>());
        tmp.get(0).add(ls);
        doReplace(player, sigilOwner, tmp, w, oldBlock, newBlock, hand);
    }

    public static void doReplace(@NonNull EntityPlayer player, UUID sigilOwner, @NonNull final Map<Integer, Set<BlockPos>> map, World w, IBlockState oldBlock, IBlockState newBlock, EnumHand hand)
    {
        ItemStack stack = player.inventory.getCurrentItem();
        MinecraftForge.EVENT_BUS.register(new Object()
        {
            Iterator<Map.Entry<Integer, Set<BlockPos>>> it = map.entrySet().iterator();
            int ticks = 0;
            BreakEvent e;
            PlaceEvent e2;
            BlockSnapshot s;
            IBlockState t;

            @SubscribeEvent
            public void onWorldTick(TickEvent.WorldTickEvent event)
            {
                if (event.phase == END)
                    return;
                ticks++;
                if (ticks % TICKS_PER_OP == 0)
                {
                    for (BlockPos v : it.next().getValue())
                    {
                        if (w.getBlockState(v).equals(oldBlock))
                        {
                            e = new BreakEvent(w, v, oldBlock, player);
                            s = new BlockSnapshot(w, v, newBlock);
                            e2 = new PlaceEvent(s, newBlock, player, hand);
                            if (!MinecraftForge.EVENT_BUS.post(e))
                            {
                                if (!MinecraftForge.EVENT_BUS.post(e2))
                                {
                                    if (BloodUtils.drainSoulNetworkWithDamage(sigilOwner, Base.sigil.rebuild.cost, player) && takeItem(player, new ItemStack(newBlock.getBlock(), 1, newBlock.getBlock().getMetaFromState(newBlock)), stack))
                                    {
                                        putItemWithDrop(player, oldBlock.getBlock().getDrops(w, v, oldBlock, 0).toArray(new ItemStack[0]));
                                        w.setBlockState(v, newBlock, 0x3);

                                        if (e.getExpToDrop() > 0)
                                            w.spawnEntity(new EntityXPOrb(w, player.posX, player.posY, player.posZ, e.getExpToDrop()));
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


    public static BlockPos shift(BlockPos pos, EnumFacing face)
    {
        return iBlockPos(pos).shift(face);
    }

    public static BlockPos plus(BlockPos pos, int x, int y, int z)
    {
        return iBlockPos(pos).$plus(x, y, z);
    }
}
