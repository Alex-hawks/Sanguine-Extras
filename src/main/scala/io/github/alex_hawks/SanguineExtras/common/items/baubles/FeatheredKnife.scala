package io.github.alex_hawks.SanguineExtras.common.items.baubles

import WayofTime.bloodmagic.ConfigHandler
import WayofTime.bloodmagic.api.BloodMagicAPI
import WayofTime.bloodmagic.api.event.SacrificeKnifeUsedEvent
import WayofTime.bloodmagic.api.util.helper.PlayerSacrificeHelper
import baubles.api.BaubleType
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge

object FeatheredKnife extends BaubleBase {
  this.maxStackSize = 1
  setCreativeTab(SanguineExtrasCreativeTab.Instance)
  this.setUnlocalizedName("baubleFeatheredKnife")
  this.setRegistryName("baubleFeatheredKnife")

  override def getBaubleType(itemStack: ItemStack): BaubleType = BaubleType.CHARM


  override def onWornTick(stack: ItemStack, ent: EntityLivingBase): Unit = {
    if (ent.worldObj.getWorldTime % 20 == 0) {
      if (ent.isInstanceOf[EntityPlayer]) {
        val player = ent.asInstanceOf[EntityPlayer]

        var lpAdded: Int = ConfigHandler.sacrificialDaggerConversion

        if (!player.capabilities.isCreativeMode && player.getHealth > 5) {
          val evt: SacrificeKnifeUsedEvent = new SacrificeKnifeUsedEvent(player, true, true, ConfigHandler.sacrificialDaggerDamage, lpAdded)
          if (MinecraftForge.EVENT_BUS.post(evt))
            return
          lpAdded = evt.lpAdded
          if (evt.shouldDrainHealth && evt.shouldFillAltar && PlayerSacrificeHelper.findAndFillAltar(player.worldObj, player, lpAdded, false)) {
            player.setHealth(Math.max(player.getHealth - 1, 0.0001f))
            if (player.getHealth <= 0.001f) {
              player.onDeath(BloodMagicAPI.getDamageSource)
              player.setHealth(0)
            }
          }
        }
      }
    }
  }
}