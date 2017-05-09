package com.creativemd.opf.block;

import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.opf.client.DownloadThread;
import com.creativemd.opf.client.PictureTexture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPicFrame extends TileEntityCreative implements ITickable{
	
	public TileEntityPicFrame() {
		if(FMLCommonHandler.instance().getSide().isClient())
			initClient();
	}
	
	@SideOnly(Side.CLIENT)
	public DownloadThread downloader;
	
	@SideOnly(Side.CLIENT)
	public PictureTexture texture;
	
	@SideOnly(Side.CLIENT)
	public boolean failed;
	
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		texture = null;
		failed = false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldLoadTexture()
	{
		return !isTextureLoaded() && !failed;
	}
	
	@SideOnly(Side.CLIENT)
	public void loadTexture() {
		texture=null;
		if (shouldLoadTexture()) {
			if (downloader == null && DownloadThread.activeDownloads < DownloadThread.MAXIMUM_ACTIVE_DOWNLOADS) {
				PictureTexture loadedTexture = DownloadThread.loadedImages.get(url);

				if (loadedTexture == null) {
					boolean startDownloader = false;
					synchronized (DownloadThread.LOCK) {
						if (!DownloadThread.loadingImages.contains(url)) {
							DownloadThread.loadingImages.add(url);
							startDownloader = true;
						}
					}
					if (startDownloader) {
						downloader = new DownloadThread(url,this.pos,this.getWorld().provider.getDimension());
					}
				}
				else {
					texture = loadedTexture;
				}
			}
			if (downloader != null && downloader.hasFinished()) {
				if (downloader.hasFailed()) {
					failed = true;
				}
				else {
					texture = DownloadThread.loadImage(downloader);
				}
				synchronized (DownloadThread.LOCK) {
					DownloadThread.loadingImages.remove(url);
				}
				downloader = null;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isTextureLoaded()
	{
		return texture != null&&DownloadThread.loadedImages.containsKey(url);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return Math.pow(renderDistance, 2);
    }
	
	public static AxisAlignedBB getBoundingBox(TileEntityPicFrame frame, int meta)
	{
		/*AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;*/
		CubeObject cube = new CubeObject(0, 0, 0, 0.05F, 1, 1);
		
		float sizeX = frame.sizeX;
		if(sizeX == 0)
			sizeX = 1;
		float sizeY = frame.sizeY;
		if(sizeY == 0)
			sizeY = 1;
		double offsetX = 0;
		double offsetY = 0;
		
		switch(frame.rotation)
		{
		case 1:
			sizeX = frame.sizeY;
			sizeY = -frame.sizeX;
			if(frame.posY == 0)
				offsetY += 1;
			else if(frame.posY == 2)
				offsetY -= 1;
			break;
		case 2:
			sizeX = -frame.sizeX;
			sizeY = -frame.sizeY;
			if(frame.posX == 0)
				offsetX += 1;
			else if(frame.posX == 2)
				offsetX -= 1;
			if(frame.posY == 0)
				offsetY += 1;
			else if(frame.posY == 2)
				offsetY -= 1;
			break;
		case 3:
			sizeX = -frame.sizeY;
			sizeY = frame.sizeX;
			if(frame.posX == 0)
				offsetX += 1;
			else if(frame.posX == 2)
				offsetX -= 1;
			break;
		}
		
		if(frame.posX == 1)
			offsetX += (-sizeX+1)/2D;
		else if(frame.posX == 2)
			offsetX += -sizeX+1;
		
		
		if(frame.posY == 1)
			offsetY += (-sizeY+1)/2D;
		else if(frame.posY == 2)
			offsetY += -sizeY+1;
		
		EnumFacing direction = EnumFacing.getFront(meta);
		if(direction == EnumFacing.UP)
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
		
        return CubeObject.rotateCube(cube, direction).getAxis();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return getBoundingBox(this, getBlockMetadata()).offset(pos);
    }
	
	public int renderDistance = 128;
	
	public String url = "";
	public float sizeX = 1F;
	public float sizeY = 1F;
	
	public boolean flippedX;
	public boolean flippedY;
	
	/**-90 to 90**/
	public float rotationX;
	/**-90 to 90**/
	public float rotationY;
	
	/**0-3 all directions**/
	public byte rotation = 0;
	
	/**0: normal,1: center, 2: -normal**/
	public byte posX = 0;
	/**0: normal,1: center, 2: -normal**/
	public byte posY = 0;
	
	public boolean visibleFrame = true;
	
	public void setOwner(String playername)
	{
		owner=playername;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
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
		nbt.setFloat("rotX", rotationX);
		nbt.setFloat("rotY", rotationY);
		return nbt;
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
		rotationX = nbt.getFloat("rotX");
		rotationY = nbt.getFloat("rotY");
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
		nbt.setFloat("rotX", rotationX);
		nbt.setFloat("rotY", rotationY);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void receiveUpdatePacket(NBTTagCompound nbt)
	{
		super.receiveUpdatePacket(nbt);
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
		rotationX = nbt.getFloat("rotX");
		rotationY = nbt.getFloat("rotY");
		initClient();
		updateRender();
    }
	@Override
	public void update() {
		if (this.getWorld().isRemote) {
			tickTexture();
		}
	}

	private void tickTexture() {
		if (texture != null) {
			texture.tick();
		}
	}
}

