package com.creativemd.opf.gui;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.controls.gui.GuiTextfield;
import com.creativemd.opf.OPFrameConfig;

public class GuiUrlTextfield extends GuiTextfield {
	public static final Style DISABLED = new Style("disabled", new ColoredDisplayStyle(50, 0, 0), new ColoredDisplayStyle(150, 90, 90), new ColoredDisplayStyle(180, 100, 100), new ColoredDisplayStyle(220, 198, 198), new ColoredDisplayStyle(50, 0, 0, 100));
	private SubGuiPic gui;

	public GuiUrlTextfield(SubGuiPic gui, String name, String text, int x, int y, int width, int height) {
		super(name, text, x, y, width, height);
		this.gui = gui;
	}

	@Override
	protected void renderBackground(GuiRenderHelper helper, Style style) {
		super.renderBackground(helper, OPFrameConfig.getGlobalLimitations().canUse(mc.player, text) ? style : DISABLED);
	}

	@Override
	public boolean onKeyPressed(char character, int key) {
		boolean pressed = super.onKeyPressed(character, key);
		gui.save.setEnabled(OPFrameConfig.getGlobalLimitations().canUse(mc.player, text));
		return pressed;
	}
}
