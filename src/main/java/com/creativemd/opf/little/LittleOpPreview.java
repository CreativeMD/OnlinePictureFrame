package com.creativemd.opf.little;

import com.creativemd.littletiles.common.utils.LittleTilePreview;
import com.creativemd.littletiles.common.utils.small.LittleTileBox;
import com.creativemd.littletiles.common.utils.small.LittleTileSize;
import com.creativemd.littletiles.common.utils.small.LittleTileVec;
import com.creativemd.littletiles.utils.PlacePreviewTile;

import net.minecraft.nbt.NBTTagCompound;

public class LittleOpPreview extends LittleTilePreview {
	
	public LittleOpPreview(NBTTagCompound nbt) {
		super(nbt);
	}

	public LittleOpPreview(LittleTileSize size, NBTTagCompound nbt) {
		super(size, nbt);
	}
	
	@Override
	public PlacePreviewTile getPlaceableTile(LittleTileBox box, boolean canPlaceNormal, LittleTileVec offset)
	{
		if(this.box == null)
		{
			return new LittlePlaceOpPreview(box.copy(), this);
		}else{
			if(!canPlaceNormal)
				this.box.addOffset(offset);
			return new LittlePlaceOpPreview(this.box, this);
		}
	}
	
}
