package io.github.alex_hawks.SanguineExtras.client.sigil_utils;

import io.github.alex_hawks.SanguineExtras.common.sigil_utils.UtilsBuilding;
import io.github.alex_hawks.SanguineExtras.common.sigils.ItemBuilding;
import io.github.alex_hawks.util.Vector3;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ClientUtilsBuilding
{
    int tex = FMLClientHandler.instance().getClient().renderEngine.getTexture(TextureMap.locationBlocksTexture).getGlTextureId();
    
//    @SubscribeEvent
    public void render(DrawBlockHighlightEvent e)   //  TODO: Fix this rendering...
    {
        if ((e.currentItem != null) && ((e.currentItem.getItem() instanceof ItemBuilding)))
        {
            Minecraft mc = Minecraft.getMinecraft();
            EntityClientPlayerMP p = mc.thePlayer;
            World w = p.worldObj;
            
            Block b = w.getBlock(e.target.blockX, e.target.blockY, e.target.blockZ);
            Set<Vector3> ls = UtilsBuilding.getBlocksForBuild(w, new Vector3(e.target.blockX, e.target.blockY, e.target.blockZ), ForgeDirection.getOrientation(e.target.sideHit), p, 9);
            
            GL11.glPushMatrix();
            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.35F);
            GL11.glLineWidth(10.0F);
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            
            double px = e.player.lastTickPosX + (e.player.posX - e.player.lastTickPosX) * e.partialTicks;
            double py = e.player.lastTickPosZ + (e.player.posY - e.player.lastTickPosY) * e.partialTicks;
            double pz = e.player.lastTickPosY + (e.player.posZ - e.player.lastTickPosZ) * e.partialTicks;
            
            GL11.glTranslated(-px, -py, -pz);
            GL11.glBindTexture(GL_2D, tex);
            int minX, minY, minZ, maxX, maxY, maxZ;
            float textureMinU, textureMinV, textureMaxU, textureMaxV;
            for (Vector3 v : ls)
            {
                // System.out.println("Attempting to render: " + v);
                minX = v.x;
                minY = v.y;
                minZ = v.z;
                
                maxX = v.x + 1;
                maxY = v.y + 1;
                maxZ = v.z + 1;
                
                textureMinU = b.getBlockTextureFromSide(0).getMinU();
                textureMaxU = b.getBlockTextureFromSide(0).getMaxU();
                textureMinV = b.getBlockTextureFromSide(0).getMinV();
                textureMaxV = b.getBlockTextureFromSide(0).getMaxV();
                
                GL11.glTexCoord2f(textureMinU, textureMinV);
                GL11.glVertex3f(minX, minY, minZ);
                GL11.glTexCoord2f(textureMaxU, textureMinV);
                GL11.glVertex3f(minX, minY, minZ);
                GL11.glTexCoord2f(textureMaxU, textureMaxV);
                GL11.glVertex3f(maxX, minY, maxZ);
                GL11.glTexCoord2f(textureMinU, textureMaxV);
                GL11.glVertex3f(minX, minY, maxZ);
                
                GL11.glEnd();
            }
            
            GL11.glPopMatrix();
        }
    }
}
