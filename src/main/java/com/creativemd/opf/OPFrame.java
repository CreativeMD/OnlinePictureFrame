package com.creativemd.opf;

import java.util.Arrays;

import com.creativemd.opf.block.BlockPicFrame;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.OPFrameClient;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = OPFrame.modid, version = OPFrame.version, name = "OnlinePictureFrame")
public class OPFrame{
	
	public static final String modid = "opframe";
	public static final String version = "0.1";
	
	public static Block frame = new BlockPicFrame().setBlockName("opFrame");
	
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		OPFrameClient.initClient();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		
		GameRegistry.registerBlock(frame, "opFrame");
		
		GameRegistry.registerTileEntity(TileEntityPicFrame.class, "OPFrameTileEntity");
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			initClient();
		
		GameRegistry.addRecipe(new ItemStack(frame),  new Object[]
				{
				"AXA", "XLX", "AXA", 'X', Blocks.planks, 'L', Items.dye, 'A', Blocks.wool
				});
	}
}
