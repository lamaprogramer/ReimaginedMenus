package net.iamaprogrammer.reimaginedmenus.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.AdvancedTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.GeneralTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.WorldTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.IOException;

public class MenuSettings {
    public static JsonObject getIcons() {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager()
                    .getResourceOrThrow(Identifier.of("reimaginedmenus", "textures/misc/tabicons.json"));

            Gson gson = new Gson();
            return gson.fromJson(resource.getReader(), JsonObject.class);
        } catch (IOException ignored) {}
        return null;
    }

    public static Identifier getIconFromJson(String key, JsonObject object, Identifier def) {
        if (object != null) {
            try {
                return new Identifier(object.get(key).getAsString());
            } catch (InvalidIdentifierException ignored) {}
        }
        return def;
    }
}
