package com.creativemd.opf.little;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.utils.math.box.AlignedBox;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.littletiles.client.render.tile.LittleRenderBox;
import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.tile.LittleTile;
import com.creativemd.littletiles.common.tile.math.box.LittleBox;
import com.creativemd.littletiles.common.tile.place.PlacePreview;
import com.creativemd.littletiles.common.tile.preview.LittlePreview;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.tileentity.TileList;
import com.creativemd.littletiles.common.util.grid.LittleGridContext;
import com.creativemd.littletiles.common.util.place.PlacementMode;
import com.creativemd.opf.block.TileEntityPicFrame;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LittlePlaceOpPreview extends PlacePreview {
	
	public LittlePlaceOpPreview(LittleBox box, LittlePreview preview) {
		super(box, preview);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<LittleRenderBox> getPreviews(LittleGridContext context) {
		NBTTagCompound nbt = preview.getTileData();
		List<LittleRenderBox> cubes = new ArrayList<LittleRenderBox>();
		cubes.add(box.getRenderingCube(context, null, 0));
		if (!nbt.getBoolean("fresh")) {
			TileEntityPicFrame tileEntity = (TileEntityPicFrame) TileEntity.create(Minecraft.getMinecraft().world, nbt.getCompoundTag("tileEntity"));
			AlignedBox picPreview = LittleOpFrame.getBoundingBoxByTilenEntity(context, tileEntity, nbt.getInteger("meta"));
			picPreview.add(box.getMinVec().getVector(context));
			LittleRenderBox renderingCube = box.getRenderingCube(context, picPreview, null, 0);
			renderingCube.color = ColorUtils.VecToInt(new Vec3d(0, 1, 1));
			cubes.add(renderingCube);
		}
		
		return cubes;
	}
	
	@Override
	public List<LittleTile> placeTile(EntityPlayer player, BlockPos pos, LittleGridContext context, TileEntityLittleTiles te, TileList list, List<LittleTile> unplaceableTiles, List<LittleTile> removedTiles, PlacementMode mode, EnumFacing facing, boolean requiresCollisionTest, LittleStructure structure) {
		List<LittleTile> tiles = super.placeTile(player, pos, context, te, list, unplaceableTiles, removedTiles, mode, facing, requiresCollisionTest, structure);
		LittleTile tile = tiles.size() > 0 ? tiles.get(0) : null;
		if (tile instanceof LittleOpFrame && preview.getTileData().getBoolean("fresh")) {
			((LittleOpFrame) tile).setMeta(facing.getIndex());
			ReflectionHelper.setPrivateValue(TileEntity.class, ((LittleOpFrame) tile).getTileEntity(), ((LittleOpFrame) tile).getMeta(), new String[] { "blockMetadata", "field_145847_g" });
		}
		return tiles;
	}
	
	@Override
	public PlacePreview copy() {
		return new LittlePlaceOpPreview(box.copy(), preview.copy());
	}
	
}
