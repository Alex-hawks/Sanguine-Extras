package io.github.alex_hawks.SanguineExtras.common
package items

import WayofTime.alchemicalWizardry.common.items.ItemRitualDiviner
import net.minecraft.item.Item
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable
import cpw.mods.fml.relauncher.SideOnly
import cpw.mods.fml.relauncher.Side
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import java.util.List
import net.minecraft.util.EnumChatFormatting
import io.github.alex_hawks.SanguineExtras.common.util.SanguineExtrasCreativeTab
import java.util.ArrayList
import scala.collection.JavaConversions
import net.minecraft.world.World
import WayofTime.alchemicalWizardry.common.items.EnergyItems
import WayofTime.alchemicalWizardry.common.tileEntity.TEMasterStone
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone
import WayofTime.alchemicalWizardry.api.rituals.Rituals
import scala.util.control.Breaks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.block.Block
import WayofTime.alchemicalWizardry.ModBlocks
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagInt
import WayofTime.alchemicalWizardry.api.rituals.IRitualStone
import net.minecraft.util.ChatComponentText
import WayofTime.alchemicalWizardry.api.rituals.ITileRitualStone
import cpw.mods.fml.common.registry.GameData

class AdvancedDiviner extends ItemRitualDiviner {
  this.maxStackSize = 1;
  this.setCreativeTab(SanguineExtrasCreativeTab.Instance)
  val customTag = "SanguineE"

  @SideOnly(Side.CLIENT)
  override def registerIcons(iconRegister: IIconRegister) {
    this.itemIcon = iconRegister.registerIcon("AlchemicalWizardry:RitualDiviner")
  }

  override def addInformation(par1ItemStack: ItemStack, par2EntityPlayer: EntityPlayer, rawLs: List[_], par4: Boolean) {
    val ls = rawLs.asInstanceOf[List[String]]
    val ls2 = new ArrayList[String]
    super.addInformation(par1ItemStack, par2EntityPlayer, ls2, par4)
    ls2.set(0, loreFormat + ls2.get(0))

    ls.addAll(ls2)
  }

  override def onItemUse(stack: ItemStack, par2EntityPlayer: EntityPlayer, par3World: World, par4: Int, par5: Int, par6: Int, par7: Int, par8: Float, par9: Float, par10: Float): Boolean =
    {
      val direction = this.getDirection(stack);

      EnergyItems.checkAndSetItemOwner(stack, par2EntityPlayer);
      val playerInventory = par2EntityPlayer.inventory.mainInventory;
      val tileEntity = par3World.getTileEntity(par4, par5, par6);
      val breaks1 = new Breaks
      val breaks2 = new Breaks

      if (tileEntity.isInstanceOf[IMasterRitualStone]) {
        val masterStone = tileEntity.asInstanceOf[IMasterRitualStone];
        val ritualList = Rituals.getRitualList(this.getCurrentRitual(stack));
        if (ritualList == null) {
          return false;
        }

        var playerInvRitualStoneLocation = -1;

        breaks1.breakable {
          for (i <- 0 until playerInventory.length) {
            breaks2.breakable {
              if (playerInventory(i) == null) {
                breaks2.break;
              }

              if (getRitualStone(stack).isItemEqual(playerInventory(i))) {
                playerInvRitualStoneLocation = i;
                breaks1.break;
              }
            }
          }
        }

        for (rc <- JavaConversions.asScalaIterator(ritualList.iterator())) {
          if (par3World.isAirBlock(par4 + rc.getX(direction), par5 + rc.getY(), par6 + rc.getZ(direction))) {
            if (playerInvRitualStoneLocation >= 0) {
              if (canPlaceStone(stack, rc.getStoneType)) {
                par3World.playAuxSFX(200, par4, par5 + 1, par6, 0);
                return true;
              }

              if (!par2EntityPlayer.capabilities.isCreativeMode) {
                par2EntityPlayer.inventory.decrStackSize(playerInvRitualStoneLocation, 1);
              }

              if (EnergyItems.syphonBatteries(stack, par2EntityPlayer, getEnergyUsed())) {
                par3World.setBlock(par4 + rc.getX(direction), par5 + rc.getY(), par6 + rc.getZ(direction), ModBlocks.ritualStone, rc.getStoneType(), 3);

                if (par3World.isRemote) {
                  par3World.playAuxSFX(2005, par4, par5 + 1, par6, 0);

                  return true;
                }
              }

              return true;
            }
          } else {
            val block = par3World.getBlock(par4 + rc.getX(direction), par5 + rc.getY(), par6 + rc.getZ(direction));

            if (block == ModBlocks.ritualStone) {
              val metadata = par3World.getBlockMetadata(par4 + rc.getX(direction), par5 + rc.getY(), par6 + rc.getZ(direction));

              if (metadata != rc.getStoneType()) {
                if (EnergyItems.syphonBatteries(stack, par2EntityPlayer, getEnergyUsed())) {
                  if (canPlaceStone(stack, rc.getStoneType)) {
                    par3World.playAuxSFX(200, par4, par5 + 1, par6, 0);
                    return true;
                  }

                  par3World.setBlockMetadataWithNotify(par4 + rc.getX(direction), par5 + rc.getY(), par6 + rc.getZ(direction), rc.getStoneType(), 3);
                  return true;
                }
              }
            } else {
              //              par3World.playAuxSFX(0000, par4, par5 + 1, par6, 0);
              return true;
            }
          }
        }
      } else if (!(par3World.getBlock(par4, par5, par6).isInstanceOf[IRitualStone] || par3World.getTileEntity(par4, par5, par6).isInstanceOf[ITileRitualStone])) {
        if (par3World.isRemote) {
          return false;
        }
        this.cycleDirection(stack);
        par2EntityPlayer.addChatComponentMessage(new ChatComponentText("Ritual tuned to face: " + this.getNameForDirection(this.getDirection(stack))));
      }

      return false;
    }

  def getRitualStone(stack: ItemStack): ItemStack = {
    var tag = stack.stackTagCompound.getCompoundTag(customTag)

    if (tag == null) {
      tag = new NBTTagCompound
      tag.setString("block", ModBlocks.ritualStone.getUnlocalizedName().substring(5))
      stack.stackTagCompound.setTag(customTag, tag)
    }

    def stone = getItemOrBlock(tag.getString("block")) // yes, this is meant to be a def. I understand what it means
    

    if (stone == null || stone == Blocks.air) {
      tag.setString("block", ModBlocks.ritualStone.getUnlocalizedName().substring(5))
      stack.stackTagCompound.setTag(customTag, tag)
    }

    new ItemStack(stone)
  }

  def canPlaceStone(stack: ItemStack, stone: Int): Boolean = {
    var tag = stack.stackTagCompound.getCompoundTag(customTag)
    var array: Array[Int] = tag.getIntArray("placeable")

    if (array.length == 0) {
      array = Array(0, 1, 2, 3, 4)
      tag.setIntArray("placeable", array)
    }

    for (x <- 0 until array.length)
      if (array(x) == stone)
        true

    false
  }

  def getItemOrBlock(name: String) = {
    val block = GameData.getBlockRegistry.getObject(name)
    val item = GameData.getItemRegistry.getObject(name)

    if (block == null)
      item
    block
  }
}