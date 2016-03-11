package io.github.alex_hawks.SanguineExtras.client.constructs

import io.github.alex_hawks.SanguineExtras.common.constructs.Chest.textureLocGui
import io.github.alex_hawks.SanguineExtras.common.constructs.{ContainerChest, TileChest}
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object RenderChest extends {
  val size = 0.35f
  val model = new ModelChest
  val chestpng0 = new ResourceLocation("sanguineextras:textures/model/chest0.png")
  val chestpng1 = new ResourceLocation("sanguineextras:textures/model/chest1.png");
  //  TODO make my chest texture
  val chestpng2 = new ResourceLocation("sanguineextras:textures/model/chest2.png");
  //  TODO make my chest texture
  val chestpng3 = new ResourceLocation("sanguineextras:textures/model/chest3.png");
  //  TODO make my chest texture
  val chestpng4 = new ResourceLocation("sanguineextras:textures/model/chest4.png");
  //  TODO make my chest texture
  val chestpngVanilla = new ResourceLocation("textures/entity/chest/normal.png")
  val pixel = 0.0625f
} with TileEntitySpecialRenderer[TileChest] {
  override def renderTileEntityAt(ch: TileChest, X: Double, Y: Double, Z: Double, partialTickTime: Float, destroyStage: Int) = {

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

    bindTexture(ch.getBlockMetadata match {
      case 0 => chestpng0
      case 1 => chestpng0
      case 2 => chestpng0
      case 3 => chestpng0
      case 4 => chestpng0
      case _ => chestpngVanilla
    })

    //    Tessellator.getInstance().setBrightness(ModBlocks.Chest.getMixedBrightnessForBlock(te.getWorld, new BlockPos(X.toInt, Y.toInt, Z.toInt)))
    val f1: Float = ch.prevLidAngle + (ch.lidAngle - ch.prevLidAngle) * partialTickTime;

    model.chestLid.rotateAngleX = -(f1 * Math.PI / 2.0F).toFloat;
    model.renderAll

    GL11.glPopMatrix
  }
}

class GuiChest(val player: EntityPlayer, val chest: TileChest) extends GuiContainer(new ContainerChest(player, chest)) {
  xSize = 238;
  ySize = 256;

  override def drawGuiContainerBackgroundLayer(opacity: Float, x: Int, y: Int) = {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    this.mc.getTextureManager.bindTexture(textureLocGui)

    val (xStart, yStart) = ((width - xSize) / 2, (height - ySize) / 2)
    this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
  }
} 