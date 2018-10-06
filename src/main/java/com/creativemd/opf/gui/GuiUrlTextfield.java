package com.creativemd.opf.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.controls.gui.GuiTextfield;
import com.creativemd.opf.OPFrameConfig;
import com.google.common.collect.Lists;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class GuiUrlTextfield extends GuiTextfield {
	public static final Style DISABLED = new Style("disabled", new ColoredDisplayStyle(50, 0, 0), new ColoredDisplayStyle(150, 90, 90), new ColoredDisplayStyle(180, 100, 100), new ColoredDisplayStyle(220, 198, 198), new ColoredDisplayStyle(50, 0, 0, 100));
	public static final Style WARNING = new Style("warning", new ColoredDisplayStyle(50, 50, 0), new ColoredDisplayStyle(150, 150, 90), new ColoredDisplayStyle(180, 180, 100), new ColoredDisplayStyle(220, 220, 198), new ColoredDisplayStyle(50, 50, 0, 100));
	private SubGuiPic gui;
	
	public GuiUrlTextfield(SubGuiPic gui, String name, String text, int x, int y, int width, int height) {
		super(name, text, x, y, width, height);
		this.gui = gui;
	}
	
	@Override
	protected void renderBackground(GuiRenderHelper helper, Style style) {
		if (!canUse(true)) {
			style = OPFrameConfig.getGlobalLimitations().whitelistEnabled ? DISABLED : WARNING;
		}
		super.renderBackground(helper, style);
	}
	
	@Override
	public boolean onKeyPressed(char character, int key) {
		boolean pressed = super.onKeyPressed(character, key);
		gui.save.setEnabled(canUse(false));
		return pressed;
	}
	
	@Override
	public ArrayList<String> getTooltip() {
		if (!canUse(false)) {
			return Lists.newArrayList(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + I18n.translateToLocal("label.opframe.not_whitelisted.name"));
		} else if (!canUse(true)) {
			return Lists.newArrayList(TextFormatting.GOLD + I18n.translateToLocal("label.opframe.whitelist_warning.name"));
		}
		return Lists.newArrayList();
	}
	
	protected boolean canUse(boolean ignoreToggle) {
		return OPFrameConfig.getGlobalLimitations().canUse(mc.player, text, ignoreToggle);
	}
}
