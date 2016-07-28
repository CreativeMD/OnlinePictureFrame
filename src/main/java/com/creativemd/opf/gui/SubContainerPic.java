package com.creativemd.opf.gui;

import com.creativemd.creativecore.gui.container.SubContainer;
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
	public void onPacketReceive(NBTTagCompound nbt) {
		if(nbt.getInteger("type") == 0)
		{
			frame.url = nbt.getString("url");
			//frame.initClient();
			frame.sizeX = nbt.getFloat("x");
			frame.sizeY = nbt.getFloat("y");
			
			frame.renderDistance = nbt.getInteger("render");
			frame.posX = nbt.getByte("posX");
			frame.posY = nbt.getByte("posY");
			frame.rotation = nbt.getByte("rotation");
			frame.visibleFrame = nbt.getBoolean("visibleFrame");
			frame.flippedX = nbt.getBoolean("flippedX");
			frame.flippedY = nbt.getBoolean("flippedY");
			
			frame.updateBlock();
		}
	}

}
