package com.creativemd.opf.gui;

import javax.vecmath.Vector2f;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiButton;
import com.creativemd.creativecore.common.gui.controls.GuiCheckBox;
import com.creativemd.creativecore.common.gui.controls.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.DownloadThread;
import com.google.common.util.concurrent.ExecutionError;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class SubGuiPic extends SubGui{
	
	public TileEntityPicFrame frame;
	
	public SubGuiPic(TileEntityPicFrame frame) {
		super(200, 200);
		this.frame = frame;
	}
	
	@Override
	public void createControls() {
		GuiTextfield url = new GuiTextfield("url", frame.url, 5, 5, 164, 20);
		url.maxLength = 512;
		controls.add(url);
		controls.add(new GuiTextfield("sizeX", frame.sizeX + "", 5, 30, 40, 20).setFloatOnly());
		controls.add(new GuiTextfield("sizeY", frame.sizeY + "", 50, 30, 40, 20).setFloatOnly());
		
		controls.add(new GuiButton("reX", "x->y", 95, 30, 50));
		controls.add(new GuiButton("reY", "y->x", 145, 30, 50));
		
		controls.add(new GuiCheckBox("flipX", "flip (x-axis)", 5, 50, frame.flippedX));
		controls.add(new GuiCheckBox("flipY", "flip (y-axis)", 80, 50, frame.flippedY));
		
		controls.add(new GuiStateButton("posX", frame.posX, 5, 70, 70, 20, "left (x)", "center (x)", "right (x)"));
		controls.add(new GuiStateButton("posY", frame.posY, 80, 70, 70, 20, "left (y)", "center (y)", "right (y)"));
		
		controls.add(new GuiStateButton("rotation", frame.rotation, 5, 100, 80, 20, "rotation: 0", "rotation: 1", "rotation: 2", "rotation: 3"));
		
		controls.add(new GuiCheckBox("visibleFrame", "visible Frame", 90, 105, frame.visibleFrame));
		
		controls.add(new GuiLabel("render distance (blocks):", 5, 125));
		controls.add(new GuiSteppedSlider("renderDistance", 5, 140, 100, 20, 5, 1024, frame.renderDistance));
		
		controls.add(new GuiButton("Save", 120, 140, 50));
	}
	
	@CustomEventSubscribe
	public void onClicked(ControlClickEvent event)
	{
		if(event.source.is("reX") || event.source.is("reY"))
		{
			GuiTextfield sizeXField = (GuiTextfield) getControl("sizeX");
			GuiTextfield sizeYField = (GuiTextfield) getControl("sizeY");
			
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
		if(event.source.is("Save"))
		{
			NBTTagCompound nbt = new NBTTagCompound();
			GuiTextfield url = (GuiTextfield) getControl("url");
			GuiTextfield sizeX = (GuiTextfield) getControl("sizeX");
			GuiTextfield sizeY = (GuiTextfield) getControl("sizeY");
			
			GuiStateButton buttonPosX = (GuiStateButton) getControl("posX");
			GuiStateButton buttonPosY = (GuiStateButton) getControl("posY");
			GuiStateButton rotation = (GuiStateButton) getControl("rotation");
			
			GuiCheckBox flipX = (GuiCheckBox) getControl("flipX");
			GuiCheckBox flipY = (GuiCheckBox) getControl("flipY");
			GuiCheckBox visibleFrame = (GuiCheckBox) getControl("visibleFrame");
			
			GuiSteppedSlider renderDistance = (GuiSteppedSlider) getControl("renderDistance");
			
			nbt.setByte("posX", (byte) buttonPosX.getState());
			nbt.setByte("posY", (byte) buttonPosY.getState());
			
			nbt.setByte("rotation", (byte) rotation.getState());
			
			nbt.setBoolean("flippedX", flipX.value);
			nbt.setBoolean("flippedY", flipY.value);
			nbt.setBoolean("visibleFrame", visibleFrame.value);
			
			nbt.setInteger("render", (int) renderDistance.value);
			
			nbt.setString("url", url.text);
			float x = 1;
			float y = 1;
			try{
				x = Float.parseFloat(sizeX.text);
			}catch(Exception e){
				x = 1;
			}
			try{
				y = Float.parseFloat(sizeY.text);
			}catch(Exception e){
				y = 1;
			}
			nbt.setFloat("x", x);
			nbt.setFloat("y", y);
			
			sendPacketToServer(0, nbt);
		}
	}

	@Override
	public void drawOverlay(FontRenderer fontRenderer) {
		
	}

}
