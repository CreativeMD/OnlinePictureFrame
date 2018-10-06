package com.creativemd.opf.client;

import com.creativemd.creativecore.client.rendering.model.CreativeBlockRenderHelper;
import com.creativemd.opf.OPFrame;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OPFrameClient {
	
	//public static int modelID;
	
	public static void initClient() {
		//modelID = RenderingRegistry.getNextAvailableRenderId();
		//RenderingRegistry.registerBlockHandler(modelID, new PicBlockRenderer());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPicFrame.class, new PicTileRenderer());
		
		CreativeBlockRenderHelper.registerCreativeRenderedBlock(OPFrame.frame);
		
		if (Loader.isModLoaded("littletiles"))
			CreativeBlockRenderHelper.registerCreativeRenderedBlock(OPFrame.littleFrame);
		//registerBlockItem(OPFrame.frame);
	}
	
	/* private static void registerBlockItem(Block toRegister){
	 * Item item = Item.getItemFromBlock(toRegister);
	 * Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(OPFrame.modid + ":" + item.getUnlocalizedName(), "inventory"));
	 * } */
}
