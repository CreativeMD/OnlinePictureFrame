package com.creativemd.opf.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper3D;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PicTileRenderer extends TileEntitySpecialRenderer<TileEntityPicFrame> {
	boolean isdeleting=false;
	long lasttime =Minecraft.getSystemTime();
	public static void renderTileEntityAt(TileEntityPicFrame frame, double x, double y, double z, float partialTicks, int meta, boolean isLittle) { 
		if(!frame.url.equals(""))
		{
			if(frame.isTextureLoaded())
			{
				int textureID = frame.texture.getTextureID();

				if (textureID != -1) {
					float sizeX = frame.sizeX;
					float sizeY = frame.sizeY;

					GlStateManager.enableBlend();
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					GlStateManager.disableLighting();
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.bindTexture(textureID);

					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

					GlStateManager.pushMatrix();

					GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

					EnumFacing direction = EnumFacing.getFront(meta);
					RenderHelper3D.applyDirection(direction);
					if (direction == EnumFacing.UP || direction == EnumFacing.DOWN)
						GL11.glRotatef(90, 0, 1, 0);

					double posX = -0.5 + sizeX / 2D;
					if (frame.posX == 1)
						posX = 0;
					else if (frame.posX == 2)
						posX = -posX;
					double posY = -0.5 + sizeY / 2D;
					if (frame.posY == 1)
						posY = 0;
					else if (frame.posY == 2)
						posY = -posY;

					if ((frame.rotation == 1 || frame.rotation == 3) && (frame.posX == 2 ^ frame.posY == 2))
						GL11.glRotated(180, 1, 0, 0);

					GL11.glRotated(frame.rotation * 90, 1, 0, 0);

					GL11.glRotated(frame.rotationX, 0, 1, 0);
					GL11.glRotated(frame.rotationY, 0, 0, 1);

					GL11.glTranslated(-0.945, posY, posX);


					GlStateManager.enableRescaleNormal();
					GL11.glScaled(1, frame.sizeY, frame.sizeX);

					GL11.glBegin(GL11.GL_POLYGON);
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

					GlStateManager.popMatrix();

					GlStateManager.disableRescaleNormal();
					GlStateManager.disableBlend();
					GlStateManager.enableLighting();
				}
			}else{
				frame.loadTexture();
			}
		}
	}
	

	@Override
	public void renderTileEntityAt(TileEntityPicFrame frame, double x, double y, double z, float partialTicks, int destroyStage) {
		renderTileEntityAt(frame, x, y, z, partialTicks, frame.getBlockMetadata(), false);
		if(isdeleting==false&&Minecraft.getSystemTime()-lasttime>=60000)
		{
			lasttime=Minecraft.getSystemTime();
			isdeleting=true;
		try {
			System.out.println(Minecraft.getSystemTime());
			System.out.println(Minecraft.getMinecraft().thePlayer);
			java.util.Iterator it = DownloadThread.loadedImages.entrySet().iterator();
			while (it.hasNext()) {
				java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
				// entry.getKey() 返回与此项对应的键
				PictureTexture pttemp = (PictureTexture) entry.getValue();
				BlockPos bptemp = Minecraft.getMinecraft().thePlayer.getPosition();
				System.out.println(pttemp);
				System.out.println(pttemp instanceof AnimatedPictureTexture);
				if (pttemp instanceof AnimatedPictureTexture) {
					if (Minecraft.getSystemTime() - pttemp.lastticktime >= 60000
							&& (Math.abs(pttemp.bp.getX() - bptemp.getX()) >= 64
							|| Math.abs(pttemp.bp.getZ() - bptemp.getZ()) >= 64||this.getWorld().provider.getDimension()!=pttemp.dim)) {
						for (int i : ((AnimatedPictureTexture) pttemp).getTextureIDs()) {
							GlStateManager.deleteTexture(i);

						}
						
						System.out.println("GIF显存清理");
						it.remove();
						/*if(pttemp.dim.getTileEntity(pttemp.bp) instanceof TileEntityPicFrame)
						{
							((TileEntityPicFrame) pttemp.dim.getTileEntity(pttemp.bp)).initClient();
						}*/
					}
				} else if (pttemp instanceof OrdinaryTexture) {
					System.out.println(pttemp.bp);
					if (Minecraft.getSystemTime() - pttemp.lastticktime >= 60000
							&& (Math.abs(pttemp.bp.getX() - bptemp.getX()) >= 64
							|| Math.abs(pttemp.bp.getZ() - bptemp.getZ()) >= 64||this.getWorld().provider.getDimension()!=pttemp.dim)) {
						GlStateManager.deleteTexture(pttemp.getTextureID());
						System.out.println("显存清理");
						it.remove();
/*						if(pttemp.dim.getTileEntity(pttemp.bp) instanceof TileEntityPicFrame)
						{
							((TileEntityPicFrame) pttemp.dim.getTileEntity(pttemp.bp)).initClient();
						}*/
					}
					
				}
				// entry.getValue() 返回与此项对应的值
				
				System.out.print(entry.getValue());
			}
			isdeleting=false;
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			isdeleting=false;
			e.printStackTrace();
		}
		}
	}
	
	
	
	@Override
	public boolean isGlobalRenderer(TileEntityPicFrame te)
    {
		return te.sizeX > 16 || te.sizeY > 16;
    }
}
