package io.github.alex_hawks.SanguineExtras.client.util

import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks
import WayofTime.bloodmagic.ritual.RitualComponent
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BlockRendererDispatcher, BufferBuilder, GlStateManager, Tessellator}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import io.github.alex_hawks.util.minecraft.common.Implicit.iBlockPos
import net.minecraft.util.EnumFacing

object Render {

  /**
    * Render code stole from [[net.minecraft.client.renderer.entity.RenderFallingBlock#doRender]]
    */
  def drawFakeBlock(state: IBlockState, min: (Double, Double, Double), w: World, v: BlockPos): Unit = {
    val (x, y, z) = min

    GlStateManager.pushMatrix()
    GlStateManager.translate(x, y, z)
    GlStateManager.disableLighting()

    val tessellator: Tessellator = Tessellator.getInstance
    val worldRenderer: BufferBuilder = tessellator.getBuffer
    worldRenderer.begin(7, DefaultVertexFormats.BLOCK)

    val i: Int = v.getX
    val j: Int = v.getY
    val k: Int = v.getZ

    worldRenderer.setTranslation(-i, -j, -k)

    val dispatcher: BlockRendererDispatcher = Minecraft.getMinecraft.getBlockRendererDispatcher
    val model: IBakedModel = dispatcher.getModelForState(state)

    dispatcher.getBlockModelRenderer.renderModel(w, model, state, v, worldRenderer, false)
    worldRenderer.setTranslation(0.0D, 0.0D, 0.0D)
    tessellator.draw()
    GlStateManager.enableLighting()
    GlStateManager.popMatrix()
  }

  def drawFakeRune(rune: RitualComponent)(implicit p: (Double, Double, Double), w: World, mrsLoc: BlockPos, dir: EnumFacing): Unit = {
    val v = mrsLoc + rune.getOffset(dir)
    val min = (v.getX - p._1, v.getY - p._2, v.getZ - p._3)

    if (!w.getBlockState(v).isOpaqueCube)
      Render.drawFakeBlock(RegistrarBloodMagicBlocks.RITUAL_STONE.getStateFromMeta(rune.getRuneType.ordinal()), min, w, v)
  }

}
