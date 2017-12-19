package com.creativemd.opf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

@Config(modid = "opframe", category = "")
@Mod.EventBusSubscriber
public class OPFrameConfig {
	@Config.Name("limitations")
	@Config.LangKey("config.opframe.limitations")
	public static Limitations limitations = new Limitations();

	private static Limitations serverLimitations = limitations;

	public static Limitations getGlobalLimitations() {
		return serverLimitations;
	}

	public static void setGlobalLimitations(Limitations globalLimitations) {
		serverLimitations = globalLimitations;
	}

	public static class Limitations {
		@Config.Name("size-limitation")
		@Config.RangeDouble(min = 0.0F, max = 10000.0F)
		@Config.Comment("The maximum size (in blocks) for a frame")
		@Config.LangKey("config.opframe.sizeLimit")
		@Config.RequiresMcRestart
		public double sizeLimitation = 1000.0F;

		@Config.Name("onlyOperators")
		@Config.LangKey("config.opframe.onlyOpped")
		@Config.Comment("True if only operators (opped players) are allowed to edit a frame")
		public boolean onlyOps = false;

		@Config.Name("disableAdventure")
		@Config.LangKey("config.opframe.disableAdventure")
		@Config.Comment("If true, players in adventure mode will not be able to edit frames")
		public boolean disableAdventure = true;

		@Config.Name("whitelistEnabled")
		@Config.LangKey("config.opframe.enableWhitelist")
		@Config.Comment("If true, only URLs listed in the whitelist can be used for a frame")
		@Config.RequiresMcRestart
		public boolean whitelistEnabled = false;

		@Config.Name("whitelist")
		@Config.LangKey("config.opframe.whitelist")
		@Config.Comment("A list of URLs that can be used for frames")
		@Config.RequiresMcRestart
		public String[] whitelist = new String[] {
				"imgur.com",
				"gyazo.com",
				"prntscr.com",
				"tinypic.com",
				"puu.sh",
				"pinimg.com",
				"photobucket.com",
				"staticflickr.com",
				"flic.kr",
				"tenor.co",
				"gfycat.com",
				"giphy.com",
				"gph.is",
				"gifbin.com",
				"i.redd.it",
				"media.tumblr.com",
				"twimg.com",
				"discordapp.com",
				"images.discordapp.net",
				"githubusercontent.com",
				"googleusercontent.com",
				"googleapis.com",
				"wikimedia.org",
				"ytimg.com"
		};

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
				}
				catch (URISyntaxException e) {
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
			if(disableAdventure && ((EntityPlayerMP) player).interactionManager.getGameType() == GameType.ADVENTURE)
				return false;
			boolean isOperator = world.getMinecraftServer().isSinglePlayer() || player.canUseCommand(world.getMinecraftServer().getOpPermissionLevel(), "");
			if (onlyOps) {
				return isOperator;
			} else {
				return isOperator || (!disableAdventure || player.capabilities.allowEdit);
			}
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals("opframe")) {
			ConfigManager.sync("opframe", Config.Type.INSTANCE);
		}
	}
}
