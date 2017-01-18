package com.creativemd.opf.little;

import java.util.ArrayList;

import com.creativemd.creativecore.common.utils.ColoredCube;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.utils.LittleTile;
import com.creativemd.littletiles.common.utils.LittleTilePreview;
import com.creativemd.littletiles.common.utils.small.LittleTileBox;
import com.creativemd.littletiles.utils.PlacePreviewTile;
import com.creativemd.opf.block.TileEntityPicFrame;

import io.netty.util.concurrent.ProgressiveFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LittlePlaceOpPreview extends PlacePreviewTile {

	public LittlePlaceOpPreview(LittleTileBox box, LittleTilePreview preview) {
		super(box, preview);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<ColoredCube> getPreviews()
	{
		NBTTagCompound nbt = preview.getTileData();
		ArrayList<ColoredCube> cubes = new ArrayList<>();
		cubes.add(new ColoredCube(box.getCube()));
		if(preview.box != null && nbt.hasKey("tileEntity"))
		{
			TileEntityPicFrame tileEntity = (TileEntityPicFrame) TileEntity.func_190200_a(Minecraft.getMinecraft().theWorld, nbt.getCompoundTag("tileEntity"));
			CubeObject picPreview = LittleOpFrame.getBoundingBoxByTilenEntity(tileEntity, nbt.getInteger("meta"));
			picPreview.add(box.getMinVec().getVec());
			cubes.add(new ColoredCube(picPreview, new Vec3d(0, 1, 1)));
		}
		
		
		return cubes;
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
