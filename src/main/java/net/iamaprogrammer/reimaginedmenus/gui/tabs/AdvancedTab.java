package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.text.Text;


public class AdvancedTab extends GridScreenTab {
    private static final Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
    private static final Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
    private static final Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");
    private static final Text EXPERIMENTS_TEXT = Text.translatable("selectWorld.experiments");

    private final MinecraftClient client;
    private final WorldCreator worldCreator;
    private final CreateWorldScreen target;

    private final int buttonWidth;
    private final int posX;
    private final int posY;

    public AdvancedTab(MinecraftClient client, CreateWorldScreen target, WorldCreator worldCreator, int posX, int posY) {
        super(MORE_TAB_TITLE_TEXT);
        this.client = client;
        this.worldCreator = worldCreator;
        this.target = target;
        this.posX = posX;
        this.posY = posY;
        this.buttonWidth = (int)((this.posX/1.5) - 20);

        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner().marginLeft(this.posX).marginTop(this.posY);;
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

