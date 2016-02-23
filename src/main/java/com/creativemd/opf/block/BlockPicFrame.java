package com.creativemd.opf.block;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.IGuiCreator;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.core.CreativeCore;
import com.creativemd.opf.client.OPFrameClient;
import com.creativemd.opf.gui.SubContainerPic;
import com.creativemd.opf.gui.SubGuiPic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPicFrame extends BlockContainer implements IGuiCreator {

	public BlockPicFrame() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
		CubeObject cube = new CubeObject(0, 0, 0, 0.05, 1, 1);
		ForgeDirection direction = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
		/*TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityPicFrame)
		{
			if(direction == ForgeDirection.UP)
			{
				cube.minZ -= ((TileEntityPicFrame) te).sizeX-1;
				cube.minY -= ((TileEntityPicFrame) te).sizeY-1;
			}else{
				cube.maxZ += ((TileEntityPicFrame) te).sizeX-1;
				cube.maxY += ((TileEntityPicFrame) te).sizeY-1;
			}
			
			
		}*/
		
        return CubeObject.rotateCube(cube, direction).getAxis().getOffsetBoundingBox(x, y, z);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityPicFrame)
			return ((TileEntityPicFrame) te).getBoundingBox();
		
		ForgeDirection direction = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
		CubeObject cube = new CubeObject(0, 0, 0, 0.05, 1, 1);
        return CubeObject.rotateCube(cube, direction).getAxis().getOffsetBoundingBox(x, y, z);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.planks.getIcon(side, meta);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry)
    {
        
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float offX, float offY, float offZ)
    {
		if(!world.isRemote)
			((EntityPlayerMP)player).openGui(CreativeCore.instance, 0, world, x, y, z);
        return true;
    }
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float offX, float offY, float offZ, int meta)
    {
        return side;
    }
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType()
    {
        return OPFrameClient.modelID;
    }
	
	@Override
	public boolean isNormalCube()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPicFrame();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SubGui getGui(EntityPlayer player, ItemStack stack, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityPicFrame)
			return new SubGuiPic((TileEntityPicFrame) te);
		return null;
	}

	@Override
	public SubContainer getContainer(EntityPlayer player, ItemStack stack, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityPicFrame)
			return new SubContainerPic((TileEntityPicFrame) te, player);
		return null;
	}

}
