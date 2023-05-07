package net.iamaprogrammer.reimaginedmenus.gui.tabs;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Difficulty;

import java.io.File;


public class GeneralTab extends GridScreenTab {
    private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
    private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    private static final Text ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName");
    private static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable("selectWorld.allowCommands.info");
    private final TextFieldWidget worldNameField;

    private final int posX;
    private final int posY;

    public GeneralTab(MinecraftClient client, WorldCreator worldCreator, CreateWorldScreen target, TextRenderer renderer, int posX, int posY) {
        super(GAME_TAB_TITLE_TEXT);
        this.posX = posX;
        this.posY = posY;

        File file3 = new File(client.runDirectory, "worldicons/");

        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner().marginLeft(this.posX).marginTop(this.posY);
        GridWidget grid2 = new GridWidget().setRowSpacing(4);
        GridWidget.Adder adder2 = grid2.createAdder(1);

        adder2.add(new TextWidget(ENTER_NAME_TEXT, client.textRenderer), adder2.copyPositioner().marginLeft(this.posX + 1).marginTop(this.posY));
        this.worldNameField = adder2.add(new TextFieldWidget(renderer, this.posX, this.posY, 208, 20, Text.translatable("selectWorld.enterName")), adder2.copyPositioner().marginLeft(this.posX + 1).marginTop(this.posY));
        this.worldNameField.setText(worldCreator.getWorldName());
        this.worldNameField.setChangedListener(worldCreator::setWorldName);
        worldCreator.addListener(creator -> this.worldNameField.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC)))));
        //CreateWorldScreen.super.setInitialFocus(this.worldNameField);
        adder.add(adder2.getGridWidget(), adder.copyPositioner().alignHorizontalCenter());

        CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = (CyclingButtonWidget)adder.add(CyclingButtonWidget.builder(value -> ((WorldCreator.Mode)value).name).values((WorldCreator.Mode[])new WorldCreator.Mode[]{WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE}).build(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> worldCreator.setGameMode((WorldCreator.Mode)((Object)value))), positioner);
        cyclingButtonWidget.setValue(WorldCreator.Mode.SURVIVAL);


        worldCreator.addListener(creator -> {
            cyclingButtonWidget.setValue(creator.getGameMode());
            cyclingButtonWidget.active = !creator.isDebug();
            cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
        });
        CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Difficulty[])Difficulty.values()).build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> worldCreator.setDifficulty((Difficulty)value)), positioner);
        cyclingButtonWidget2.setValue(Difficulty.NORMAL);
        worldCreator.addListener(creator -> {
            cyclingButtonWidget2.setValue(worldCreator.getDifficulty());
            cyclingButtonWidget2.active = !worldCreator.isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.of(worldCreator.getDifficulty().getInfo()));
        });
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(CyclingButtonWidget.onOffBuilder().tooltip(value -> Tooltip.of(ALLOW_COMMANDS_INFO_TEXT)).build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> worldCreator.setCheatsEnabled((boolean)value)), positioner);
        cyclingButtonWidget3.setValue(false);
        worldCreator.addListener(creator -> {
            cyclingButtonWidget3.setValue(worldCreator.areCheatsEnabled());
            cyclingButtonWidget3.active = !worldCreator.isDebug() && !worldCreator.isHardcore();
        });

    }
    @Override
    public void tick() {
        this.worldNameField.tick();
    }
}
