package io.github.alex_hawks.SanguineExtras.common.sigil_utils;

import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.putItem;
import static io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils.takeItem;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.util.Vector3;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import cpw.mods.fml.common.FMLCommonHandler;

public class UtilsRebuilding
{
    public static List<Vector3> find(int x, int y, int z, World world)
    {
        List<Vector3> solidBlocks = new ArrayList<Vector3>();
        List<Vector3> airBlocks = new ArrayList<Vector3>();
        List<Vector3> currentSolidBlocks = new ArrayList<Vector3>();
        List<Vector3> currentairBlocks = new ArrayList<Vector3>();

        List<Vector3> toReturn = new ArrayList<Vector3>();

        Block b;

        Block original = world.getBlock(x, y, z);
        solidBlocks.add(new Vector3(x, y, z));

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
        {
            b = world.getBlock(x + d.offsetX, y + d.offsetY, z + d.offsetZ);

            if (!b.isBlockSolid(world, x, y, z, world.getBlockMetadata(x, y, z)) && !b.equals(original))
            {
                airBlocks.add(new Vector3(x + d.offsetX, y + d.offsetY, z + d.offsetZ));
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
                for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
                {
                    b = world.getBlock(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ);

                    if (b.isBlockSolid(world, x, y, z, world.getBlockMetadata(x, y, z)) && b.equals(original))
                    {
                        solidBlocks.add(new Vector3(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ));
                    }
                    else
                    {
                        airBlocks.add(new Vector3(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ));
                    }
                }
            }
            for (Vector3 v : currentairBlocks)
            {
                for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
                {
                    b = world.getBlock(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ);

                    if (!b.isBlockSolid(world, x, y, z, world.getBlockMetadata(x, y, z)) && !b.equals(original))
                    {
                        airBlocks.add(new Vector3(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ));
                    }
                }
            }
        }

        Vector3 vec3;
        block: for(Vector3 v : currentSolidBlocks)
        {
            for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
            {
                for (Vector3 air : airBlocks)
                {
                    vec3 = new Vector3(v.x + d.offsetX, v.y + d.offsetY, v.z + d.offsetZ);

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
        return find(coords.x, coords.y, coords.z, w);
    }

    public static void doReplace(EntityPlayer player, String sigilOwner, List<Vector3> list, World w, Block oldBlock, int oldMeta, Block newBlock, int newMeta)
    {
        doReplace(player, sigilOwner, list.toArray(new Vector3[0]), w, oldBlock, oldMeta, newBlock, newMeta);
    }
    
    public static void doReplace(EntityPlayer player, String sigilOwner, Vector3[] list, World w, Block oldBlock, int oldMeta, Block newBlock, int newMeta)
    {
        BreakEvent e;
        PlaceEvent e2;
        BlockSnapshot s;
        for (Vector3 v : list)
        {
            if (w.getBlock(v.x, v.y, v.z).equals(oldBlock) && w.getBlockMetadata(v.x, v.y, v.z) == oldMeta)
            {
                e = new BreakEvent(v.x, v.y, v.z, w, oldBlock, oldMeta, player);
                s = new BlockSnapshot(w, v.x, v.y, v.z, newBlock, newMeta);
                e2 = new PlaceEvent(s, null, player);
                if (!FMLCommonHandler.instance().bus().post(e))
                {
                    if (!FMLCommonHandler.instance().bus().post(e2))
                    {
                        if (BloodUtils.drainSoulNetwork(sigilOwner, SanguineExtras.rebuildSigilCost) && takeItem(player, new ItemStack(newBlock, 1, newMeta)))
                        {
                            putItem(player, oldBlock.getDrops(w, v.x, v.y, v.z, oldMeta, 0).toArray(new ItemStack[0]));
                            w.setBlock(v.x, v.y, v.z, newBlock, newMeta, 0x3);

                            if (e.getExpToDrop() > 0)
                                w.spawnEntityInWorld(new EntityXPOrb(w, player.posX, player.posY, player.posZ, e.getExpToDrop()));
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
        }
    }
}
