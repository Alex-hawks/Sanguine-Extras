package io.github.alex_hawks.SanguineExtras.common.ritual_stones.advanced_master;

import io.github.alex_hawks.SanguineExtras.api.ritual.InteractableRitualEffect;

import java.lang.reflect.Field;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AMRSHandler
{
	private static Field effect;

	static
	{
		try
		{
			effect = Rituals.class.getDeclaredField("effect");
			effect.setAccessible(true);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerClick(PlayerInteractEvent e) 
	{
		if (e.useBlock != Result.ALLOW)
			return;
		try
		{
			TileEntity te = e.world.getTileEntity(e.x, e.y, e.z);

			if (!(te instanceof TEAdvancedMasterStone))
				return;

			Rituals ritual = Rituals.ritualMap.get(((TEAdvancedMasterStone) te).getCurrentRitual());

			if (ritual == null)
				return;

			RitualEffect E = (RitualEffect) effect.get(ritual);

			if (E instanceof InteractableRitualEffect)
			{
				if (e.action == Action.LEFT_CLICK_BLOCK)
					((InteractableRitualEffect) E).onLeftClick((TEAdvancedMasterStone) te);
				if (e.action == Action.RIGHT_CLICK_BLOCK)
					((InteractableRitualEffect) E).onRightClick((TEAdvancedMasterStone) te);
			}
		} catch (SecurityException e1)
		{
			e1.printStackTrace();
		} catch (IllegalArgumentException e1)
		{
			e1.printStackTrace();
		} catch (IllegalAccessException e1)
		{
			e1.printStackTrace();
		}
	}

	public static void onColide(TEAdvancedMasterStone mrs, Entity ent)
	{
		try
		{
			if (mrs == null)
				return;

			Rituals ritual = Rituals.ritualMap.get(mrs.getCurrentRitual());

			if (ritual == null)
				return;

			RitualEffect E = (RitualEffect) effect.get(ritual);

			if (E instanceof InteractableRitualEffect)
			{
				((InteractableRitualEffect) E).onColideWith(mrs, ent);
			}
		} catch (SecurityException e1)
		{
			e1.printStackTrace();
		} catch (IllegalArgumentException e1)
		{
			e1.printStackTrace();
		} catch (IllegalAccessException e1)
		{
			e1.printStackTrace();
		}
	}
}
