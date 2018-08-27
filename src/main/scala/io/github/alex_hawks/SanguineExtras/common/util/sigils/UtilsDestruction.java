package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SEEventHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilsDestruction
{
    private static boolean eventCaughtStuff = false;
    private static int blocks = 0;

    public static List<BlockPos> find(BlockPos pos, World world, EnumFacing side, int length)
    {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        SanguineExtras.LOG.debug("Side: {}", side);
        List<BlockPos> toReturn = new ArrayList<>();
        switch (side)
        {
            case UP:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x + i, y - k, z + j));
                break;
            case DOWN:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x + i, y + k, z + j));
                break;
            case EAST:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x - k, y + i, z + j));
                break;
            case NORTH:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x + i, y + j, z + k));
                break;
            case SOUTH:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x + i, y + j, z - k));
                break;
            case WEST:
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        for (int k = 0; k < length; k++)
                            toReturn.add(new BlockPos(x + k, y + i, z + j));
                break;
            default:
                throw new IllegalArgumentException("What am I supposed to do with this direction?");
        }
        return toReturn;
    }

    public static void doDrops(final EntityPlayer p, final EnumHand hand, final UUID sigilOwner, List<BlockPos> list, final World w)
    {
        if (w.isRemote)
          return;

        PlayerUtils.startOrb(p);
        blocks = 0;

        for (BlockPos v : list)
        {
            if (v.getY() > 255 || v.getY() < 0)
                continue;

            IBlockState b = w.getBlockState(v);

            if (b.getBlockHardness(w, v) < 0 || b.getBlock().isAir(b, w, v))
                continue;

            BreakEvent e = new BreakEvent(w, v, b, p);

            if (!MinecraftForge.EVENT_BUS.post(e))
            {
                eventCaughtStuff = false;
                SEEventHandler.hand_$eq(hand); // LOL HAX coz HarvestDropsEvent doesn't have a hand or a harvesting stack for me to use 😛

                MinecraftForge.EVENT_BUS.register(new Object()
                {
                    @SubscribeEvent(priority = EventPriority.HIGH) // Highest is for the Cutting Enchantment
                    public void catchDrops(HarvestDropsEvent e)
                    {
                        if (e.getPos() != v)
                            return; // Somehow, something has happened between this handler's registration and the Block#harvestBlock call below that made this event exist on something else
                        if (!e.getDrops().isEmpty())
                        {
                            BloodUtils.drainSoulNetwork(sigilOwner, ++blocks, p);
                            eventCaughtStuff = true;
                        }
                        e.getDrops().forEach((stack) -> {
                            if (w.rand.nextFloat() <= e.getDropChance())
                                PlayerUtils.addToOrb(p, stack);
                        });
                        e.getDrops().clear();
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                });

                b.getBlock().harvestBlock(w, p, v, b, w.getTileEntity(v), p.getHeldItem(hand));

                if (eventCaughtStuff)
                {
                    w.destroyBlock(v, false);
                    if (e.getExpToDrop() > 0)
                        w.spawnEntity(new EntityXPOrb(w, p.posX, p.posY, p.posZ, e.getExpToDrop()));
                }
                SEEventHandler.hand_$eq(null);
            }
        }
        PlayerUtils.finishOrb(p);
    }
}
