package com.creativemd.opf.client;

import com.creativemd.creativecore.client.rendering.model.CreativeBlockRenderHelper;
import com.creativemd.opf.OPFrame;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OPFrameClient {
	
	public static void initClient() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPicFrame.class, new PicTileRenderer());
		
		CreativeBlockRenderHelper.registerCreativeRenderedBlock(OPFrame.frame);
	}
}
