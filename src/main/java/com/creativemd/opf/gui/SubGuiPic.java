package com.creativemd.opf.gui;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiButton;
import com.creativemd.creativecore.common.gui.controls.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.google.common.util.concurrent.ExecutionError;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiPic extends SubGui{
	
	public TileEntityPicFrame frame;
	
	public SubGuiPic(TileEntityPicFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public void createControls() {
		controls.add(new GuiTextfield("url", frame.url, 5, 5, 100, 20));
		controls.add(new GuiTextfield("sizeX", frame.sizeX + "", 5, 30, 40, 20));
		controls.add(new GuiTextfield("sizeY", frame.sizeY + "", 60, 30, 40, 20));
		
		controls.add(new GuiButton("Save", 100, 100, 50));
	}
	
	@CustomEventSubscribe
	public void onClicked(ControlClickEvent event)
	{
		if(event.source.is("Save"))
		{
			NBTTagCompound nbt = new NBTTagCompound();
			GuiTextfield url = (GuiTextfield) getControl("url");
			GuiTextfield sizeX = (GuiTextfield) getControl("sizeX");
			GuiTextfield sizeY = (GuiTextfield) getControl("sizeY");
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
