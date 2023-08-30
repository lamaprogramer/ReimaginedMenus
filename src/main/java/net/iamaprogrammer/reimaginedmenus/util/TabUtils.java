package net.iamaprogrammer.reimaginedmenus.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.WorldCreator;

public class TabUtils {
    public final MinecraftClient client;
    public final WorldCreator worldCreator;
    public final TextRenderer renderer;

    public TabUtils(MinecraftClient client, WorldCreator worldCreator, TextRenderer renderer) {
        this.client = client;
        this.worldCreator = worldCreator;
        this.renderer = renderer;
    }
}