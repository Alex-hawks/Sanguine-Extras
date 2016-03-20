package io.github.alex_hawks.SanguineExtras.common.sigils;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.iface.ISigil;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.api.util.helper.PlayerHelper;
import WayofTime.bloodmagic.item.ItemBindable;
import WayofTime.bloodmagic.util.helper.TextHelper;
import com.google.common.base.Strings;
import io.github.alex_hawks.SanguineExtras.common.SanguineExtras;
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.util.minecraft.common.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static io.github.alex_hawks.SanguineExtras.common.util.LangUtils.translate;

public class ItemBuilding extends ItemBindable implements ISigil
{
    public ItemBuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName("sigilBuilding");
        this.setRegistryName("sigilBuilding");
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List tooltip, boolean par4)
    {
        tooltip.add(MODULE$.loreFormat() + translate("pun.se.sigil.building"));
        tooltip.add("");

        NBTHelper.checkNBT(stack);

        if (!Strings.isNullOrEmpty(stack.getTagCompound().getString(Constants.NBT.OWNER_UUID)))
            tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentOwner", PlayerHelper.getUsernameFromStack(stack)));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        final Map<Integer, Set<Vector3>> map = UtilsBuilding.getBlocksForBuild(w, new Vector3(pos), side, player, 9);

        MinecraftForge.EVENT_BUS.register(new Object()
        {
            Iterator<Map.Entry<Integer, Set<Vector3>>> it = map.entrySet().iterator();
            int ticks = 0;

            PlaceEvent e;
            Block b;
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
                        t = w.getBlockState(pos);
                        s = new BlockSnapshot(w, v.toPos(), t);
                        b = t.getBlock();
                        e = new PlaceEvent(s, t, player);
                        if (!MinecraftForge.EVENT_BUS.post(e))
                        {
                            String str = ItemBuilding.this.getBindableOwner(stack);
                            if (str != null && !str.isEmpty())
                            {
                                if (BloodUtils.drainSoulNetworkWithDamage(UUID.fromString(ItemBuilding.this.getBindableOwner(stack)), player, SanguineExtras.rebuildSigilCost)
                                        && PlayerUtils.takeItem(player, new ItemStack(b, 1, b.getMetaFromState(w.getBlockState(pos)))))
                                {
                                    w.setBlockState(v.toPos(), t, 0x3);
                                }
                            }
                        }
                    }

                    if (!it.hasNext())
                        MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });

        return true;
    }
}
