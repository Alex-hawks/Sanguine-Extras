package io.github.alex_hawks.SanguineExtras.common.items.sigils;

import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.iface.ISigil;
import WayofTime.bloodmagic.item.ItemBindableBase;
import WayofTime.bloodmagic.util.helper.NBTHelper;
import WayofTime.bloodmagic.util.helper.TextHelper;
import io.github.alex_hawks.SanguineExtras.common.Constants;
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils;
import io.github.alex_hawks.SanguineExtras.common.util.PlayerUtils;
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab;
import io.github.alex_hawks.SanguineExtras.common.util.config.Base;
import io.github.alex_hawks.SanguineExtras.common.util.sigils.UtilsBuilding;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.alex_hawks.SanguineExtras.common.package$.MODULE$;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END;

public class ItemBuilding extends ItemBindableBase implements ISigil
{
    public static final String              ID              =       "sigil_building";
    public static final ResourceLocation    RL              = new   ResourceLocation(Constants.Metadata.MOD_ID, ID);
    public static final int                 TICKS_PER_OP    =       10;

    public ItemBuilding()
    {
        super();
        this.maxStackSize = 1;
        setCreativeTab(SanguineExtrasCreativeTab.Instance);
        this.setUnlocalizedName(ID);
        this.setRegistryName(RL);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(MODULE$.loreFormat() + TextHelper.localize("pun.se.sigil.building"));
//        tooltip.add("");

        NBTHelper.checkNBT(stack);
  
        Binding binding = getBinding(stack);
        if (binding != null)
            tooltip.add(TextHelper.localizeEffect("tooltip.bloodmagic.currentOwner", binding.getOwnerName()));
        else
            tooltip.add(TextHelper.localizeEffect("tooltip.se.owner.null"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        final Map<Integer, Set<BlockPos>> map = UtilsBuilding.getBlocksForBuild(w, pos, side, player, Constants.HardLimits.BUILDERS_SIGIL_COUNT, hand == EnumHand.MAIN_HAND);
        final Binding bind = BloodUtils.getOrBind(stack, player);

        MinecraftForge.EVENT_BUS.register(new Object()
        {
            Iterator<Map.Entry<Integer, Set<BlockPos>>> it = map.entrySet().iterator();
            int ticks = 0;

            PlaceEvent e;
            Block b;
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
                        t = w.getBlockState(pos);
                        s = new BlockSnapshot(w, v, t);
                        b = t.getBlock();
                        e = new PlaceEvent(s, t, player, hand);
                        if (!MinecraftForge.EVENT_BUS.post(e))
                        {
                            if (bind != null)
                            {
                                if (BloodUtils.drainSoulNetworkWithDamage(bind.getOwnerId(), Base.sigil.build.cost, player)
                                        && PlayerUtils.takeItem(player, new ItemStack(b, 1, b.getMetaFromState(w.getBlockState(pos))), stack))
                                {
                                    w.setBlockState(v, t, 0x3);
                                }
                            }
                        }
                    }

                    if (!it.hasNext())
                        MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });

        return EnumActionResult.SUCCESS;
    }
}
