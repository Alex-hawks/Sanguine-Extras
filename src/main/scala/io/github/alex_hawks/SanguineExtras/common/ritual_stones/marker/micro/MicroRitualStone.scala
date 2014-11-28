package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import codechicken.multipart.TCuboidPart
import net.minecraft.world.World
import codechicken.lib.vec.Cuboid6
import WayofTime.alchemicalWizardry.api.rituals.IRitualStone
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovingObjectPosition
import WayofTime.alchemicalWizardry.common.items.ScribeTool
import codechicken.multipart.TSlottedPart
import io.github.alex_hawks.SanguineExtras.common.ModItems
import net.minecraft.nbt.NBTTagCompound
import codechicken.lib.data.MCDataOutput
import codechicken.lib.data.MCDataInput
import WayofTime.alchemicalWizardry.api.rituals.ITileRitualStone
import codechicken.lib.vec.Vector3
import net.minecraft.client.renderer.Tessellator
import WayofTime.alchemicalWizardry.ModBlocks

class MicroRitualStone(stoneType: Int = 0) extends MicroStone("MicroRitualStone", stoneType, new ItemStack(ModItems.MicroRitualStone)) with ITileRitualStone {

  override def isRuneType(runeType: Int): Boolean = {
    return stoneType == runeType
  }
}