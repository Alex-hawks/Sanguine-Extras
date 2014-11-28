package io.github.alex_hawks.SanguineExtras.client.ritual_stones.marker.micro

import net.minecraftforge.client.IItemRenderer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRenderType._
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import WayofTime.alchemicalWizardry.ModBlocks
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.Minecraft
import io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro.ItemMicroRitualStone

class RenderRitualStoneCube(val halfSize: Double = 0.25) extends IItemRenderer {

  def this() = {
    this(0.25)
  }
  override def handleRenderType(item: ItemStack, rtype: ItemRenderType): Boolean = {
    return rtype != FIRST_PERSON_MAP
  }

  override def shouldUseRenderHelper(rtype: ItemRenderType, item: ItemStack, helper: ItemRendererHelper): Boolean = {
    return rtype != FIRST_PERSON_MAP
  }

  override def renderItem(rtype: ItemRenderType, item: ItemStack, data: Object*): Unit = {
    val (zero, half) = ((0.0, 0.0, 0.0), (0.5, 0.5, 0.5))

    val tessellator = Tessellator.instance
    val (x, y, z) = rtype match {
      case ENTITY => zero
      case EQUIPPED => half
      case EQUIPPED_FIRST_PERSON => half
      case FIRST_PERSON_MAP => null
      case INVENTORY => half
    }

    val (minX, minY, minZ) = (x - halfSize, y - halfSize, z - halfSize)
    val (maxX, maxY, maxZ) = (x + halfSize, y + halfSize, z + halfSize)

    val textureMinU = ModBlocks.ritualStone.getBlockTextureFromSide(0).getMinU()
    val textureMaxU = ModBlocks.ritualStone.getBlockTextureFromSide(0).getMaxU()
    val textureMinV = ModBlocks.ritualStone.getBlockTextureFromSide(0).getMinV()
    val textureMaxV = ModBlocks.ritualStone.getBlockTextureFromSide(0).getMaxV()

    Minecraft.getMinecraft.renderEngine.bindTexture(Minecraft.getMinecraft.renderEngine.getResourceLocation(0))

    tessellator.startDrawingQuads

    tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMaxV)

    tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(minX, minY, maxZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMaxV)

    tessellator.addVertexWithUV(maxX, minY, minZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(minX, minY, minZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMinU, textureMaxV)

    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMinU, textureMaxV)

    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMaxV)

    tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMinV)
    tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMinV)
    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMaxV)
    tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMaxV)

    tessellator.draw
  }
}