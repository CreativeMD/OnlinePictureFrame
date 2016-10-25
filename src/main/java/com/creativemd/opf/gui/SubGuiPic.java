package com.creativemd.opf.gui;

import javax.vecmath.Vector2f;

import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.DownloadThread;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SubGuiPic extends SubGui{
	
	public TileEntityPicFrame frame;
	
	public boolean editFacing;
	
	public SubGuiPic(TileEntityPicFrame frame) {
		this(frame, false);
	}
	
	public SubGuiPic(TileEntityPicFrame frame, boolean editFacing) {
		super(200, editFacing ? 220 : 200);
		this.frame = frame;
		this.editFacing = editFacing;
	}
	
	@Override
	public void createControls() {
		GuiTextfield url = new GuiTextfield("url", frame.url, 0, 0, 194, 16);
		url.maxLength = 512;
		controls.add(url);
		controls.add(new GuiTextfield("sizeX", frame.sizeX + "", 0, 30, 40, 16).setFloatOnly());
		controls.add(new GuiTextfield("sizeY", frame.sizeY + "", 47, 30, 40, 16).setFloatOnly());
		controls.add(new GuiButton("reX", "x->y", 94, 30, 50){
			@Override
			public void onClicked(int x, int y, int button) {
			}
		});
		controls.add(new GuiButton("reY", "y->x", 144, 30, 50){
			@Override
			public void onClicked(int x, int y, int button) {
			}
		});
		
		controls.add(new GuiCheckBox("flipX", "flip (x-axis)", 0, 50, frame.flippedX));
		controls.add(new GuiCheckBox("flipY", "flip (y-axis)", 75, 50, frame.flippedY));
		
		controls.add(new GuiStateButton("posX", frame.posX, 0, 70, 70, "left (x)", "center (x)", "right (x)"));
		controls.add(new GuiStateButton("posY", frame.posY, 80, 70, 70, "left (y)", "center (y)", "right (y)"));
		
		controls.add(new GuiStateButton("rotation", frame.rotation, 0, 100, 80, "rotation: 0", "rotation: 1", "rotation: 2", "rotation: 3"));
		
		controls.add(new GuiCheckBox("visibleFrame", "visible Frame", 90, 100, frame.visibleFrame));
		
		controls.add(new GuiLabel("rotation (h):", 0, 124));
		controls.add(new GuiAnalogeSlider("rotX", 67, 122, 122, 12, frame.rotationX, -90, 90));
		
		controls.add(new GuiLabel("rotation (v):", 0, 143));
		controls.add(new GuiAnalogeSlider("rotY", 67, 141, 122, 12, frame.rotationY, -90, 90));
		
		controls.add(new GuiLabel("render distance (blocks):", 0, 160));
		controls.add(new GuiSteppedSlider("renderDistance", 0, 174, 100, 14, frame.renderDistance, 5, 1024));
		
		controls.add(new GuiButton("Save", 120, 174, 50){
			@Override
			public void onClicked(int x, int y, int button) {
				NBTTagCompound nbt = new NBTTagCompound();
				GuiTextfield url = (GuiTextfield) get("url");
				GuiTextfield sizeX = (GuiTextfield) get("sizeX");
				GuiTextfield sizeY = (GuiTextfield) get("sizeY");
				
				GuiStateButton buttonPosX = (GuiStateButton) get("posX");
				GuiStateButton buttonPosY = (GuiStateButton) get("posY");
				GuiStateButton rotation = (GuiStateButton) get("rotation");
				
				GuiCheckBox flipX = (GuiCheckBox) get("flipX");
				GuiCheckBox flipY = (GuiCheckBox) get("flipY");
				GuiCheckBox visibleFrame = (GuiCheckBox) get("visibleFrame");
				
				GuiSteppedSlider renderDistance = (GuiSteppedSlider) get("renderDistance");
				
				GuiAnalogeSlider rotX = (GuiAnalogeSlider) get("rotX");
				GuiAnalogeSlider rotY = (GuiAnalogeSlider) get("rotY");
				
				nbt.setByte("posX", (byte) buttonPosX.getState());
				nbt.setByte("posY", (byte) buttonPosY.getState());
				
				nbt.setByte("rotation", (byte) rotation.getState());
				
				nbt.setBoolean("flippedX", flipX.value);
				nbt.setBoolean("flippedY", flipY.value);
				nbt.setBoolean("visibleFrame", visibleFrame.value);
				
				nbt.setInteger("render", (int) renderDistance.value);
				nbt.setFloat("rotX", rotX.value);
				nbt.setFloat("rotY", rotY.value);
				
				nbt.setString("url", url.text);
				float posX = 1;
				float posY = 1;
				try{
					posX = Float.parseFloat(sizeX.text);
				}catch(Exception e){
					posX = 1;
				}
				try{
					posY = Float.parseFloat(sizeY.text);
				}catch(Exception e){
					posY = 1;
				}
				nbt.setFloat("x", posX);
				nbt.setFloat("y", posY);
				
				nbt.setInteger("type", 0);
				
				if(editFacing)
				{
					GuiStateButton facing = (GuiStateButton) get("facing");
					nbt.setInteger("facing", facing.getState());
				}
				sendPacketToServer(nbt);
			}
		});
		
		if(editFacing)
		{
			String[] names = new String[EnumFacing.VALUES.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = EnumFacing.VALUES[i].getName();
			}
			controls.add(new GuiStateButton("facing", frame.getBlockMetadata(), 0, 196, 50, names));
		}
	}
	
	@CustomEventSubscribe
	public void onClicked(GuiControlClickEvent event)
	{
		if(event.source.is("reX") || event.source.is("reY"))
		{
			GuiTextfield sizeXField = (GuiTextfield) get("sizeX");
			GuiTextfield sizeYField = (GuiTextfield) get("sizeY");
			
			float x = 1;
			try{
				x = Float.parseFloat(sizeXField.text);
			}catch(Exception e){
				x = 1;
			}
			
			float y = 1;
			try{
				y = Float.parseFloat(sizeYField.text);
			}catch(Exception e){
				y = 1;
			}
			
			Vector2f size = DownloadThread.loadedImagesSize.get(frame.url);
			if(size != null)
			{
				if(event.source.is("reX"))
				{
					sizeYField.text = "" + (size.y/(size.x/x));
				}else{
					sizeXField.text = "" + (size.x/(size.y/y));
				}
			}
		}
	}

}
