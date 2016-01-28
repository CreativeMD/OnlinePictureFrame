package com.creativemd.opf.block;

import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
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
import net.minecraft.util.AxisAlignedBB;
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
					downloader = new DownloadThread(url);
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
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }
	
	public int renderDistance = 512;
	
	public String url = "";
	public float sizeX = 1F;
	public float sizeY = 1F;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("url", url);
		nbt.setFloat("sizeX", sizeX);
		nbt.setFloat("sizeY", sizeY);
		nbt.setInteger("render", renderDistance);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		url = nbt.getString("url");
		sizeX = nbt.getFloat("sizeX");
		sizeY = nbt.getFloat("sizeY");
		renderDistance = nbt.getInteger("render");
	}
	
	@Override
	public void getDescriptionNBT(NBTTagCompound nbt)
	{
		super.getDescriptionNBT(nbt);
		nbt.setString("url", url);
		nbt.setFloat("sizeX", sizeX);
		nbt.setFloat("sizeY", sizeY);
		nbt.setInteger("render", renderDistance);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		super.onDataPacket(net, pkt);
		url = pkt.func_148857_g().getString("url");
		sizeX = pkt.func_148857_g().getFloat("sizeX");
		sizeY = pkt.func_148857_g().getFloat("sizeY");
		renderDistance = pkt.func_148857_g().getInteger("render");
		initClient();
    }

}
