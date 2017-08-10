package com.creativemd.opf;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.littletiles.common.gui.handler.LittleGuiHandler;
import com.creativemd.littletiles.common.ingredients.BlockIngredient;
import com.creativemd.littletiles.common.tiles.LittleTile;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreviewHandler;
import com.creativemd.opf.block.BlockLittlePicFrame;
import com.creativemd.opf.block.BlockPicFrame;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.OPFrameClient;
import com.creativemd.opf.gui.SubContainerPic;
import com.creativemd.opf.gui.SubGuiPic;
import com.creativemd.opf.little.LittleOpFrame;
import com.creativemd.opf.little.LittleOpPreview;
import com.creativemd.opf.little.LittlePlacedOpFrame;
import com.creativemd.opf.packet.OPFrameConfigPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = OPFrame.modid, version = OPFrame.version, name = "OnlinePictureFrame",acceptedMinecraftVersions="")
@Mod.EventBusSubscriber
public class OPFrame{
	
	public static final String modid = "opframe";
	public static final String version = "0.1";

	public static Block frame = new BlockPicFrame().setUnlocalizedName("opFrame").setRegistryName("opFrame");
	public static Block littleFrame;
	
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		OPFrameClient.initClient();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		CreativeCorePacket.registerPacket(OPFrameConfigPacket.class, "OPFCfg");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {		
		GameRegistry.registerWithItem(frame);
		
		GameRegistry.registerTileEntity(TileEntityPicFrame.class, "OPFrameTileEntity");
		
		if(Loader.isModLoaded("littletiles"))
			loadLittleTiles();
		
		if(FMLCommonHandler.instance().getSide().isClient())
			initClient();
		
		GameRegistry.addRecipe(new ItemStack(frame),
				"AXA",
				"XLX",
				"AXA",
				'X', Blocks.PLANKS, 'L', Items.IRON_INGOT, 'A', Blocks.WOOL);
	}
	
	@Method(modid = "littletiles")
	public void loadLittleTiles()
	{
		littleFrame = new BlockLittlePicFrame().setUnlocalizedName("littleOpFrame").setRegistryName("littleOpFrame");
		
		GameRegistry.registerWithItem(littleFrame);
		
		GuiHandler.registerGuiHandler("littleOpFrame", new LittleGuiHandler(){

			@Override
			public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt, LittleTile tile) {
				if(tile instanceof LittleOpFrame)
					return new SubContainerPic((TileEntityPicFrame) ((LittleOpFrame) tile).getTileEntity(), player, tile);
				return null;
			}

			@Override
			@SideOnly(Side.CLIENT)
			public SubGui getGui(EntityPlayer player, NBTTagCompound nbt, LittleTile tile) {
				if(tile instanceof LittleOpFrame)
					return new SubGuiPic((TileEntityPicFrame) ((LittleOpFrame) tile).getTileEntity(), true, LittleTile.gridSize);
				return null;
			}
			
		});
		GameRegistry.addRecipe(new ItemStack(littleFrame),
				"AX",
				"XL",
				'X',
				Blocks.PLANKS, 'L', Items.IRON_INGOT, 'A', Blocks.WOOL);
		LittleTile.registerLittleTile(LittleOpFrame.class, "OpFrame", new LittleTilePreviewHandler.DefaultPreviewHandler(){
			@Override
			public BlockIngredient getBlockIngredient(LittleTilePreview preview)
			{
				return new BlockIngredient(preview.getPreviewBlock(), 0, preview.size.getPercentVolume());
			}
			
		});
		
		LittleTilePreview.registerPreviewType("opPreview", LittleOpPreview.class);
		LittleTilePreview.registerPreviewType("opPlacedPreview", LittlePlacedOpFrame.class);
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (!player.world.isRemote && player instanceof EntityPlayerMP) {
			PacketHandler.sendPacketToPlayer(new OPFrameConfigPacket(), (EntityPlayerMP) player);
		}
	}
}
