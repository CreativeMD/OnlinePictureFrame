package com.creativemd.opf.gui;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.opf.OPFrameConfig;
import com.creativemd.opf.block.TileEntityPicFrame;
import com.creativemd.opf.client.DownloadThread;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SubGuiPic extends SubGui {
	
	public TileEntityPicFrame frame;
	
	public boolean editFacing;
	
	public float scaleMultiplier;
	
	public GuiTextfield url;
	public GuiButton save;
	
	public SubGuiPic(TileEntityPicFrame frame) {
		this(frame, false, 16);
	}
	
	public SubGuiPic(TileEntityPicFrame frame, boolean editFacing, int scaleSize) {
		super(200, editFacing ? 220 : 200);
		this.frame = frame;
		this.editFacing = editFacing;
		this.scaleMultiplier = 1F / (scaleSize);
	}
	
	@Override
	public void createControls() {
		url = new GuiUrlTextfield(this, "url", frame.url, 0, 0, 194, 16);
		url.maxLength = 512;
		controls.add(url);
		controls.add(new GuiLabel(translate(frame.error != null ? frame.error : ""), 0, 20, ColorUtils.RED));
		controls.add(new GuiButton("in-size-x", "<", 49, 30, 5, 12) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				GuiTextfield sizeX = (GuiTextfield) get("sizeX");
				float width = 1;
				try {
					width = Float.parseFloat(sizeX.text);
				} catch (Exception e) {
					width = 1;
				}
				int scaled = (int) (width / scaleMultiplier);
				scaled++;
				sizeX.text = Float.toString(scaled * scaleMultiplier);
			}
		}.setRotation(90));
		controls.add(new GuiButton("de-size-x", ">", 49, 40, 5, 12) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				GuiTextfield sizeX = (GuiTextfield) get("sizeX");
				float width = 1;
				try {
					width = Float.parseFloat(sizeX.text);
				} catch (Exception e) {
					width = 1;
				}
				int scaled = (int) (width / scaleMultiplier);
				scaled--;
				sizeX.text = Float.toString(scaled * scaleMultiplier);
			}
		}.setRotation(90));
		
		controls.add(new GuiButton("in-size-y", "<", 145, 30, 5, 12) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				GuiTextfield sizeY = (GuiTextfield) get("sizeY");
				float height = 1;
				try {
					height = Float.parseFloat(sizeY.text);
				} catch (Exception e) {
					height = 1;
				}
				int scaled = (int) (height / scaleMultiplier);
				scaled++;
				sizeY.text = Float.toString(scaled * scaleMultiplier);
			}
		}.setRotation(90));
		controls.add(new GuiButton("de-size-y", ">", 145, 40, 5, 12) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				GuiTextfield sizeY = (GuiTextfield) get("sizeY");
				float height = 1;
				try {
					height = Float.parseFloat(sizeY.text);
				} catch (Exception e) {
					height = 1;
				}
				int scaled = (int) (height / scaleMultiplier);
				scaled--;
				sizeY.text = Float.toString(scaled * scaleMultiplier);
			}
		}.setRotation(90));
		
		controls.add(new GuiTextfield("sizeX", frame.sizeX + "", 0, 33, 40, 15).setFloatOnly());
		controls.add(new GuiTextfield("sizeY", frame.sizeY + "", 96, 33, 40, 15).setFloatOnly());
		
		controls.add(new GuiButton("reX", "x->y", 62, 33, 25, 15) {
			@Override
			public void onClicked(int x, int y, int button) {
			}
		});
		
		controls.add(new GuiButton("reY", "y->x", 158, 33, 25, 15) {
			@Override
			public void onClicked(int x, int y, int button) {
			}
		});
		
		controls.add(new GuiCheckBox("flipX", "flip (x-axis)", 0, 50, frame.flippedX));
		controls.add(new GuiCheckBox("flipY", "flip (y-axis)", 75, 50, frame.flippedY));
		
		controls.add(new GuiStateButton("posX", frame.posX, 0, 70, 70, "left (x)", "center (x)", "right (x)"));
		controls.add(new GuiStateButton("posY", frame.posY, 80, 70, 70, "top (y)", "center (y)", "bottom (y)"));
		
		controls.add(new GuiStateButton("rotation", frame.rotation, 0, 93, 80, 10, "rotation: 0", "rotation: 1", "rotation: 2", "rotation: 3"));
		
		controls.add(new GuiCheckBox("visibleFrame", "visible Frame", 90, 91, frame.visibleFrame));
		
		controls.add(new GuiLabel("transparency:", 0, 110));
		controls.add(new GuiAnalogeSlider("transparency", 80, 112, 109, 5, frame.transparency, 0, 1));
		
		controls.add(new GuiLabel("brightness:", 0, 122));
		controls.add(new GuiAnalogeSlider("brightness", 80, 124, 109, 5, frame.brightness, 0, 1));
		
		controls.add(new GuiLabel("rotation (h):", 0, 134));
		controls.add(new GuiAnalogeSlider("rotX", 67, 136, 122, 5, frame.rotationX, -90, 90));
		
		controls.add(new GuiLabel("rotation (v):", 0, 146));
		controls.add(new GuiAnalogeSlider("rotY", 67, 148, 122, 5, frame.rotationY, -90, 90));
		
		controls.add(new GuiLabel("render distance (blocks):", 0, 160));
		controls.add(new GuiSteppedSlider("renderDistance", 0, 174, 80, 14, frame.renderDistance, 5, 1024));
		
		controls.add(new GuiButton("reload", 90, 174) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				synchronized (DownloadThread.LOCK) {
					if (GuiScreen.isShiftKeyDown())
						DownloadThread.loadedImages.clear();
					else {
						GuiTextfield url = (GuiTextfield) get("url");
						DownloadThread.loadedImages.remove(url.text);
					}
					frame.failed = false;
					frame.texture = null;
					frame.error = null;
				}
			}
		}.setCustomTooltip("Hold shift to reload all"));
		
		save = new GuiButton("Save", 140, 174, 50) {
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
				
				GuiAnalogeSlider transparency = (GuiAnalogeSlider) get("transparency");
				GuiAnalogeSlider brightness = (GuiAnalogeSlider) get("brightness");
				
				nbt.setByte("posX", (byte) buttonPosX.getState());
				nbt.setByte("posY", (byte) buttonPosY.getState());
				
				nbt.setByte("rotation", (byte) rotation.getState());
				
				nbt.setBoolean("flippedX", flipX.value);
				nbt.setBoolean("flippedY", flipY.value);
				nbt.setBoolean("visibleFrame", visibleFrame.value);
				
				nbt.setInteger("render", (int) renderDistance.value);
				nbt.setFloat("rotX", (float) rotX.value);
				nbt.setFloat("rotY", (float) rotY.value);
				
				nbt.setFloat("transparency", (float) transparency.value);
				nbt.setFloat("brightness", (float) brightness.value);
				
				nbt.setString("url", url.text);
				float posX = 1;
				float posY = 1;
				try {
					posX = Float.parseFloat(sizeX.text);
				} catch (Exception e) {
					posX = 1;
				}
				try {
					posY = Float.parseFloat(sizeY.text);
				} catch (Exception e) {
					posY = 1;
				}
				nbt.setFloat("x", posX);
				nbt.setFloat("y", posY);
				
				nbt.setInteger("type", 0);
				
				if (editFacing) {
					GuiStateButton facing = (GuiStateButton) get("facing");
					nbt.setInteger("facing", facing.getState());
				}
				sendPacketToServer(nbt);
			}
		};
		save.setEnabled(OPFrameConfig.getGlobalLimitations().canUse(mc.player, url.text));
		controls.add(save);
		
		if (editFacing) {
			String[] names = new String[EnumFacing.VALUES.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = EnumFacing.VALUES[i].getName();
			}
			controls.add(new GuiStateButton("facing", frame.getBlockMetadata(), 0, 196, 50, names));
		}
	}
	
	@CustomEventSubscribe
	public void onClicked(GuiControlClickEvent event) {
		if (event.source.is("reX") || event.source.is("reY")) {
			GuiTextfield sizeXField = (GuiTextfield) get("sizeX");
			GuiTextfield sizeYField = (GuiTextfield) get("sizeY");
			
			float x = 1;
			try {
				x = Float.parseFloat(sizeXField.text);
			} catch (Exception e) {
				x = 1;
			}
			
			float y = 1;
			try {
				y = Float.parseFloat(sizeYField.text);
			} catch (Exception e) {
				y = 1;
			}
			
			if (frame.texture != null) {
				if (event.source.is("reX")) {
					sizeYField.text = "" + (frame.texture.height / (frame.texture.width / x));
				} else {
					sizeXField.text = "" + (frame.texture.width / (frame.texture.height / y));
				}
			}
		}
	}
	
}
