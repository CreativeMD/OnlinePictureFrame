package com.creativemd.opf.client;

public abstract class PictureTexture {
	
	
	public final int width;
	public final int height;
	
	public PictureTexture(int width, int height)
	{
		this.width = width;
		this.height = height;
		
	}

	public abstract void tick();

	public abstract int getTextureID();
	
}
