package io.github.alex_hawks.SanguineExtras.client.util

import io.github.alex_hawks.util.minecraft.common.Vector3
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BlockRendererDispatcher, GlStateManager, Tessellator, VertexBuffer}
import net.minecraft.world.World

object Render {

  /**
    * Render code stole from [[net.minecraft.client.renderer.entity.RenderFallingBlock#doRender]]
    */
  def drawFakeBlock(state: IBlockState, min: (Double, Double, Double), w: World, v: Vector3) = {
    val (x, y, z) = min
    GlStateManager.pushMatrix
    GlStateManager.translate(x, y, z)
    GlStateManager.disableLighting
    val tessellator: Tessellator = Tessellator.getInstance
    val worldrenderer: VertexBuffer = tessellator.getBuffer
    worldrenderer.begin(7, DefaultVertexFormats.BLOCK)
    val i: Int = v.x
    val j: Int = v.y
    val k: Int = v.z
    worldrenderer.setTranslation(-i, -j, -k)
    val dispatcher: BlockRendererDispatcher = Minecraft.getMinecraft.getBlockRendererDispatcher
    val model: IBakedModel = dispatcher.getModelForState(state)
    dispatcher.getBlockModelRenderer.renderModel(w, model, state, v.toPos, worldrenderer, false)
    worldrenderer.setTranslation(0.0D, 0.0D, 0.0D)
    tessellator.draw
    GlStateManager.enableLighting
    GlStateManager.popMatrix
  }

}
