package io.github.alex_hawks.SanguineExtras.client.sigil_utils

import java.util.{Map => Jmap, Set => Jset}

import io.github.alex_hawks.SanguineExtras.client.util.Render
import io.github.alex_hawks.SanguineExtras.common.Constants
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.{UtilsBuilding => ServerUtils}
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemBuilding
import io.github.alex_hawks.util.minecraft.common.Vector3
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.init.Blocks
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

    if (p.inventory.getCurrentItem != null && p.inventory.getCurrentItem.getItem.isInstanceOf[ItemBuilding]) {

      val b: IBlockState = w.getBlockState(mc.objectMouseOver.getBlockPos)
      if (b == null || b.getBlock.equals(Blocks.air))
        return
      val ls: Jmap[Integer, Jset[Vector3]] = ServerUtils.getBlocksForBuild(w, new Vector3(mc.objectMouseOver.getBlockPos), mc.objectMouseOver.sideHit, p, Constants.HardLimits.BUILDERS_SIGIL_COUNT)

      val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.partialTicks,
        p.lastTickPosY + (p.posY - p.lastTickPosY) * e.partialTicks,
        p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.partialTicks)

      Minecraft.getMinecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

      for (e <- JavaConversions.asScalaSet[Jmap.Entry[Integer, Jset[Vector3]]](ls.entrySet())) {
        for (v <- JavaConversions.asScalaSet[Vector3](e.getValue)) {

          val min = (v.x - px, v.y - py, v.z - pz)

          Render.drawFakeBlock(b, min, w, new Vector3(mc.objectMouseOver.getBlockPos))
        }
      }
    }
  }
}
