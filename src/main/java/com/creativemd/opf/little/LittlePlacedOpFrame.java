package com.creativemd.opf.little;

import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.littletiles.common.tiles.place.PlacePreviewTile;
import com.creativemd.littletiles.common.tiles.preview.LittlePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;
import com.creativemd.littletiles.common.utils.grid.LittleGridContext;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LittlePlacedOpFrame extends LittleTilePreview {
	
	public LittlePlacedOpFrame(NBTTagCompound nbt) {
		super(nbt);
	}
	
	public LittlePlacedOpFrame(LittleTileBox box, NBTTagCompound tileData) {
		super(box, tileData);
	}
	
	@Override
	public PlacePreviewTile getPlaceableTile(LittleTileBox box, boolean canPlaceNormal, LittleTileVec offset, LittlePreviews previews) {
		if (this.box == null)
			return new LittlePlaceOpPreview(box.copy(), this, previews);
		else {
			LittleTileBox newBox = this.box.copy();
			if (!canPlaceNormal)
				newBox.addOffset(offset);
			return new LittlePlaceOpPreview(newBox, this, previews);
		}
	}
	
	@Override
	public void flipPreview(Axis axis, LittleTileVec doubledCenter) {
		super.flipPreview(axis, doubledCenter);
		EnumFacing facing = EnumFacing.getFront(tileData.getInteger("meta"));
		
		if (facing.getAxis() == axis)
			facing = facing.getOpposite();
		
		boolean reverseX = false;
		boolean reverseY = false;
		
		switch (facing.getAxis()) {
		case X:
		case Z:
			if (axis != Axis.Y)
				reverseX = !reverseX;
			else
				reverseY = !reverseY;
			break;
		case Y:
			if (axis != Axis.Z)
				reverseY = !reverseY;
			else
				reverseX = !reverseX;
			break;
		}
		
		if (reverseX) {
			int posX = tileData.getCompoundTag("tileEntity").getInteger("offsetX");
			if (posX == 0)
				tileData.getCompoundTag("tileEntity").setInteger("offsetX", 2);
			else if (posX == 2)
				tileData.getCompoundTag("tileEntity").setInteger("offsetX", 0);
			tileData.getCompoundTag("tileEntity").setBoolean("flippedY", !tileData.getCompoundTag("tileEntity").getBoolean("flippedY"));
		}
		if (reverseY) {
			int posY = tileData.getCompoundTag("tileEntity").getInteger("offsetY");
			if (posY == 0)
				tileData.getCompoundTag("tileEntity").setInteger("offsetY", 2);
			else if (posY == 2)
				tileData.getCompoundTag("tileEntity").setInteger("offsetY", 0);
			tileData.getCompoundTag("tileEntity").setBoolean("flippedX", !tileData.getCompoundTag("tileEntity").getBoolean("flippedX"));
		}
		
		tileData.setInteger("meta", facing.getIndex());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public RenderCubeObject getCubeBlock(LittleGridContext context) {
		RenderCubeObject cube = super.getCubeBlock(context);
		EnumFacing direction = EnumFacing.getFront(tileData.getInteger("meta"));
		double width = 0.025;
		switch (direction) {
		case EAST:
			cube.maxX = (float) (cube.minX + width);
			break;
		case WEST:
			cube.minX = (float) (cube.maxX - width);
			break;
		case UP:
			cube.maxY = (float) (cube.minY + width);
			break;
		case DOWN:
			cube.minY = (float) (cube.maxY - width);
			break;
		case SOUTH:
			cube.maxZ = (float) (cube.minZ + width);
			break;
		case NORTH:
			cube.minZ = (float) (cube.maxZ - width);
			break;
		default:
			break;
		}
		return new RenderCubeObject(cube, Blocks.PLANKS, 0);
	}
	
	@Override
	public void rotatePreview(Rotation direction, LittleTileVec doubledCenter) {
		super.rotatePreview(direction, doubledCenter);
		EnumFacing facing = EnumFacing.getFront(tileData.getInteger("meta"));
		tileData.setInteger("meta", RotationUtils.rotate(facing, direction).getIndex());
		
		EnumFacing front = EnumFacing.getHorizontal(tileData.getCompoundTag("tileEntity").getInteger("rotation"));
		front = RotationUtils.rotate(front, direction);
		
		tileData.getCompoundTag("tileEntity").setInteger("rotation", front.getHorizontalIndex());
	}
	
}
