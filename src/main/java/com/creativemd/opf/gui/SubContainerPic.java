package com.creativemd.opf.gui;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerPic extends SubContainer {
	
	public TileEntityPicFrame frame;
	
	public SubContainerPic(TileEntityPicFrame frame, EntityPlayer player) {
		super(player);
		this.frame = frame;
	}

	@Override
	public void createControls() {
		
	}

	@Override
	public void onGuiPacket(int controlID, NBTTagCompound nbt, EntityPlayer player) {
		if(controlID == 0)
		{
			frame.url = nbt.getString("url");
			//frame.initClient();
			frame.sizeX = nbt.getFloat("x");
			frame.sizeY = nbt.getFloat("y");
			frame.updateBlock();
		}
	}

}
