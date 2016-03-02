package io.github.alex_hawks.SanguineExtras.client.handler

import WayofTime.bloodmagic.api.ritual.{IMasterRitualStone, RitualComponent}
import WayofTime.bloodmagic.item.ItemRitualDiviner
import WayofTime.bloodmagic.registry.ModBlocks
import io.github.alex_hawks.SanguineExtras.client.util.Render
import io.github.alex_hawks.SanguineExtras.common.util.BloodUtils
import io.github.alex_hawks.util.Vector3
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.JavaConversions

class RitualDivinerRender {

  @SubscribeEvent
  def render(e: RenderWorldLastEvent) {
    val mc: Minecraft = Minecraft.getMinecraft
    val p: EntityPlayerSP = mc.thePlayer
    val w: World = p.getEntityWorld

    if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
      return

    val te: TileEntity = w.getTileEntity(mc.objectMouseOver.getBlockPos)

    if (!te.isInstanceOf[IMasterRitualStone])
      return

    val v3 = new Vector3(mc.objectMouseOver.getBlockPos)
    val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.partialTicks, p.lastTickPosY + (p.posY - p.lastTickPosY) * e.partialTicks, p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.partialTicks)

    if (p.inventory.getCurrentItem != null && p.inventory.getCurrentItem.getItem.isInstanceOf[ItemRitualDiviner]) {
      val d = p.inventory.getCurrentItem.getItem.asInstanceOf[ItemRitualDiviner]
      val dir = d.getDirection(p.inventory.getCurrentItem)

      val r = BloodUtils.getEffectFromString(d.getCurrentRitual(p.inventory.getCurrentItem))
      if (r == null)
        return

      System.out.println(r)

      for (x <- JavaConversions.asScalaBuffer[RitualComponent](r.getComponents)) {
        val v: Vector3 = v3 + (new Vector3(x.getOffset))
        val min = (v.x - px, v.y - py, v.z - pz)

        if (!w.getBlockState(v.toPos).getBlock.isOpaqueCube)
          Render.drawFakeBlock(ModBlocks.ritualStone.getStateFromMeta(x.getRuneType.ordinal()), min, w, v3)
      }
    }
  }
}
