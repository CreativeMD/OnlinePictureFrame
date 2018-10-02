package com.creativemd.opf.client;

public class OrdinaryTexture extends PictureTexture {
	
	private final int textureID;
	
	public OrdinaryTexture(ProcessedImageData image) {
		super(image.getWidth(), image.getHeight());
		textureID = image.uploadFrame(0);
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public int getTextureID() {
		return textureID;
	}
}
