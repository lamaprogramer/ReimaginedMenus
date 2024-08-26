package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BasicTab extends GridScreenTab {
    protected final MinecraftClient client;
    protected final WorldCreator worldCreator;
    protected final TextRenderer renderer;
    protected final int width;
    protected final int posX;
    protected final int posY;

    public final String translationKey;
    public final Identifier icon;

    protected BasicTab(TabUtils utils, int posX, int width, String translationKey, Identifier icon, Text title) {
        super(title);
        this.client = utils.client;
        this.worldCreator = utils.worldCreator;
        this.renderer = utils.renderer;
        this.translationKey = translationKey;
        this.icon = icon;
        this.width = width;
        this.posX = posX;
        this.posY = 0;
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        this.grid.refreshPositions();
        SimplePositioningWidget.setPos(this.grid, tabArea, 0.0f, 0.16666667f);
    }
}
