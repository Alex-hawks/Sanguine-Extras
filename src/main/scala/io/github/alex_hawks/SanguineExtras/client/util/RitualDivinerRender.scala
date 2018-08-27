package io.github.alex_hawks.SanguineExtras.client.util

import java.util.function.Consumer

import WayofTime.bloodmagic.item.ItemRitualDiviner
import WayofTime.bloodmagic.ritual.{IMasterRitualStone, RitualComponent}
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class RitualDivinerRender {

  @SubscribeEvent
  def render(e: RenderWorldLastEvent) {
    val mc: Minecraft = Minecraft.getMinecraft
    val p: EntityPlayerSP = mc.player
    val w: World = p.getEntityWorld

    if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK)
      return

    val te: TileEntity = w.getTileEntity(mc.objectMouseOver.getBlockPos)

    if (!te.isInstanceOf[IMasterRitualStone])
      return

    val v3 = mc.objectMouseOver.getBlockPos
    val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.getPartialTicks, p.lastTickPosY + (p.posY - p.lastTickPosY) * e.getPartialTicks, p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.getPartialTicks)

    if (p.inventory.getCurrentItem != null && p.inventory.getCurrentItem.getItem.isInstanceOf[ItemRitualDiviner]) {
      val diviner = p.inventory.getCurrentItem.getItem.asInstanceOf[ItemRitualDiviner]
      val dir = diviner.getDirection(p.inventory.getCurrentItem)

      val r = BloodUtils.getEffectFromString(diviner.getCurrentRitual(p.inventory.getCurrentItem))
      if (r == null)
        return

//      System.out.println(r)
      r.gatherComponents(new Consumer[RitualComponent] {
        override def accept(rune: RitualComponent): Unit = Render.drawFakeRune(rune)((px, py, pz), w, v3, dir)
      })
    }
  }
}
