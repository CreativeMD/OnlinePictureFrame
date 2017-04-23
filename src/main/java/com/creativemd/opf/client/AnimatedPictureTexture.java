package com.creativemd.opf.client;

import com.porpit.lib.GifDecoder;

public class AnimatedPictureTexture extends PictureTexture {

    private final int[] textureIDs;
    private final long[] delay;
    private final long duration;

    private int completedFrames;
    private GifDecoder gif;

    public AnimatedPictureTexture(GifDecoder decoder) {
        super((int) decoder.getFrameSize().getWidth(), (int) decoder.getFrameSize().getHeight());
        gif = decoder;
        textureIDs = new int[decoder.getFrameCount()];
        delay = new long[decoder.getFrameCount()];
        long time = 0;
        for (int i = 0; i < textureIDs.length; i++) {
            textureIDs[i] = -1;
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
            if (delay[i] >= time) {
                index = i;
                break;
            }
        }
        int id = textureIDs[index];
        if (id == -1) {
            id = DownloadThread.loadTexture(gif.getFrame(index));
            textureIDs[index] = id;
            if (++completedFrames >= gif.getFrameCount()) {
                gif = null;
            }
        }
        return id;
    }
}
