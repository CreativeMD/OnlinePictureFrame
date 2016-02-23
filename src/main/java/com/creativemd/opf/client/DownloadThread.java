package com.creativemd.opf.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Tuple;

@SideOnly(Side.CLIENT)
public class DownloadThread extends Thread {
	
	public static HashMap<String, Integer> loadedImages = new HashMap<String, Integer>();
	public static HashMap<String, Vector2f> loadedImagesSize = new HashMap<String, Vector2f>();
	
	public static ArrayList<String> loadingImages = new ArrayList<String>();
	
	private String url;
	
	private float progress = 0F;
	
	private BufferedImage loadedImage = null; 
	
	public DownloadThread(String url) {
		this.url = url;
		start();
	}
	
	public boolean hasFinished()
	{
		return progress == 1F;
	}
	
	public boolean hasFailed()
	{
		return hasFinished() && loadedImage == null;
	}
	
	public BufferedImage getDownloadedImage()
	{
		return loadedImage;
	}
	
	public float getProgress()
	{
		return progress;
	}
	
	@Override
	public void run()
	{
		try {
			loadedImage = ImageIO.read(new URL(url));
		} catch (Exception e) {
			loadedImage = null;
			e.printStackTrace();
		}
		
		
		
		progress = 1F;
	}
	
	public static int loadImage(DownloadThread thread)
	{
		if(!thread.hasFailed())
		{
			BufferedImage image = thread.getDownloadedImage();
			int id = loadTexture(image);
			loadedImages.put(thread.url, id);
			loadedImagesSize.put(thread.url, new Vector2f(image.getWidth(), image.getHeight()));
			return id;
		}
		return -1;
	}
	
	private static final int BYTES_PER_PIXEL = 4;
	   public static int loadTexture(BufferedImage image){
	      
	      int[] pixels = new int[image.getWidth() * image.getHeight()];
	        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

	        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB
	        
	        for(int y = 0; y < image.getHeight(); y++){
	            for(int x = 0; x < image.getWidth(); x++){
	                int pixel = pixels[y * image.getWidth() + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
	                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
	                buffer.put((byte) (pixel & 0xFF));               // Blue component
	                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
	            }
	        }

	        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

	        // You now have a ByteBuffer filled with the color data of each pixel.
	        // Now just create a texture ID and bind it. Then you can load it using 
	        // whatever OpenGL method you want, for example:

	      int textureID = GL11.glGenTextures(); //Generate texture ID
	      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID
	        
	        //Setup wrap mode
	      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

	        //Setup texture scaling filtering
	      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	        
	        //Send texel data to OpenGL
	      GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	      
	        //Return the texture ID so we can bind it later again
	      return textureID;
	   }
	
}
