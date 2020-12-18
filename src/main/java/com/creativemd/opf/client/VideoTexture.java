package com.creativemd.opf.client;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;

public class VideoTexture extends PictureTexture {
	
	public CallbackMediaPlayerComponent player;
	public ByteBuffer buffer;
	public int texture;
	private AtomicBoolean needsUpdate = new AtomicBoolean(false);
	private boolean first = true;
	
	public VideoTexture(String url) {
		super(100, 100);
		texture = GlStateManager.generateTexture();
		
		player = new CallbackMediaPlayerComponent(new MediaPlayerFactory("--quiet"), null, null, false, new RenderCallback() {
			
			@Override
			public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
				buffer = nativeBuffers[0];
				needsUpdate.set(true);
			}
		}, new BufferFormatCallback() {
			
			@Override
			public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
				VideoTexture.this.width = sourceWidth;
				VideoTexture.this.height = sourceHeight;
				VideoTexture.this.first = true;
				return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[] { sourceWidth * 4 }, new int[] { sourceHeight });
			}
			
			@Override
			public void allocatedBuffers(ByteBuffer[] buffers) {
				
			}
			
		}, null);
		player.mediaPlayer().submit(() -> {
			player.mediaPlayer().media().start(url);
		});
	}
	
	@Override
	public void beforeRender() {
		synchronized (this) {
			if (buffer != null && first) {
				GlStateManager.pushMatrix();
				GlStateManager.bindTexture(texture);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				GlStateManager.popMatrix();
				first = false;
			}
			if (needsUpdate.getAndSet(false)) {
				GlStateManager.pushMatrix();
				GlStateManager.bindTexture(texture);
				
				//GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				
				GlStateManager.popMatrix();
			}
		}
	}
	
	@Override
	public void tick() {}
	
	@Override
	public void release() {
		player.release();
		super.release();
	}
	
	@Override
	public int getTextureID() {
		return texture;
	}
	
}
