package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class AdvancedTab extends BasicTab {
    private static final Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
    private static final Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
    private static final Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");
    private static final Text EXPERIMENTS_TEXT = Text.translatable("selectWorld.experiments");

    private final CreateWorldScreen target;
    private final int buttonWidth;

    public AdvancedTab(TabUtils utils, CreateWorldScreen target, String key, Identifier icon, int posX) {
        super(utils, posX, key, icon, MORE_TAB_TITLE_TEXT);
        this.target = target;
        this.buttonWidth = (int)((this.posX/1.5) - 20);

        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner().marginLeft(this.posX).marginTop(this.posY);
        adder.add(ButtonWidget.builder(GAME_RULES_TEXT, button -> this.openGameRulesScreen()).width(this.buttonWidth).build(), positioner);
        adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> target.openExperimentsScreen(worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(this.buttonWidth).build(), positioner);
        adder.add(ButtonWidget.builder(DATA_PACKS_TEXT, button -> target.openPackScreen(worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(this.buttonWidth).build(), positioner);
    }

    private void openGameRulesScreen() {
        this.client.setScreen(new EditGameRulesScreen(this.worldCreator.getGameRules().copy(), gameRules -> {
            this.client.setScreen(this.target);
            gameRules.ifPresent(this.worldCreator::setGameRules);
        }));
    }
}

