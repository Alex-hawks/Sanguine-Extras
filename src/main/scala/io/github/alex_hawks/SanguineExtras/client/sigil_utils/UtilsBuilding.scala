package io.github.alex_hawks.SanguineExtras.client.sigil_utils

import java.util.Set

import scala.collection.JavaConversions

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import io.github.alex_hawks.SanguineExtras.common.sigil_utils.{UtilsBuilding => ServerUtils}
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemBuilding
import io.github.alex_hawks.util.Vector3
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityClientPlayerMP
import net.minecraft.client.renderer.Tessellator
import net.minecraft.init.Blocks
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.util.ForgeDirection

class UtilsBuilding {

  @SubscribeEvent
  def render(e: RenderWorldLastEvent)
  {

    val mc: Minecraft = Minecraft.getMinecraft()
    val p: EntityClientPlayerMP = mc.thePlayer
    val w: World = p.worldObj
    val h = p.inventory.currentItem

    if ((p.inventory.mainInventory(h) != null) && ((p.inventory.mainInventory(h).getItem().isInstanceOf[ItemBuilding]))) {

      val b: Block = w.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ)
      val meta = w.getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ)
      if (b == null || b.equals(Blocks.air)) return
      val ls: Set[Vector3] = ServerUtils.getBlocksForBuild(w, new Vector3(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ), ForgeDirection.getOrientation(mc.objectMouseOver.sideHit), p, 9)

      val (px, py, pz) = (p.lastTickPosX + (p.posX - p.lastTickPosX) * e.partialTicks, p.lastTickPosY + (p.posY - p.lastTickPosY) * e.partialTicks, p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.partialTicks)

      Minecraft.getMinecraft.renderEngine.bindTexture(Minecraft.getMinecraft.renderEngine.getResourceLocation(0))
      
      for (v <- JavaConversions.asScalaSet[Vector3](ls)) {

        val (minX, minY, minZ) = (v.x - px, v.y - py, v.z - pz)
        val (maxX, maxY, maxZ) = (minX + 1, minY + 1, minZ + 1)

        val tessellator = Tessellator.instance

        tessellator.startDrawingQuads
        tessellator.setBrightness(b.getMixedBrightnessForBlock(w, v.x, v.y, v.z))

        var tex: (Float, Float, Float, Float) = null

        def textureMinU = tex._1
        def textureMaxU = tex._2
        def textureMinV = tex._3
        def textureMaxV = tex._4

        tex = g(b, meta, 0)
        tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMinV)
        tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMaxV)

        tex = g(b, meta, 1)
        tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMaxV)
        tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMinV)

        tex = g(b, meta, 2)
        tessellator.addVertexWithUV(maxX, minY, minZ, textureMinU, textureMaxV)
        tessellator.addVertexWithUV(minX, minY, minZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(minX, maxY, minZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(maxX, maxY, minZ, textureMinU, textureMinV)

        tex = g(b, meta, 3)
        tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMaxV)
        tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMinV)

        tex = g(b, meta, 4)
        tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMaxV)
        tessellator.addVertexWithUV(minX, minY, maxZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(minX, maxY, maxZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMinV)

        tex = g(b, meta, 5)
        tessellator.addVertexWithUV(maxX, minY, maxZ, textureMinU, textureMaxV)
        tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMaxV)
        tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMinV)
        tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMinU, textureMinV)

        tessellator.draw
      }
    }
  }

  def g(b: Block, m: Int, s: Int): (Float, Float, Float, Float) = {
    val i = b.getIcon(s, m)
    (i.getMinU(), i.getMaxU(), i.getMinV(), i.getMaxV())
  }
}
