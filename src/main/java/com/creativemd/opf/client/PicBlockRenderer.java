package com.creativemd.opf.client;

import java.util.ArrayList;

import com.creativemd.creativecore.client.block.BlockRenderHelper;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.opf.block.TileEntityPicFrame;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class PicBlockRenderer implements ISimpleBlockRenderingHandler{

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		cubes.add(new CubeObject(0, 0, 0, 0.05, 1, 1, Blocks.planks));
		BlockRenderHelper.renderInventoryCubes(renderer, cubes, block, metadata);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		ArrayList<CubeObject> cubes = new ArrayList<CubeObject>();
		cubes.add(new CubeObject(0, 0, 0, 0.03, 1, 1, Blocks.planks));
		
		TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityPicFrame)
		{
			if(((TileEntityPicFrame) te).visibleFrame)
				BlockRenderHelper.renderCubes(world, cubes, x, y, z, block, renderer, ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)));
		}
		
		
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return OPFrameClient.modelID;
	}

}
