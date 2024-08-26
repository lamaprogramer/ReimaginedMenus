package net.iamaprogrammer.reimaginedmenus.gui.widgets;

import com.google.common.collect.ImmutableList;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.BasicTab;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OptionsTabWidget extends AbstractParentElement implements Drawable, Element, Selectable {
    private static final Text USAGE_NARRATION_TEXT = Text.translatable("narration.tab_navigation.usage");
    private final GridWidget grid;
    private final TabManager tabManager;
    private final ImmutableList<BasicTab> tabs;
    private final ImmutableList<TabButtonWidget> tabButtons;


    OptionsTabWidget(TabManager tabManager, Iterable<BasicTab> tabs, int posX, int posY) {
        this.tabManager = tabManager;
        this.tabs = ImmutableList.copyOf(tabs);
        this.grid = new GridWidget(0, 0);
        this.grid.setPosition(posX, posY);
        this.grid.getMainPositioner().alignHorizontalCenter();
        ImmutableList.Builder<TabButtonWidget> builder = ImmutableList.builder();
        int i = 0;
        Iterator var6 = tabs.iterator();

        while(var6.hasNext()) {
            Tab tab = (Tab)var6.next();
            builder.add((TabButtonWidget)this.grid.add(new TabButtonWidget(tabManager, tab, 0, 24), 0, i++));
        }
        this.tabButtons = builder.build();
    }
    public ImmutableList<BasicTab> getTabs() {
        return this.tabs;
    }

    public static net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget.Builder builder(TabManager tabManager, int posX, int posY) {
        return new net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget.Builder(tabManager, posX, posY);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.getFocused() != null) {
            this.getFocused().setFocused(focused);
        }
    }

    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        if (focused instanceof TabButtonWidget tabButtonWidget) {
            this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
        }
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (!this.isFocused()) {
            TabButtonWidget tabButtonWidget = this.getCurrentTabButton();
            if (tabButtonWidget != null) {
                return GuiNavigationPath.of(this, GuiNavigationPath.of(tabButtonWidget));
            }
        }

        return navigation instanceof GuiNavigation.Tab ? null : super.getNavigationPath(navigation);
    }

    public List<? extends Element> children() {
        return this.tabButtons;
    }

    public Selectable.SelectionType getType() {
        return (Selectable.SelectionType)this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(SelectionType.NONE);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        Optional<TabButtonWidget> optional = this.tabButtons.stream().filter(ClickableWidget::isHovered).findFirst().or(() -> {
            return Optional.ofNullable(this.getCurrentTabButton());
        });
        optional.ifPresent((button) -> {
            this.appendNarrations(builder.nextMessage(), button);
            button.appendNarrations(builder);
        });
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, USAGE_NARRATION_TEXT);
        }

    }

    protected void appendNarrations(NarrationMessageBuilder builder, TabButtonWidget button) {
        if (this.tabs.size() > 1) {
            int i = this.tabButtons.indexOf(button);
            if (i != -1) {
                builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.tab", new Object[]{i + 1, this.tabs.size()}));
            }
        }

    }

    public ScreenRect getNavigationFocus() {
        return this.grid.getNavigationFocus();
    }

    public void init() {}

    public void selectTab(int index, boolean clickSound) {
        if (this.isFocused()) {
            this.setFocused((Element)this.tabButtons.get(index));
        } else {
            this.tabManager.setCurrentTab((Tab)this.tabs.get(index), clickSound);
        }
    }

    private int getCurrentTabIndex() {
        Tab tab = this.tabManager.getCurrentTab();
        int i = this.tabs.indexOf(tab);
        return i;
    }

    private @Nullable TabButtonWidget getCurrentTabButton() {
        int i = this.getCurrentTabIndex();
        return i != -1 ? (TabButtonWidget)this.tabButtons.get(i) : null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {}


    public static class Builder {
        private final TabManager tabManager;
        private final List<BasicTab> tabs = new ArrayList<>();
        private final int posX;
        private final int posY;

        Builder(TabManager tabManager, int x, int y) {
            this.tabManager = tabManager;
            this.posX = x;
            this.posY = y;
        }

        public net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget.Builder tabs(BasicTab... tabs) {
            Collections.addAll(this.tabs, tabs);
            return this;
        }

        public net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget.Builder tabs(ArrayList<BasicTab> tabs) {
            this.tabs.addAll(tabs);
            //Collections.addAll(this.tabs, tabs);
            return this;
        }

        public net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget build() {
            return new net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget(this.tabManager, this.tabs, this.posX, this.posY);
        }
    }
}
