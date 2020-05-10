package com.creativemd.opf.block;

import java.util.ArrayList;

import com.creativemd.creativecore.client.rendering.RenderBox;
import com.creativemd.creativecore.client.rendering.model.ICreativeRendered;
import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.littletiles.common.api.ILittleTile;
import com.creativemd.littletiles.common.tile.math.box.LittleBox;
import com.creativemd.littletiles.common.tile.preview.LittlePreviews;
import com.creativemd.littletiles.common.util.grid.LittleGridContext;
import com.creativemd.opf.OPFrame;
import com.creativemd.opf.little.LittleOpFrame;
import com.creativemd.opf.little.LittleOpPreview;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
	public LittlePreviews getLittlePreview(ItemStack stack) {
		LittlePreviews previews = new LittlePreviews(LittleGridContext.get());
		NBTTagCompound nbt = new NBTTagCompound();
		LittleOpFrame frame = new LittleOpFrame(new TileEntityPicFrame(), OPFrame.littleFrame, stack.getItemDamage());
		frame.box = new LittleBox(0, 0, 0, 1, 1, 1);
		frame.saveTile(nbt);
		nbt.setBoolean("fresh", true);
		//new LittleTileSize(1, 1, 1).writeToNBT("size", nbt);
		previews.addWithoutCheckingPreview(new LittleOpPreview(new LittleBox(0, 0, 0, 1, 1, 1), nbt));
		return previews;
	}
	
	@Override
	@Method(modid = "littletiles")
	public void rotateLittlePreview(EntityPlayer player, ItemStack stack, Rotation rotation) {
		
	}
	
	@Override
	@Method(modid = "littletiles")
	public void flipLittlePreview(EntityPlayer player, ItemStack stack, Axis axis) {
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<RenderBox> getRenderingCubes(IBlockState state, TileEntity te, ItemStack stack) {
		ArrayList<RenderBox> cubes = new ArrayList<RenderBox>();
		RenderBox cube = new RenderBox(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, Blocks.PLANKS);
		cubes.add(cube);
		return cubes;
	}
	
	@Override
	public void saveLittlePreview(ItemStack stack, LittlePreviews previews) {
		stack.setTagCompound(new NBTTagCompound());
		if (previews.size() > 0)
			previews.get(0).writeToNBT(stack.getTagCompound());
	}
	
	@Override
	public boolean containsIngredients(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean hasLittlePreview(ItemStack stack) {
		return true;
	}
	
}
