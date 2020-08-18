package com.creativemd.opf;

import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.opf.block.BlockPicFrame;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.OPFrameClient;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = OPFrame.modid, version = OPFrame.version, name = "OnlinePictureFrame", acceptedMinecraftVersions = "", dependencies = "required-before:creativecore", guiFactory = "com.creativemd.opf.OpFrameSettings")
@Mod.EventBusSubscriber
public class OPFrame {
	
	public static final String modid = "opframe";
	public static final String version = "1.4.0";
	
	public static Block frame = new BlockPicFrame().setUnlocalizedName("opFrame").setRegistryName("opFrame");
	
	public static OPFrameConfig CONFIG;
	
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		OPFrameClient.initClient();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(OPFrame.class);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(frame);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(new ItemBlock(frame).setRegistryName(frame.getRegistryName()));
		
		if (FMLCommonHandler.instance().getSide().isClient())
			initClient();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		CreativeConfigRegistry.ROOT.registerValue(modid, CONFIG = new OPFrameConfig());
		
		GameRegistry.registerTileEntity(TileEntityPicFrame.class, "OPFrameTileEntity");
	}
}
