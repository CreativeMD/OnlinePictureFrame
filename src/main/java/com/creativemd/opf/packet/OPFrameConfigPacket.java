package com.creativemd.opf.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.opf.OPFrameConfig;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class OPFrameConfigPacket extends CreativeCorePacket {
	private OPFrameConfig.Limitations limitations = OPFrameConfig.limitations;
	
	public OPFrameConfigPacket() {
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeFloat((float) limitations.sizeLimitation);
		buf.writeBoolean(limitations.whitelistEnabled);
		if (limitations.whitelistEnabled) {
			buf.writeShort(limitations.whitelist.length);
			for (String whitelisted : limitations.whitelist) {
				ByteBufUtils.writeUTF8String(buf, whitelisted);
			}
		}
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		limitations.sizeLimitation = buf.readFloat();
		limitations.whitelistEnabled = buf.readBoolean();
		if (limitations.whitelistEnabled) {
			int count = buf.readUnsignedShort();
			String[] whitelist = new String[count];
			for (int i = 0; i < count; i++) {
				whitelist[i] = ByteBufUtils.readUTF8String(buf);
			}
			limitations.whitelist = whitelist;
		}
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		OPFrameConfig.setGlobalLimitations(limitations);
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
	}
}
