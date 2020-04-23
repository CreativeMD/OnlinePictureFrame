package com.creativemd.opf.little;

import com.creativemd.littletiles.common.tile.math.box.LittleBox;
import com.creativemd.littletiles.common.tile.math.vec.LittleVec;
import com.creativemd.littletiles.common.tile.place.PlacePreview;
import com.creativemd.littletiles.common.tile.preview.LittlePreview;

import net.minecraft.nbt.NBTTagCompound;

public class LittleOpPreview extends LittlePreview {
	
	public LittleOpPreview(NBTTagCompound nbt) {
		super(nbt);
	}
	
	public LittleOpPreview(LittleBox box, NBTTagCompound nbt) {
		super(box, nbt);
	}
	
	@Override
	public PlacePreview getPlaceableTile(LittleVec offset) {
		if (offset != null)
			this.box.add(offset);
		return new LittlePlaceOpPreview(this.box, this);
	}
	
}
