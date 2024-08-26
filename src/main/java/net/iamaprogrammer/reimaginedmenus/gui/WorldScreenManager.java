package net.iamaprogrammer.reimaginedmenus.gui;

import net.iamaprogrammer.reimaginedmenus.gui.tabs.BasicTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.iamaprogrammer.reimaginedmenus.util.ProportionManager;
import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class WorldScreenManager {
    private final ArrayList<BasicTab> TABS = new ArrayList<>();

    private OptionsTabWidget navigator;
    private OptionsListWidget tabList;
    private int currentTab = 0;

    private final int sideNavWidth;
    private final int tabMenuWidth;
    private final int height;

    public WorldScreenManager(int width, int height, ProportionManager proportionManager) {
        this.sideNavWidth = (int) proportionManager.getColumnProportion(width, 0);
        this.tabMenuWidth = (int) proportionManager.getColumnProportion(width, 1);
        this.height = height;
    }

    public void init(TabUtils utils, TabManager tabManager) {
        this.tabList = new OptionsListWidget(utils.client, utils.worldCreator, sideNavWidth, this.height, 20, Text.translatable("world.create.settings"));
        this.navigator = OptionsTabWidget.builder(tabManager, this.tabMenuWidth, 0)
                .tabs(TABS)
                .build();

        for (int i = 0; i < TABS.size(); i++) {
            BasicTab tab = TABS.get(i);
            this.tabList.add(utils.client, this.tabList, Text.translatable(tab.translationKey), tab.icon, i, (id) -> {
                this.navigator.selectTab(id, true);
                this.currentTab = id;
            });
        }

        this.tabList.selectTab(this.currentTab);
        this.navigator.selectTab(this.currentTab, false);
    }

    public int getCurrentTab() {
        return this.currentTab;
    }

    public void setCurrentTab(int val) {
        this.currentTab = val;
    }

    public void selectCurrentTab(boolean sound) {
        this.navigator.selectTab(this.currentTab, sound);
        this.tabList.selectTab(this.currentTab);
        this.tabList.setFocused(this.tabList.children().get(this.currentTab));
    }

    public void selectCurrentTab() {
        this.selectCurrentTab(true);
    }

    public int getNavigatorWidth() {
        return this.sideNavWidth;
    }

    public int getTabMenuWidth() {
        return this.tabMenuWidth;
    }

    public OptionsListWidget getOptionsListWidget() {
        return this.tabList;
    }

    public OptionsTabWidget getNavigator() {
        return this.navigator;
    }

    public void addTab(BasicTab tab) {
        TABS.add(tab);
    }
}
