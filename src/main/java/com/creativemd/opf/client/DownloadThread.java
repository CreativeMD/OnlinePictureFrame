package com.creativemd.opf.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.vecmath.Vector2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.porpit.lib.GifDecoder;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DownloadThread extends Thread {
	
	public static HashMap<String, PictureTexture> loadedImages = new HashMap<String, PictureTexture>();
	
	public static ArrayList<String> loadingImages = new ArrayList<String>();
	
	private String url;
	
	private float progress = 0F;
	
	private InputStream loadedStream = null; 
	
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
		return hasFinished() && loadedStream == null;
	}
	
	public InputStream getDownloadedImage()
	{
		return loadedStream;
	}
	
	public float getProgress()
	{
		return progress;
	}
	
	@Override
	public void run()
	{
		try{	        
			URLConnection con = new URL(url).openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			loadedStream = (InputStream) con.getInputStream();
		} catch (Exception e) {
			loadedStream = null;
			e.printStackTrace();
		}
		
		
		
		progress = 1F;
	}
	
	public static String readType(InputStream input) throws IOException
	{
	    ImageInputStream stream = ImageIO.createImageInputStream(input);

	    Iterator iter = ImageIO.getImageReaders(stream);
	    if (!iter.hasNext()) {
	        return "";
	    }
	    ImageReader reader = (ImageReader) iter.next();
	    ImageReadParam param = reader.getDefaultReadParam();
	    reader.setInput(stream, true, true);
	    BufferedImage bi;
	    try {
	        bi = reader.read(0, param);
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        reader.dispose();
	        try {
	            stream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    input.reset();
	    return reader.getFormatName();
	}
	
	public static PictureTexture loadImage(DownloadThread thread)
	{
		PictureTexture texture = null;
		if(!thread.hasFailed())
		{
			InputStream stream = thread.getDownloadedImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int len;
			try {
				while ((len = stream.read(buffer)) > -1 ) {
				    baos.write(buffer, 0, len);
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				baos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
			InputStream is2 = new ByteArrayInputStream(baos.toByteArray()); 
			try {
				if(readType(is1).equals("gif"))
				{
					GifDecoder gif = new GifDecoder();
					if(gif.read(is2) == GifDecoder.STATUS_OK)
						texture = new AnimatedPictureTexture(gif);
				}else{
					BufferedImage image = ImageIO.read(is2);
					if(image != null)
						texture = new OrdinaryTexture(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(texture != null)
			loadedImages.put(thread.url, texture);
		return texture;
	}
	
	private static final int BYTES_PER_PIXEL = 4;
	
	public static int loadTexture(BufferedImage image)
	{
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
		
		buffer.flip();
		
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
