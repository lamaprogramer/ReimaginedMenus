package net.iamaprogrammer.reimaginedmenus;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReimaginedMenus implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("reimaginedmenus");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Loaded Reimagined World Menus");
	}
}
