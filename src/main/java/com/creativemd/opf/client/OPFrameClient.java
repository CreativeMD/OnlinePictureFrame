package com.creativemd.opf.client;

import com.creativemd.opf.block.TileEntityPicFrame;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class OPFrameClient {
	
	public static int modelID;
	
	public static void initClient()
	{
		modelID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(modelID, new PicBlockRenderer());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPicFrame.class, new PicTileRenderer());
	}
	
}
