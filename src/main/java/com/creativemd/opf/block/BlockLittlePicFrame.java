package com.creativemd.opf.block;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.client.rendering.model.ICreativeRendered;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.Rotation;
import com.creativemd.littletiles.common.api.ILittleTile;
import com.creativemd.littletiles.common.structure.LittleStructure;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.vec.LittleTileBox;
import com.creativemd.littletiles.common.tiles.vec.LittleTileSize;
import com.creativemd.opf.OPFrame;
import com.creativemd.opf.little.LittleOpFrame;
import com.creativemd.opf.little.LittleOpPreview;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLittlePicFrame extends Block implements ILittleTile, ICreativeRendered {

	public BlockLittlePicFrame() {
		super(Material.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	@Method(modid = "littletiles")
	public ArrayList<LittleTilePreview> getLittlePreview(ItemStack stack) {
		ArrayList<LittleTilePreview> preview = new ArrayList<LittleTilePreview>();
		NBTTagCompound nbt = new NBTTagCompound();
		LittleOpFrame frame = new LittleOpFrame(new TileEntityPicFrame(), OPFrame.littleFrame, stack.getItemDamage());
		frame.box = new LittleTileBox(0, 0, 0, 1, 1, 1);
		frame.saveTile(nbt);
		nbt.setBoolean("fresh", true);
		//new LittleTileSize(1, 1, 1).writeToNBT("size", nbt);
		preview.add(new LittleOpPreview(new LittleTileBox(0, 0, 0, 1, 1, 1), nbt));
		return preview;
	}

	@Override
	@Method(modid = "littletiles")
	public void rotateLittlePreview(ItemStack stack, Rotation rotation) {
		
	}

	@Override
	@Method(modid = "littletiles")
	public void flipLittlePreview(ItemStack stack, Axis axis) {
		
	}

	@Override
	@Method(modid = "littletiles")
	public LittleStructure getLittleStructure(ItemStack stack) {
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<RenderCubeObject> getRenderingCubes(IBlockState state, TileEntity te, ItemStack stack) {
		ArrayList<RenderCubeObject> cubes = new ArrayList<RenderCubeObject>();
		RenderCubeObject cube = new RenderCubeObject(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, Blocks.PLANKS);
		cubes.add(cube);
		return cubes;
	}

	@Override
	public void saveLittlePreview(ItemStack stack, List<LittleTilePreview> previews) {
		stack.setTagCompound(new NBTTagCompound());
		if(previews.size() > 0)
			previews.get(0).writeToNBT(stack.getTagCompound());
	}

	@Override
	public boolean hasLittlePreview(ItemStack stack) {
		return true;
	}

}
