package net.iamaprogrammer.reimaginedmenus.gui.tabs;


import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;


public class GeneralTab extends BasicTab {
    private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
    private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    private static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
    private static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
    private final TextFieldWidget worldNameField;

    public GeneralTab(TabUtils utils, CreateWorldScreen target, String key, Identifier icon, int posX, int width) {
        super(utils, posX, width, key, icon, GAME_TAB_TITLE_TEXT);
        int buttonWidth = 210;
        int buttonHeight = 20;

        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner().marginLeft((this.width - buttonWidth)/2).marginTop(this.posY);
        GridWidget grid2 = new GridWidget().setRowSpacing(4);
        GridWidget.Adder adder2 = grid2.createAdder(1);

        // Text
        adder2.add(new TextWidget(ENTER_NAME_TEXT, client.textRenderer), positioner);

        // World Name Field
        this.worldNameField = adder2.add(new TextFieldWidget(renderer, this.posX, this.posY, buttonWidth, 20, Text.translatable("selectWorld.enterName")), positioner);
        this.worldNameField.setText(worldCreator.getWorldName());
        this.worldNameField.setChangedListener(worldCreator::setWorldName);
        worldCreator.addListener(creator -> this.worldNameField.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC)))));
        adder.add(adder2.getGridWidget(), adder.copyPositioner().alignHorizontalCenter());

        // Gamemode Button
        CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(CyclingButtonWidget.<WorldCreator.Mode>builder(value -> value.name).values(WorldCreator.Mode.values()).build(0, 0, buttonWidth, buttonHeight, GAME_MODE_TEXT, (button, value) -> worldCreator.setGameMode(value)), positioner);
        cyclingButtonWidget.setValue(WorldCreator.Mode.SURVIVAL);
        worldCreator.addListener(creator -> {
            cyclingButtonWidget.setValue(creator.getGameMode());
            cyclingButtonWidget.active = !creator.isDebug();
            cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
        });

        // Difficulty Button
        CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values(Difficulty.values()).build(0, 0, buttonWidth, buttonHeight, Text.translatable("options.difficulty"), (button, value) -> worldCreator.setDifficulty((Difficulty) value)), positioner);
        cyclingButtonWidget2.setValue(Difficulty.NORMAL);
        worldCreator.addListener(creator -> {
            cyclingButtonWidget2.setValue(worldCreator.getDifficulty());
            cyclingButtonWidget2.active = !worldCreator.isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.of(worldCreator.getDifficulty().getInfo()));
        });

        // Enable Cheats Button
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(CyclingButtonWidget.onOffBuilder().tooltip(value -> Tooltip.of(ALLOW_COMMANDS_INFO_TEXT)).build(0, 0, buttonWidth, buttonHeight, ALLOW_COMMANDS_TEXT, (button, value) -> worldCreator.setCheatsEnabled((boolean) value)), positioner);
        cyclingButtonWidget3.setValue(false);
        worldCreator.addListener(creator -> {
            cyclingButtonWidget3.setValue(worldCreator.areCheatsEnabled());
            cyclingButtonWidget3.active = !worldCreator.isDebug() && !worldCreator.isHardcore();
        });

    }
}
