package com.creativemd.opf.client;

import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PictureTexture {
	
	public final BlockPos bp;
	public final int dim;
	public long lastticktime;
	public final int width;
	public final int height;
	
	public PictureTexture(int width, int height,BlockPos bp,int dim)
	{
		this.dim=dim;
		this.bp=bp;
		this.lastticktime=Minecraft.getSystemTime();
		this.width = width;
		this.height = height;
	}
	public abstract void tick();
	public abstract int getTextureID();
	
}
