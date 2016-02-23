package com.creativemd.opf.block;

import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.opf.client.DownloadThread;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityPicFrame extends TileEntityCreative{
	
	public TileEntityPicFrame() {
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			initClient();
	}
	
	@SideOnly(Side.CLIENT)
	public DownloadThread downloader;
	
	@SideOnly(Side.CLIENT)
	public int textureID;
	
	@SideOnly(Side.CLIENT)
	public boolean failed;
	
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		textureID = -1;
		failed = false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldLoadTexture()
	{
		return !isTextureLoaded() && !failed;
	}
	
	@SideOnly(Side.CLIENT)
	public void loadTexutre()
	{
		if(shouldLoadTexture())
		{
			if(downloader == null)
			{
				Integer id = DownloadThread.loadedImages.get(url);
				
				if(id == null)
				{
					if(!DownloadThread.loadingImages.contains(url))
					{
						DownloadThread.loadingImages.add(url);
						downloader = new DownloadThread(url);
					}
				}
				else
					textureID = id;
			}
			if(downloader != null && downloader.hasFinished())
			{
				if(downloader.hasFailed())
					failed = true;
				else
				{
					textureID = DownloadThread.loadImage(downloader);
				}
				DownloadThread.loadingImages.remove(url);
				downloader = null;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isTextureLoaded()
	{
		return textureID != -1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return Math.pow(renderDistance, 2);
    }
	
	public AxisAlignedBB getBoundingBox()
	{
		/*AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;*/
		CubeObject cube = new CubeObject(0, 0, 0, 0.05, 1, 1);
		
		float sizeX = this.sizeX;
		float sizeY = this.sizeY;
		double offsetX = 0;
		double offsetY = 0;
		
		switch(rotation)
		{
		case 1:
			sizeX = this.sizeY;
			sizeY = -this.sizeX;
			if(posY == 0)
				offsetY += 1;
			else if(posY == 2)
				offsetY -= 1;
			break;
		case 2:
			sizeX = -this.sizeX;
			sizeY = -this.sizeY;
			if(posX == 0)
				offsetX += 1;
			else if(posX == 2)
				offsetX -= 1;
			if(posY == 0)
				offsetY += 1;
			else if(posY == 2)
				offsetY -= 1;
			break;
		case 3:
			sizeX = -this.sizeY;
			sizeY = this.sizeX;
			if(posX == 0)
				offsetX += 1;
			else if(posX == 2)
				offsetX -= 1;
			break;
		}
		
		if(posX == 1)
			offsetX += (-sizeX+1)/2D;
		else if(posX == 2)
			offsetX += -sizeX+1;
		
		
		if(posY == 1)
			offsetY += (-sizeY+1)/2D;
		else if(posY == 2)
			offsetY += -sizeY+1;
		
		ForgeDirection direction = ForgeDirection.getOrientation(getBlockMetadata());
		if(direction == ForgeDirection.UP)
		{
			cube.minZ -= sizeX-1;
			cube.minY -= sizeY-1;
			
			cube.minZ -= offsetX;
			cube.maxZ -= offsetX;
			cube.minY -= offsetY;
			cube.maxY -= offsetY;
		}else{
			cube.maxZ += sizeX-1;
			cube.maxY += sizeY-1;
			
			cube.minZ += offsetX;
			cube.maxZ += offsetX;
			cube.minY += offsetY;
			cube.maxY += offsetY;
		}
		
		cube = new CubeObject(Math.min(cube.minX, cube.maxX), Math.min(cube.minY, cube.maxY), Math.min(cube.minZ, cube.maxZ),
				Math.max(cube.minX, cube.maxX), Math.max(cube.minY, cube.maxY), Math.max(cube.minZ, cube.maxZ));
		
        return CubeObject.rotateCube(cube, direction).getAxis().getOffsetBoundingBox(xCoord, yCoord, zCoord);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return getBoundingBox();
    }
	
	public int renderDistance = 512;
	
	public String url = "";
	public float sizeX = 1F;
	public float sizeY = 1F;
	
	public boolean flippedX;
	public boolean flippedY;
	
	/**0-3 all directions**/
	public byte rotation = 0;
	
	/**0: normal,1: center, 2: -normal**/
	public byte posX = 0;
	/**0: normal,1: center, 2: -normal**/
	public byte posY = 0;
	
	public boolean visibleFrame = true;
	
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("url", url);
		nbt.setFloat("sizeX", sizeX);
		nbt.setFloat("sizeY", sizeY);
		nbt.setInteger("render", renderDistance);
		nbt.setByte("offsetX", posX);
		nbt.setByte("offsetY", posY);
		nbt.setByte("rotation", rotation);
		nbt.setBoolean("visibleFrame", visibleFrame);
		nbt.setBoolean("flippedX", flippedX);
		nbt.setBoolean("flippedY", flippedY);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		url = nbt.getString("url");
		sizeX = nbt.getFloat("sizeX");
		sizeY = nbt.getFloat("sizeY");
		renderDistance = nbt.getInteger("render");
		posX = nbt.getByte("offsetX");
		posY = nbt.getByte("offsetY");
		rotation = nbt.getByte("rotation");
		visibleFrame = nbt.getBoolean("visibleFrame");
		flippedX = nbt.getBoolean("flippedX");
		flippedY = nbt.getBoolean("flippedY");
	}
	
	@Override
	public void getDescriptionNBT(NBTTagCompound nbt)
	{
		super.getDescriptionNBT(nbt);
		nbt.setString("url", url);
		nbt.setFloat("sizeX", sizeX);
		nbt.setFloat("sizeY", sizeY);
		nbt.setInteger("render", renderDistance);
		nbt.setByte("offsetX", posX);
		nbt.setByte("offsetY", posY);
		nbt.setByte("rotation", rotation);
		nbt.setBoolean("visibleFrame", visibleFrame);
		nbt.setBoolean("flippedX", flippedX);
		nbt.setBoolean("flippedY", flippedY);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		super.onDataPacket(net, pkt);
		url = pkt.func_148857_g().getString("url");
		sizeX = pkt.func_148857_g().getFloat("sizeX");
		sizeY = pkt.func_148857_g().getFloat("sizeY");
		renderDistance = pkt.func_148857_g().getInteger("render");
		posX = pkt.func_148857_g().getByte("offsetX");
		posY = pkt.func_148857_g().getByte("offsetY");
		rotation = pkt.func_148857_g().getByte("rotation");
		visibleFrame = pkt.func_148857_g().getBoolean("visibleFrame");
		flippedX = pkt.func_148857_g().getBoolean("flippedX");
		flippedY = pkt.func_148857_g().getBoolean("flippedY");
		initClient();
		updateRender();
    }

}
