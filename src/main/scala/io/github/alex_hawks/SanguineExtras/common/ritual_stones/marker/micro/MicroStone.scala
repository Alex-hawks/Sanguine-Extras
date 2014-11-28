package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import java.lang.Iterable
import scala.collection.JavaConversions
import scala.collection.mutable.Seq
import WayofTime.alchemicalWizardry.ModBlocks
import WayofTime.alchemicalWizardry.common.items.ScribeTool
import codechicken.lib.data.MCDataInput
import codechicken.lib.data.MCDataOutput
import codechicken.lib.vec.Cuboid6
import codechicken.lib.vec.Vector3
import codechicken.multipart.TCuboidPart
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition
import codechicken.multipart.TSlottedPart

object MicroStone {
  val centeredHalfCuboid = new Cuboid6(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)
}
class MicroStone(val name: String, var stoneType: Int = 0, val pickedItem: ItemStack) extends TCuboidPart with TSlottedPart {

  override def getBounds: Cuboid6 = MicroStone.centeredHalfCuboid
  override def getType: String = name
  override def pickItem(hit: MovingObjectPosition): ItemStack = pickedItem
  override def getDrops: Iterable[ItemStack] = JavaConversions.asJavaCollection(Seq[ItemStack](pickedItem))
  override def getSlotMask: Int = 1 << 6

  override def activate(player: EntityPlayer, hit: MovingObjectPosition, item: ItemStack): Boolean = {
    if (player.worldObj.isRemote)
      return true

    val playerItem = player.getCurrentEquippedItem();

    if (playerItem == null) {
      return false
    }

    val item = playerItem.getItem();

    if (!(item.isInstanceOf[ScribeTool])) {
      return false
    }

    if (playerItem.getMaxDamage() <= playerItem.getItemDamage() && !(playerItem.getMaxDamage() == 0)) {
      return true
    }

    val scribeTool = item.asInstanceOf[ScribeTool];

    if (!player.capabilities.isCreativeMode) {
      playerItem.setItemDamage(playerItem.getItemDamage() + 1);
    }

    stoneType = scribeTool.getType();
    sendDescUpdate
    return true
  }

  override def save(tag: NBTTagCompound) {
    tag.setInteger("stoneType", stoneType)
  }

  override def load(tag: NBTTagCompound) {
    stoneType = tag.getInteger("stoneType")
  }

  override def writeDesc(packet: MCDataOutput) {
    packet.writeInt(stoneType)
  }

  override def readDesc(packet: MCDataInput) {
    stoneType = packet.readInt()
  }

  override def renderStatic(pos: Vector3, pass: Int): Boolean = {
    val tessellator = Tessellator.instance;

    val (x, y, z) = (pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    val (minX, minY, minZ) = (x - 0.25, y - 0.25, z - 0.25)
    val (maxX, maxY, maxZ) = (x + 0.25, y + 0.25, z + 0.25)

    val textureMinU = ModBlocks.ritualStone.getIcon(0, stoneType).getMinU();
    val textureMaxU = ModBlocks.ritualStone.getIcon(0, stoneType).getMaxU();
    val textureMinV = ModBlocks.ritualStone.getIcon(0, stoneType).getMinV();
    val textureMaxV = ModBlocks.ritualStone.getIcon(0, stoneType).getMaxV();

    //            tessellator.setBrightness(.getMixedBrightnessForBlock(world, X, Y, Z));
    Minecraft.getMinecraft.renderEngine.bindTexture(Minecraft.getMinecraft.renderEngine.getResourceLocation(0))

    tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMaxV);

    tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(minX, minY, maxZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMaxV);

    tessellator.addVertexWithUV(maxX, minY, minZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(minX, minY, minZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMinU, textureMaxV);

    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMinU, textureMaxV);

    tessellator.addVertexWithUV(minX, maxY, maxZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(maxX, maxY, maxZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(maxX, maxY, minZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(minX, maxY, minZ, textureMinU, textureMaxV);

    tessellator.addVertexWithUV(minX, minY, minZ, textureMinU, textureMinV);
    tessellator.addVertexWithUV(maxX, minY, minZ, textureMaxU, textureMinV);
    tessellator.addVertexWithUV(maxX, minY, maxZ, textureMaxU, textureMaxV);
    tessellator.addVertexWithUV(minX, minY, maxZ, textureMinU, textureMaxV);

    return true;
  }
}