package io.github.alex_hawks.SanguineExtras.common.ritual_stones.marker.micro

import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.slot.{EnumCenterSlot, IPartSlot}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}

object MicroStone extends IMultipart {
  override def getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase): IPartSlot = EnumCenterSlot.CENTER
  
  override def getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState): IPartSlot = EnumCenterSlot.CENTER
}
