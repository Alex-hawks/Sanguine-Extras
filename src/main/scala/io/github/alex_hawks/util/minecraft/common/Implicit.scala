package io.github.alex_hawks.util.minecraft.common

import com.google.gson.JsonObject
import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack => IS}
import net.minecraft.nbt._
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos => BP}
import scala.collection.JavaConversions._

import language.implicitConversions

object Implicit {
  object Helper {
    def createUnnamedTag(data: Any): NBTBase = data match {
      case x: Byte =>
        new NBTTagByte(x)
      case x: Short =>
        new NBTTagShort(x)
      case x: Int =>
        new NBTTagInt(x)
      case x: Long =>
        new NBTTagLong(x)
      case x: Float =>
        new NBTTagFloat(x)
      case x: Double =>
        new NBTTagDouble(x)
      case x: Enum[_] =>
        new NBTTagInt(x.ordinal())

      case x: Array[Byte] =>
        new NBTTagByteArray(x)
      case x: Array[Int] =>
        new NBTTagIntArray(x)
      case x: Array[Long] =>
        new NBTTagLongArray(x)
      case x: Array[_] =>
        val a = new NBTTagList
        for(y <- x)
          a.appendTag(createUnnamedTag(y))
        a

      case x: String =>
          new NBTTagString(x)
      case x: StringBuilder =>
        new NBTTagString(x.mkString)

      case x: Seq[_] =>
        val a = new NBTTagList
        for(y <- x)
          a.appendTag(createUnnamedTag(y))
        a
      case x: Set[_] =>
        val a = new NBTTagList
        for(y <- x)
          a.appendTag(createUnnamedTag(y))
        a

      case x: Map[_,_] =>
        val a = new NBTTagCompound
        for((k,v) <- x)
          a.setTag(k.toString, createUnnamedTag(v))
        a

      case x: IS =>
        x.writeToNBT(new NBTTagCompound)
    }

    def fromString[E](key: String, data: E): NBTTagCompound = {
      var tag = new NBTTagCompound
      var _key: String = ""

      if (key.contains("::")) {
        val keys = key.split("::")
        _key = keys(keys.length - 1)
        for(i <- 0 until keys.length - 1)
          tag.setTag(keys(i), {tag = new NBTTagCompound; tag})
      }
      else
        _key = key

      tag.setTag(_key, Helper.createUnnamedTag(data))
      tag
    }

    implicit def fromJson(json: JsonObject): NBTTagCompound = {
      if (json.has("key") && json.has("data") && json.size == 2) {
        val data = json.get("data").getAsJsonPrimitive
        var _data: Any = null

        data match {
          case _ if data.isBoolean  => _data = data.getAsBoolean
          case _ if data.isNumber   => _data = data.getAsNumber
          case _ if data.isString   => _data = data.getAsString
        }
        fromString(json.get("key").getAsString, _data)
      }
      else {
        val tag = new NBTTagCompound

        for (entry <- json.entrySet) {
          val data = entry.getValue.getAsJsonPrimitive
          var _data: Any = null

          data match {
            case _ if data.isBoolean  => _data = data.getAsBoolean
            case _ if data.isNumber   => _data = data.getAsNumber
            case _ if data.isString   => _data = data.getAsString
          }

          tag.merge(fromString(entry.getKey, _data))
        }
        tag
      }
    }
  }

  implicit class item(i: Item) extends iItemStack(new IS(i))
  implicit class block(b: Block) extends iItemStack(new IS(b))

  implicit class iItemStack(s: IS) {
    def apply(): IS = s

    def copyWithCount(x: Int): IS = {
      val y = s.copy
      y.setCount(x)
      y
    }

    def withNBT[E](key: String, data: E): IS = {
      if (!s.hasTagCompound)
        s.setTagCompound(new NBTTagCompound)

      var tag: NBTTagCompound = s.getTagCompound
      var _key: String = ""
      if (key.contains("::")) {
        val keys = key.split("::")
        _key = keys(keys.length - 1)
        for(i <- 0 until keys.length - 1)
          tag.setTag(keys(i), {tag = new NBTTagCompound; tag})
      }
      else
        _key = key

      tag.setTag(_key, Helper.createUnnamedTag(data))
      s
    }

    def withMoreNBT[E](key: String, data: E*): IS = {
      if (!s.hasTagCompound)
        s.setTagCompound(new NBTTagCompound)

      var tag: NBTTagCompound = s.getTagCompound
      var _key: String = ""
      if (key.contains("::")) {
        val keys = key.split("::")
        _key = keys(keys.length - 1)
        for(i <- 0 until keys.length - 1)
          tag.setTag(keys(i), {tag = new NBTTagCompound; tag})
      }
      else
        _key = key

      tag.setTag(_key, Helper.createUnnamedTag(data))
      s
    }
  }

  implicit class iBlockPos(p: BP) {

    def +(w: (Int, Int, Int)) = new BP(p.getX + w._1, p.getY + w._2, p.getZ + w._3)

    def +(x: Int, y: Int, z: Int) = new BP(p.getX + x, p.getY + y, p.getZ + z)

    def +(that: BP) = new BP(p.getX + that.getX, p.getY + that.getY, p.getZ + that.getZ)

    def shift(dir: EnumFacing) = p +(dir.getFrontOffsetX, dir.getFrontOffsetY, dir.getFrontOffsetZ)

    def shiftAABB(aabb: AxisAlignedBB): AxisAlignedBB = {
      import aabb._
      new AxisAlignedBB(minX + p.getX, minY + p.getY, minZ + p.getZ, maxX + p.getX, maxY + p.getY, maxZ + p.getZ)
    }
  }
}
