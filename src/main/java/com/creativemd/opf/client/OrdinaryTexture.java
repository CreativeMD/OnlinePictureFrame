package com.creativemd.opf.client;

import java.awt.image.BufferedImage;

public class OrdinaryTexture extends PictureTexture {

	private final int textureID;
	
	public OrdinaryTexture(BufferedImage image) {
		super(image.getWidth(), image.getHeight());
		this.textureID = DownloadThread.loadTexture(image);
	}

	@Override
	public int getTextureID() {
		return textureID;
	}
}
