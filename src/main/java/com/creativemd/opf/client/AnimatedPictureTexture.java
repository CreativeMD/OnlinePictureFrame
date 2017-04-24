package com.creativemd.opf.client;

public class AnimatedPictureTexture extends PictureTexture {

	private final int[] textureIDs;
	private final long[] delay;
	private final long duration;

	private int completedFrames;
	private ProcessedImageData imageData;

	public AnimatedPictureTexture(ProcessedImageData image) {
		super(image.getWidth(), image.getHeight());
		imageData = image;
		textureIDs = new int[image.getFrameCount()];
		delay = image.getDelay();
		duration = image.getDuration();
		for (int i = 0; i < textureIDs.length; i++) {
			textureIDs[i] = -1;
		}
	}

	@Override
	public int getTextureID() {
		long time = duration > 0 ? System.currentTimeMillis() % duration : 0;
		int index = 0;
		for (int i = 0; i < delay.length; i++) {
			if (delay[i] >= time) {
				index = i;
				break;
			}
		}
		int id = textureIDs[index];
		if (id == -1) {
			id = imageData.uploadFrame(index);
			textureIDs[index] = id;
			if (++completedFrames >= imageData.getFrameCount()) {
				//Unload imageData after all frames have been loaded
				imageData = null;
			}
		}
		return id;
	}
}
