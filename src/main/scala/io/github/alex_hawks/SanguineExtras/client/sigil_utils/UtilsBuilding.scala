package io.github.alex_hawks.SanguineExtras.client.sigil_utils

import java.util.Set

import scala.collection.JavaConversions

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.{UtilsBuilding => ServerUtils}
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemBuilding
import io.github.alex_hawks.util.Vector3
import io.github.alex_hawks.SanguineExtras.client.util.Render
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityClientPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.util.ForgeDirection

class UtilsBuilding {

  @SubscribeEvent
  def render(e: RenderWorldLastEvent)
  {

    val mc: Minecraft = Minecraft.getMinecraft
    val p: EntityClientPlayerMP = mc.thePlayer
    val w: World = p.worldObj

    if (p.inventory.getCurrentItem != null && p.inventory.getCurrentItem.getItem.isInstanceOf[ItemBuilding]) {

      val b: Block = w.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ)
      val meta = w.getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ)
      if (b == null || b.equals(Blocks.air)) return
      val ls: Set[Vector3] = ServerUtils.getBlocksForBuild(w, new Vector3(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ), ForgeDirection.getOrientation(mc.objectMouseOver.sideHit), p, 9)

      val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.partialTicks, p.lastTickPosY + (p.posY - p.lastTickPosY) * e.partialTicks, p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.partialTicks)

      Minecraft.getMinecraft.renderEngine.bindTexture(Minecraft.getMinecraft.renderEngine.getResourceLocation(0))
      
      for (v <- JavaConversions.asScalaSet[Vector3](ls)) {

        val min = (v.x - px, v.y - py, v.z - pz)

        Render.drawFakeBlock(v, b, meta, min, w)

      }
    }
  }
}
