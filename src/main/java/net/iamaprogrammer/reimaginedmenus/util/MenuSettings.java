package net.iamaprogrammer.reimaginedmenus.util;

import net.iamaprogrammer.reimaginedmenus.gui.tabs.AdvancedTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.GeneralTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.WorldTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.util.Identifier;

public class MenuSettings {
    private static final Identifier GENERAL_SETTINGS_ICON = new Identifier("minecraft", "textures/block/crafting_table_top.png");
    private static final Identifier WORLD_SETTINGS_ICON = new Identifier("minecraft", "textures/block/furnace_front.png");
    private static final Identifier ADVANCED_SETTINGS_ICON = new Identifier("minecraft", "textures/block/enchanting_table_top.png");
    public static int numberOfTabs = 3;

    public static OptionsTabWidget init(TabUtils utils, TabManager tabManager, CreateWorldScreen target, int navigatorWidth, int tabMenuWidth) {

        GeneralTab generalTab = new GeneralTab(utils, target, "world.create.tab.general", GENERAL_SETTINGS_ICON, navigatorWidth);
        WorldTab worldTab = new WorldTab(utils, target,  "world.create.tab.world", WORLD_SETTINGS_ICON, navigatorWidth);
        AdvancedTab advancedTab = new AdvancedTab(utils, target, "world.create.tab.advanced", ADVANCED_SETTINGS_ICON, navigatorWidth);

        return OptionsTabWidget.builder(tabManager, tabMenuWidth, 0).tabs(generalTab, worldTab, advancedTab).build();
    }
}
