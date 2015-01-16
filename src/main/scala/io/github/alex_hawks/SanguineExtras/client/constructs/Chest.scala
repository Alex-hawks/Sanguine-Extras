package io.github.alex_hawks.SanguineExtras.client.constructs

import org.lwjgl.opengl.GL11

import io.github.alex_hawks.SanguineExtras.client.constructs.RenderChest._
import io.github.alex_hawks.SanguineExtras.common.ModBlocks
import io.github.alex_hawks.SanguineExtras.common.constructs.Chest.textureLocGui
import io.github.alex_hawks.SanguineExtras.common.constructs.ContainerChest
import io.github.alex_hawks.SanguineExtras.common.constructs.TileChest
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

object RenderChest {
  val size = 0.35f
  val model = new ModelChest
  val chestpng = new ResourceLocation("textures/entity/chest/normal.png");  //  TODO make my chest texture
  val pixel = 0.0625f
}

class RenderChest extends TileEntitySpecialRenderer {

  override def renderTileEntityAt(te: TileEntity, X: Double, Y: Double, Z: Double, partialTickTime: Float) = {
    val ch = te.asInstanceOf[TileChest]

    GL11.glPushMatrix
    GL11.glTranslated(X + 0.5, Y + 0.625 + ch.height, Z + 0.5)
    GL11.glRotatef(ch.rotation, 0, 1, 0)
    GL11.glScalef(size, size, size)
    GL11.glRotatef(180, 1, 0, 1) // the chest is rendered upside down
    
    GL11.glTranslated(-(pixel + size), 0, -(pixel + size))

    val x = 0
    val y = 0
    val z = 0

    val minX = x - size
    val maxX = x + size
    val minY = y - size
    val maxY = y + size
    val minZ = z - size
    val maxZ = z + size

    this.bindTexture(chestpng)

    Tessellator.instance.setBrightness(ModBlocks.Chest.getMixedBrightnessForBlock(te.getWorldObj, X.toInt, Y.toInt, Z.toInt))
    val f1: Float = ch.prevLidAngle + (ch.lidAngle - ch.prevLidAngle) * partialTickTime;

    model.chestLid.rotateAngleX = -(f1 * Math.PI / 2.0F).toFloat;
    model.renderAll

    GL11.glPopMatrix
  }
}

class GuiChest(val player: InventoryPlayer, val chest: TileChest) extends GuiContainer(new ContainerChest(player, chest)) {
  xSize = 238;
  ySize = 256;

  override def drawGuiContainerBackgroundLayer(opacity: Float, x: Int, y: Int) = {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    this.mc.getTextureManager.bindTexture(textureLocGui)

    val (xStart, yStart) = ((width - xSize) / 2, (height - ySize) / 2)
    this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
  }
} 