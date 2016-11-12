package io.github.alex_hawks.util.minecraft.common

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}

class Vector3(val x: Int, val y: Int, val z: Int) {

  def this(pos: BlockPos) = this(pos.getX, pos.getY, pos.getZ)

  def +(w: (Int, Int, Int)): Vector3 = new Vector3(x + w._1, y + w._2, z + w._3)

  def +(that: Vector3): Vector3 = new Vector3(this.x + that.x, this.y + that.y, this.z + that.z)

  override def toString = s"V3(${x},${y},${z})"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Vector3]

  override def equals(other: Any): Boolean = other match {
    case that: Vector3 =>
      (that canEqual this) &&
        x == that.x &&
        y == that.y &&
        z == that.z
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(x, y, z)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  def toPos: BlockPos = new BlockPos(this.x, this.y, this.z)

  def shift(dir: EnumFacing): Vector3 = this +(dir.getFrontOffsetX, dir.getFrontOffsetY, dir.getFrontOffsetZ)

  def shiftAABB(aabb: AxisAlignedBB):AxisAlignedBB = {
    import aabb._
    new AxisAlignedBB(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z)
  }


}
