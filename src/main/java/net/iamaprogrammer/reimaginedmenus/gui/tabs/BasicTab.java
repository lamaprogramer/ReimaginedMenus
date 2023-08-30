package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BasicTab extends GridScreenTab {
    protected final MinecraftClient client;
    protected final WorldCreator worldCreator;
    protected final TextRenderer renderer;
    public final String translationKey;
    public final Identifier icon;

    protected final int posX;
    protected final int posY = 0;

    public BasicTab(TabUtils utils, int posX, String translationKey, Identifier icon, Text title) {
        super(title);
        this.client = utils.client;
        this.worldCreator = utils.worldCreator;
        this.renderer = utils.renderer;
        this.translationKey = translationKey;
        this.icon = icon;
        this.posX = posX;
    }
}