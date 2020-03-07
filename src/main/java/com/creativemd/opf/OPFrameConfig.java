package com.creativemd.opf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class OPFrameConfig {
	
	@CreativeConfig
	public double sizeLimitation = 1000.0;
	
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public int maxRenderDistance = 10000;
	
	@CreativeConfig
	public boolean onlyOps = false;
	
	@CreativeConfig
	public boolean disableAdventure = true;
	
	@CreativeConfig
	public boolean onlyCreative = false;
	
	@CreativeConfig
	public boolean whitelistEnabled = false;
	
	@CreativeConfig
	public List<String> whitelist = Arrays.asList("imgur.com", "gyazo.com", "prntscr.com", "tinypic.com", "puu.sh", "pinimg.com", "photobucket.com", "staticflickr.com", "flic.kr", "tenor.co", "gfycat.com", "giphy.com", "gph.is", "gifbin.com", "i.redd.it", "media.tumblr.com", "twimg.com", "discordapp.com", "images.discordapp.net", "githubusercontent.com", "googleusercontent.com", "googleapis.com", "wikimedia.org", "ytimg.com");
	
	public boolean canUse(EntityPlayer player, String url) {
		return canUse(player, url, false);
	}
	
	public boolean canUse(EntityPlayer player, String url, boolean ignoreToggle) {
		World world = player.world;
		if (!world.isRemote && (world.getMinecraftServer().isSinglePlayer() || player.canUseCommand(world.getMinecraftServer().getOpPermissionLevel(), ""))) {
			return true;
		}
		if (whitelistEnabled || ignoreToggle) {
			try {
				return isDomainWhitelisted(new URI(url.toLowerCase(Locale.ROOT)).getHost());
			} catch (URISyntaxException e) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isDomainWhitelisted(String domain) {
		if (domain != null) {
			for (String url : whitelist) {
				String formattedUrl = url.trim().toLowerCase(Locale.ROOT);
				if (domain.endsWith("." + formattedUrl) || domain.equals(formattedUrl)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canInteract(EntityPlayer player, World world) {
		if (disableAdventure && ((EntityPlayerMP) player).interactionManager.getGameType() == GameType.ADVENTURE)
			return false;
		if (onlyCreative && !player.isCreative())
			return false;
		boolean isOperator = world.getMinecraftServer().isSinglePlayer() || player.canUseCommand(world.getMinecraftServer().getOpPermissionLevel(), "");
		if (onlyOps) {
			return isOperator;
		} else {
			return isOperator || (!disableAdventure || player.capabilities.allowEdit);
		}
	}
}
