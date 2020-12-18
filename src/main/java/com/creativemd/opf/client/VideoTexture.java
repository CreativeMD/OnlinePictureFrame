package com.creativemd.opf.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.Loader;
import team.creative.onlinepictureframe.lib.network.InterProcessError;
import team.creative.onlinepictureframe.lib.network.InterProcessNetwork;
import team.creative.onlinepictureframe.lib.network.InterProcessPacket;
import team.creative.onlinepictureframe.lib.network.packet.BufferPacket;
import team.creative.onlinepictureframe.lib.network.packet.DimensionPacket;
import team.creative.onlinepictureframe.lib.network.packet.ErrorPacket;
import team.creative.onlinepictureframe.lib.network.packet.UpdatePacket;
import team.creative.onlinepictureframe.lib.network.packet.VolumePacket;

public class VideoTexture extends PictureTexture {
	
	static final File vlcj = new File(new File(new File(Loader.instance().getConfigDir().getParent(), "mods"), "1.12.2"), "OnlinePictureFrameVLCJ_v1.0.1.jar");
	
	public ByteBuffer buffer;
	public int texture;
	private Process process;
	private InterProcessNetwork network;
	private AtomicBoolean needsUpdate = new AtomicBoolean(false);
	private boolean first = true;
	
	public VideoTexture(String url) {
		super(100, 100);
		texture = GlStateManager.generateTexture();
		
		try {
			process = Runtime.getRuntime().exec("java -jar \"" + vlcj + "\" \"" + url + "\"");
			network = new InterProcessNetwork(process.getInputStream(), process.getOutputStream()) {
				
				@Override
				public void process(InterProcessPacket packet) {
					if (packet instanceof ErrorPacket) {
						if (((ErrorPacket) packet).error == InterProcessError.FAILED_LOAD_VIDEO)
							network.release();
					} else if (packet instanceof UpdatePacket)
						needsUpdate.set(true);
					else if (packet instanceof DimensionPacket) {
						synchronized (VideoTexture.this) {
							width = ((DimensionPacket) packet).width;
							height = ((DimensionPacket) packet).height;
							first = true;
						}
					} else if (packet instanceof BufferPacket) {
						synchronized (VideoTexture.this) {
							buffer = ((BufferPacket) packet).buffer;
						}
						/*System.out.println("newbuffer:" + ((BufferPacket) packet).address + "," + ((BufferPacket) packet).capacity);
						//buffer = new Pointer(((BufferPacket) packet).address).getByteBuffer(0, ((BufferPacket) packet).capacity);
						buffer = UnsafeByteBufferUtils.create(((BufferPacket) packet).address, ((BufferPacket) packet).capacity);
						System.out.println("Updated buffer " + buffer);*/
					}
				}
				
				@Override
				public boolean isMod() {
					return true;
				}
				
				@Override
				public void receive(InterProcessPacket packet, InputStream in) throws IOException {
					if (packet instanceof BufferPacket)
						synchronized (VideoTexture.this) {
							((BufferPacket) packet).buffer = buffer;
							packet.receive(in);
						}
				}
			};
			network.send(new VolumePacket(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void beforeRender() {
		synchronized (this) {
			if (buffer != null && first) {
				System.out.println("Uploading for the first time " + buffer);
				GlStateManager.pushMatrix();
				GlStateManager.bindTexture(texture);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				GlStateManager.popMatrix();
				System.out.println("Done");
				first = false;
			}
			if (needsUpdate.getAndSet(false)) {
				GlStateManager.pushMatrix();
				GlStateManager.bindTexture(texture);
				
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				//GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				
				GlStateManager.popMatrix();
			}
		}
	}
	
	@Override
	public void tick() {}
	
	@Override
	public void release() {
		if (process.isAlive())
			process.destroyForcibly();
		super.release();
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (process.isAlive())
			process.destroyForcibly();
		super.finalize();
	}
	
	@Override
	public int getTextureID() {
		return texture;
	}
	
}
