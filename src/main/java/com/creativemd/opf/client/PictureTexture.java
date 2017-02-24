package com.creativemd.opf.client;

import java.awt.image.BufferedImage;

public abstract class PictureTexture {
	
	
	public final int width;
	public final int height;
	
	public PictureTexture(int width, int height)
	{
		this.width = width;
		this.height = height;
		
	}
	
	public abstract int getTextureID();
	
}
