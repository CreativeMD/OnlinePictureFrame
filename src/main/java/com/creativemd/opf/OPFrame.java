package com.creativemd.opf;

import com.creativemd.opf.block.BlockPicFrame;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.OPFrameClient;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = OPFrame.modid, version = OPFrame.version, name = "OnlinePictureFrame")
public class OPFrame{
	
	public static final String modid = "opframe";
	public static final String version = "0.1";
	
	public static float sizeLimitation = 1000;
	
	public static Block frame = new BlockPicFrame().setUnlocalizedName("opFrame");
	
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		OPFrameClient.initClient();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		Configuration config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		sizeLimitation = config.getFloat("size-limitation", "limitations", sizeLimitation, 0, 10000, "size in blocks");
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		//GameRegistry.register(frame.setRegistryName("opFrame"));
		
		GameRegistry.registerBlock(frame, "opFrame");
		
		GameRegistry.registerTileEntity(TileEntityPicFrame.class, "OPFrameTileEntity");
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			initClient();
		
		GameRegistry.addRecipe(new ItemStack(frame),  new Object[]
				{
				"AXA", "XLX", "AXA", 'X', Blocks.PLANKS, 'L', Items.IRON_INGOT, 'A', Blocks.WOOL
				});
	}
}
