package com.creativemd.opf.client;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OrdinaryTexture extends PictureTexture {

	private final int textureID;
	
	
	public OrdinaryTexture(ProcessedImageData image,BlockPos bp,int dim) {
		super(image.getWidth(), image.getHeight(),bp, dim);
		textureID = image.uploadFrame(0);
	}
	@Override
	public void tick() {
	}
	@Override
	public int getTextureID() {
		this.lastticktime=Minecraft.getSystemTime();
		return textureID;
	}
}
