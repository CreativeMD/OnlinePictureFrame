package com.creativemd.opf.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper3D;
import com.creativemd.creativecore.common.utils.RotationUtils;
import com.creativemd.opf.block.TileEntityPicFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class PicTileRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
			float partialTick) {
		if(te instanceof TileEntityPicFrame)
		{
			TileEntityPicFrame frame = (TileEntityPicFrame) te;
			if(!frame.url.equals(""))
			{
				//frame.url = "http://i.imgur.com/cnJ0dHP.png";
				if(frame.isTextureLoaded())
				{
					float sizeX = frame.sizeX;
					float sizeY = frame.sizeY;
					//sizeX = 16;
					//sizeY = 9;
					//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
					GL11.glEnable(GL11.GL_BLEND);
		            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		            GL11.glDisable(GL11.GL_LIGHTING);
		            GL11.glColor4f(1, 1, 1, 1);
		           // GL11.glLineWidth(2.0F);
		            GL11.glEnable(GL11.GL_TEXTURE_2D);
		            //GL11.glDepthMask(false);
		            
		            GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.textureID);
		            
		            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		            //GL11.glPushMatrix();
		            GL11.glPushMatrix();
		            
		    		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
		    		
		    		
		    		//GL11.glRotatef((float)System.nanoTime()/10000000F, 0, 0, 1);
		    		ForgeDirection direction = ForgeDirection.getOrientation(frame.getBlockMetadata());
		    		RenderHelper3D.applyDirection(direction);
		    		if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
		    			GL11.glRotatef(90, 0, 1, 0);
		    		
		    		GL11.glTranslated(-0.945, -0.5+sizeY/2D, -0.5+sizeX/2D);
		    		
		    		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		    		GL11.glScaled(1, sizeY, sizeX);
		    		/*GL11.glRotated(90, 1, 0, 0);
		    		GL11.glRotated(90, 0, 0, 1);*/
		    		//GL11.glRotated(rotateZ, 0, 0, 1);
		    		/*GL11.glRotated(rotateX, 1, 0, 0);
		    		
		    		GL11.glScaled(width, height, length);
		    		GL11.glColor4d(red, green, blue, alpha);*/
		    		
		    		GL11.glBegin(GL11.GL_POLYGON);
		    		//GL11.glColor4d(red, green, blue, alpha);
		    		GL11.glNormal3f(1.0f, 0.0F, 0.0f);
		    		
		    		GL11.glTexCoord3f(1, 1, 0);
		    		GL11.glVertex3f(0.5F, -0.5f, -0.5f);
		    		GL11.glTexCoord3f(1, 0, 0);
		    		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		    		GL11.glTexCoord3f(0, 0, 0);
		    		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		    		GL11.glTexCoord3f(0, 1, 0);
		    		GL11.glVertex3f(0.5f, -0.5f, 0.5f);
		    		GL11.glEnd();
		    		
		            GL11.glPopMatrix();
		            //GL11.glPopMatrix();
		            
		            //GL11.glDepthMask(true);
		            //GL11.glEnable(GL11.GL_TEXTURE_2D);
		            GL11.glDisable(GL11.GL_BLEND);
		            GL11.glEnable(GL11.GL_LIGHTING);
				}else{
					frame.loadTexutre();
				}
			}
		}
	}

}
