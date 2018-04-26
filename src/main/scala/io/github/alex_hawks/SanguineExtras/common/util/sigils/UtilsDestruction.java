package io.github.alex_hawks.SanguineExtras.common.util.sigils;

import WayofTime.bloodmagic.demonAura.WorldDemonWillHandler;
import WayofTime.bloodmagic.ritual.types.RitualCrushing;
import WayofTime.bloodmagic.soul.EnumDemonWillType;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.BreakContext;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.ArrayList;
import java.util.List;

public class UtilsDestruction
{
    private static double steadfastWillDrain   = RitualCrushing.steadfastWillDrain;
    private static double destructiveWillDrain = RitualCrushing.destructiveWillDrain;

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

    public static void doDrops(EntityPlayer p, String sigilOwner, List<BlockPos> list, World w, BreakContext context)
    {
        PlayerUtils.startOrb(p);
        int blocks = 0;

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
                if (BloodUtils.drainSoulNetwork(sigilOwner, ++blocks, p))
                {
                    // Corrosive is Cutting Fluid, Steadfast for Silky, Destructive for Fortune
                    double corrosiveWill    = WorldDemonWillHandler.getCurrentWill(w, v, EnumDemonWillType.CORROSIVE);
                    double steadfastWill    = WorldDemonWillHandler.getCurrentWill(w, v, EnumDemonWillType.STEADFAST);
                    double destructiveWill  = WorldDemonWillHandler.getCurrentWill(w, v, EnumDemonWillType.DESTRUCTIVE);

                    if(context.crusher)
                    {
                        //TODO add this feature, run the drops through the cutting fluid handler consuming the required will et al from the player's inventory
                        //No-op
                    }
                    if (context.silk_touch && steadfastWill >= steadfastWillDrain && b.getBlock().canSilkHarvest(w, v, b, p))
                    {
                        ItemStack drop = b.getBlock().getItem(w, v, b); // TODO use an AccessTransformer to make the commented out call below valid and use it instead
//                        List<ItemStack> drops = b.getBlock().getSilkTouchDrop(b);

                        if (!drop.isEmpty())
                        {
                            w.destroyBlock(v, false);

                            PlayerUtils.addToOrb(p, drop);
                            if (e.getExpToDrop() > 0) // in case someone changed it
                                w.spawnEntity(new EntityXPOrb(w, p.posX, p.posY, p.posZ, e.getExpToDrop()));

                            WorldDemonWillHandler.drainWill(w, v, EnumDemonWillType.STEADFAST, steadfastWillDrain, true);
                            continue;
                        }
                    }
                    if (context.fortune > 0 && destructiveWill >= destructiveWillDrain)
                    {
                        NonNullList<ItemStack> drops = NonNullList.create();
                        b.getBlock().getDrops(drops, w, v, b, context.fortune);
                        if (!drops.isEmpty())
                        {
                            w.destroyBlock(v, false);

                            for (ItemStack drop : drops)
                            {
                                PlayerUtils.addToOrb(p, drop);
                                if (e.getExpToDrop() > 0)
                                    w.spawnEntity(new EntityXPOrb(w, p.posX, p.posY, p.posZ, e.getExpToDrop()));
                            }

                            WorldDemonWillHandler.drainWill(w, v, EnumDemonWillType.DESTRUCTIVE, destructiveWillDrain, true);
                            continue;
                        }
                    }

                    NonNullList<ItemStack> drops = NonNullList.create();
                    b.getBlock().getDrops(drops, w, v, b, 0);
                    w.destroyBlock(v, false);

                    for (ItemStack drop : drops)
                    {
                        PlayerUtils.addToOrb(p, drop);
                        if (e.getExpToDrop() > 0)
                            w.spawnEntity(new EntityXPOrb(w, p.posX, p.posY, p.posZ, e.getExpToDrop()));
                    }
                }
            }
        }
        PlayerUtils.finishOrb(p);
    }
}
