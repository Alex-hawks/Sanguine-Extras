package io.github.alex_hawks.SanguineExtras.client.sigil_utils

import java.util.{Map => Jmap, Set => Jset}

import io.github.alex_hawks.SanguineExtras.client.util.Render
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.items.sigils.ItemBuilding
import io.github.alex_hawks.SanguineExtras.common.util.sigils.{UtilsBuilding => ServerUtils}
import io.github.alex_hawks.util.minecraft.common.Vector3
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.JavaConversions

class UtilsBuilding {

  @SubscribeEvent
  def render(e: RenderWorldLastEvent) {

    val mc: Minecraft = Minecraft.getMinecraft
    val p: EntityPlayerSP = mc.thePlayer
    val w: World = p.getEntityWorld
    val a = (p.inventory.getCurrentItem != null && p.inventory.getCurrentItem.getItem.isInstanceOf[ItemBuilding],
      p.inventory.offHandInventory(0) != null && p.inventory.offHandInventory(0).getItem.isInstanceOf[ItemBuilding])

    if (a._1 || a._2) {

      if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK)
        return
      val b: IBlockState = w.getBlockState(mc.objectMouseOver.getBlockPos)
      if (b == null || b.getBlock.equals(Blocks.AIR))
        return
      var ls: Jmap[Integer, Jset[Vector3]] = null
      if (a._1)
        ls = ServerUtils.getBlocksForBuild(w, new Vector3(mc.objectMouseOver.getBlockPos), mc.objectMouseOver.sideHit, p, Constants.HardLimits.BUILDERS_SIGIL_COUNT, true)
      else if (a._2)
        ls = ServerUtils.getBlocksForBuild(w, new Vector3(mc.objectMouseOver.getBlockPos), mc.objectMouseOver.sideHit, p, Constants.HardLimits.BUILDERS_SIGIL_COUNT, false)

      val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.getPartialTicks,
        p.lastTickPosY + (p.posY - p.lastTickPosY) * e.getPartialTicks,
        p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.getPartialTicks)

      Minecraft.getMinecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      for (e <- JavaConversions.asScalaSet[Jmap.Entry[Integer, Jset[Vector3]]](ls.entrySet())) {
        for (v <- JavaConversions.asScalaSet[Vector3](e.getValue)) {

          val min = (v.x - px, v.y - py, v.z - pz)

          Render.drawFakeBlock(b, min, w, new Vector3(mc.objectMouseOver.getBlockPos))
        }
      }
    }
  }
}
