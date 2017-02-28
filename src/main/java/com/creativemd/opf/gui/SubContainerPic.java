package com.creativemd.opf.gui;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.premade.SubContainerTileEntity;
import com.creativemd.littletiles.common.utils.LittleTile;
import com.creativemd.opf.OPFrame;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.little.LittleOpFrame;

import mezz.jei.api.JEIPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import scala.tools.nsc.transform.patmat.Solving.Solver.Lit;

public class SubContainerPic extends SubContainerTileEntity {
	
	public TileEntityPicFrame frame;
	public Object tile;
	
	public SubContainerPic(TileEntityPicFrame frame, EntityPlayer player, Object tile) {
		super(player, frame);
		this.frame = frame;
		this.tile = tile;
	}
	
	public SubContainerPic(TileEntityPicFrame frame, EntityPlayer player) {
		this(frame, player, null);
	}
	
	@Override
	public boolean shouldTick()
	{
		return false;
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
			frame.sizeX = Math.min(OPFrame.sizeLimitation, nbt.getFloat("x"));
			frame.sizeY = Math.min(OPFrame.sizeLimitation, nbt.getFloat("y"));
			
			frame.renderDistance = nbt.getInteger("render");
			frame.posX = nbt.getByte("posX");
			frame.posY = nbt.getByte("posY");
			frame.rotation = nbt.getByte("rotation");
			frame.visibleFrame = nbt.getBoolean("visibleFrame");
			frame.flippedX = nbt.getBoolean("flippedX");
			frame.flippedY = nbt.getBoolean("flippedY");
			
			frame.rotationX = nbt.getFloat("rotX");
			frame.rotationY = nbt.getFloat("rotY");
			
			if(nbt.hasKey("facing"))
				setFacing(EnumFacing.getFront(nbt.getInteger("facing")));
			
			frame.updateBlock();
		}
	}
	
	@Method(modid = "littletiles")
	public void setFacing(EnumFacing facing)
	{
		if(tile != null)
		{
			((LittleOpFrame) tile).setMeta(facing.getIndex());
			((LittleOpFrame) tile).markForUpdate();
			ReflectionHelper.setPrivateValue(TileEntity.class, frame, facing.getIndex(), "blockMetadata", "field_145847_g");
			((LittleOpFrame) tile).needsFullUpdate = true;
		}
	}

}
