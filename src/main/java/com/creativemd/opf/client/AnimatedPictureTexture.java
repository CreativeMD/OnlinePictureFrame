package com.creativemd.opf.client;

import com.porpit.lib.GifDecoder;

public class AnimatedPictureTexture extends PictureTexture {
	
	private final int[] textureIDs;
	private final long[] delay;
	private final long duration;
	
	public AnimatedPictureTexture(GifDecoder decoder) {
		super((int) decoder.getFrameSize().getWidth(), (int) decoder.getFrameSize().getHeight());
		textureIDs = new int[decoder.getFrameCount()];
		delay = new long[decoder.getFrameCount()];
		long time = 0;
		for (int i = 0; i < textureIDs.length; i++) {
			textureIDs[i] = DownloadThread.loadTexture(decoder.getFrame(i));
			delay[i] = time;
			time += decoder.getDelay(i);
		}
		duration = time;
	}

	@Override
	public int getTextureID() {
		long time = duration > 0 ? System.currentTimeMillis() % duration : 0;
		int index = 0;
		for (int i = 0; i < delay.length; i++) {
			if(delay[i] >= time)
			{
				index = i;
				break;
			}
		}
		return textureIDs[index];
	}

}
