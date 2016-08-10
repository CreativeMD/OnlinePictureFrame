package com.creativemd.opf.block;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.creativemd.creativecore.client.rendering.model.ICreativeRendered;
import com.creativemd.creativecore.common.block.TileEntityState;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.RenderCubeObject;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.creativecore.gui.opener.IGuiCreator;
import com.creativemd.opf.gui.SubContainerPic;
import com.creativemd.opf.gui.SubGuiPic;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPicFrame extends BlockContainer implements IGuiCreator, ICreativeRendered {
	
	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public BlockPicFrame() {
		super(Material.IRON);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.EAST));
		setCreativeTab(CreativeTabs.DECORATIONS);
	}	    
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		return new TileEntityState(state, world.getTileEntity(pos));
    }
	
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
	@Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        //worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
	    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
	@Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
	@Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
	@Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	/**
     * Checks if an IBlockState represents a block that is opaque and a full cube.
     */
	@Override
    public boolean isFullyOpaque(IBlockState state)
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
		CubeObject cube = new CubeObject(0, 0, 0, 0.05F, 1, 1);
		EnumFacing direction = blockState.getValue(FACING);		
        return CubeObject.rotateCube(cube, direction).getAxis(); //.offset(pos);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		TileEntity te = source.getTileEntity(pos);
		if(te instanceof TileEntityPicFrame)
			return ((TileEntityPicFrame) te).getBoundingBox();
		
		CubeObject cube = new CubeObject(0, 0, 0, 0.05F, 1, 1);
		EnumFacing direction = state.getValue(FACING);		
        return CubeObject.rotateCube(cube, direction).getAxis();//.offset(pos);
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		GuiHandler.openGui(playerIn, worldIn, pos);
        return true;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPicFrame();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SubGui getGui(EntityPlayer player, ItemStack stack, World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityPicFrame)
			return new SubGuiPic((TileEntityPicFrame) te);
		return null;
	}

	@Override
	public SubContainer getContainer(EntityPlayer player, ItemStack stack, World world, BlockPos pos,
			IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityPicFrame)
			return new SubContainerPic((TileEntityPicFrame) te, player);
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<RenderCubeObject> getRenderingCubes(IBlockState state, TileEntity te, ItemStack stack) {
		ArrayList<RenderCubeObject> cubes = new ArrayList<RenderCubeObject>();
		RenderCubeObject cube = new RenderCubeObject(0, 0, 0, 0.03F, 1, 1, Blocks.PLANKS);
		if(te instanceof TileEntityPicFrame && ((TileEntityPicFrame) te).visibleFrame)
			cube = new RenderCubeObject(CubeObject.rotateCube(cube, state.getValue(FACING)), cube);
		cubes.add(cube);
		return cubes;
	}

}
