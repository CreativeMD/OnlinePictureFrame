package com.creativemd.opf.little;

import java.util.ArrayList;

import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.utils.LittleTile;
import com.creativemd.littletiles.common.utils.LittleTilePreview;
import com.creativemd.littletiles.common.utils.small.LittleTileBox;
import com.creativemd.littletiles.utils.PlacePreviewTile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class LittlePlaceOpPreview extends PlacePreviewTile {

	public LittlePlaceOpPreview(LittleTileBox box, LittleTilePreview preview) {
		super(box, preview);
	}
	
	@Override
	public LittleTile placeTile(EntityPlayer player, ItemStack stack, BlockPos pos, TileEntityLittleTiles teLT, LittleStructure structure, ArrayList<LittleTile> unplaceableTiles, boolean forced, EnumFacing facing)
	{
		LittleTile tile = super.placeTile(player, stack, pos, teLT, structure, unplaceableTiles, forced, facing);
		if(tile instanceof LittleOpFrame)
		{
			((LittleOpFrame) tile).meta = facing.getIndex();
			ReflectionHelper.setPrivateValue(TileEntity.class, ((LittleOpFrame) tile).getTileEntity(), ((LittleOpFrame) tile).meta, "blockMetadata", "field_145847_g");
		}
		return tile;
	}
	
	@Override
	public PlacePreviewTile copy()
	{
		return new LittlePlaceOpPreview(box.copy(), preview.copy());
	}

}
