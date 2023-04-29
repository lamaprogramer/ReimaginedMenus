package net.iamaprogrammer.reimaginedmenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ReimaginedMenus implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("reimaginedmenus");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Loaded Reimagined World Menus");
	}
}
