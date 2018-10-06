package com.creativemd.opf.little;

import com.creativemd.littletiles.common.tiles.place.PlacePreviewTile;
import com.creativemd.littletiles.common.tiles.preview.LittlePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.littletiles.common.tiles.vec.LittleTileSize;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;

import net.minecraft.nbt.NBTTagCompound;

public class LittleOpPreview extends LittleTilePreview {
	
	public LittleOpPreview(NBTTagCompound nbt) {
		super(nbt);
	}

	public LittleOpPreview(LittleTileBox box, NBTTagCompound nbt) {
		super(box, nbt);
	}
	
	@Override
	public PlacePreviewTile getPlaceableTile(LittleTileBox box, boolean canPlaceNormal, LittleTileVec offset, LittlePreviews previews)
	{
		if(!canPlaceNormal)
			this.box.addOffset(offset);
		return new LittlePlaceOpPreview(this.box, this, previews);
	}
	
}
