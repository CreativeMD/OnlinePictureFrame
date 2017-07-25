package com.creativemd.opf.little;

import com.creativemd.creativecore.common.utils.ColoredCube;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.tiles.LittleTile;
import com.creativemd.littletiles.common.tiles.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.place.PlacePreviewTile;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.opf.block.TileEntityPicFrame;
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

import java.util.ArrayList;

public class LittlePlaceOpPreview extends PlacePreviewTile {

	public LittlePlaceOpPreview(LittleTileBox box, LittleTilePreview preview) {
		super(box, preview);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<ColoredCube> getPreviews()
	{
		NBTTagCompound nbt = preview.getTileData();
		ArrayList<ColoredCube> cubes = new ArrayList<ColoredCube>();
		cubes.add(new ColoredCube(box.getCube()));
		if(preview.box != null && nbt.hasKey("tileEntity"))
		{
			TileEntityPicFrame tileEntity = (TileEntityPicFrame) TileEntity.create(Minecraft.getMinecraft().world, nbt.getCompoundTag("tileEntity"));
			CubeObject picPreview = LittleOpFrame.getBoundingBoxByTilenEntity(tileEntity, nbt.getInteger("meta"));
			picPreview.add(box.getMinVec().getVec());
			cubes.add(new ColoredCube(picPreview, new Vec3d(0, 1, 1)));
		}
		
		
		return cubes;
	}
	
	@Override
	public LittleTile placeTile(EntityPlayer player, ItemStack stack, BlockPos pos, TileEntityLittleTiles teLT, LittleStructure structure, ArrayList<LittleTile> unplaceableTiles, boolean forced, EnumFacing facing, boolean requiresCollisionTest)
	{
		LittleTile tile = super.placeTile(player, stack, pos, teLT, structure, unplaceableTiles, forced, facing, requiresCollisionTest);
		if(tile instanceof LittleOpFrame && preview.box == null)
		{
			((LittleOpFrame) tile).setMeta(facing.getIndex());
			ReflectionHelper.setPrivateValue(TileEntity.class, ((LittleOpFrame) tile).getTileEntity(), ((LittleOpFrame) tile).getMeta(), "blockMetadata", "field_145847_g");
		}
		return tile;
	}
	
	@Override
	public PlacePreviewTile copy()
	{
		return new LittlePlaceOpPreview(box.copy(), preview.copy());
	}

}
