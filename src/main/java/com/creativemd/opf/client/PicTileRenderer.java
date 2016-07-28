package com.creativemd.opf.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper3D;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PicTileRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
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
		    		EnumFacing direction = EnumFacing.getFront(frame.getBlockMetadata());
		    		RenderHelper3D.applyDirection(direction);
		    		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN)
		    			GL11.glRotatef(90, 0, 1, 0);
		    		
		    		double posX = -0.5+sizeX/2D;
		    		if(frame.posX == 1)
		    			posX = 0;
		    		else if(frame.posX == 2)
		    			posX = -posX;
		    		double posY = -0.5+sizeY/2D;
		    		if(frame.posY == 1)
		    			posY = 0;
		    		else if(frame.posY == 2)
		    			posY = -posY;
		    		
		    		if((frame.rotation == 1 || frame.rotation == 3) && (frame.posX == 2 ^ frame.posY == 2))
		    			GL11.glRotated(180, 1, 0, 0);
		    		
		    		GL11.glRotated(frame.rotation * 90, 1, 0, 0);
		    		
		    		/*double moveX = 0;//sizeX/2D-0.5;
		    		double moveY = -0;//sizeY/2D-0.5;
		    		double moveZ = -0.5;
		    		GL11.glTranslated(moveZ, moveY, moveX);*/
		    		GL11.glRotated(frame.rotationX, 0, 1, 0);
		    		GL11.glRotated(frame.rotationY, 0, 0, 1);
		    		//GL11.glTranslated(-moveZ, -moveY, -moveX);
		    		
		    		GL11.glTranslated(-0.945, posY, posX);
		    		
		    		
		    		
		    		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		    		GL11.glScaled(1, frame.sizeY, frame.sizeX);
		    		
		    		/*GL11.glRotated(90, 1, 0, 0);
		    		GL11.glRotated(90, 0, 0, 1);*/
		    		
		    		
		    		/*GL11.glScaled(width, height, length);
		    		GL11.glColor4d(red, green, blue, alpha);*/
		    		
		    		GL11.glBegin(GL11.GL_POLYGON);
		    		//GL11.glColor4d(red, green, blue, alpha);
		    		GL11.glNormal3f(1.0f, 0.0F, 0.0f);
		    		
		    		GL11.glTexCoord3f(frame.flippedY ? 0 : 1, frame.flippedX ? 0 : 1, 0);
		    		GL11.glVertex3f(0.5F, -0.5f, -0.5f);
		    		GL11.glTexCoord3f(frame.flippedY ? 0 : 1, frame.flippedX ? 1 : 0, 0);
		    		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		    		GL11.glTexCoord3f(frame.flippedY ? 1 : 0, frame.flippedX ? 1 : 0, 0);
		    		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		    		GL11.glTexCoord3f(frame.flippedY ? 1 : 0, frame.flippedX ? 0 : 1, 0);
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
