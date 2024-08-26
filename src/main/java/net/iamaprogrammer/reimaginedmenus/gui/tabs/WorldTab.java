package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class WorldTab extends BasicTab {
    private static final Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
    private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
    private static final Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
    private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
    private static final Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
    private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
    private static final Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo").formatted(Formatting.DARK_GRAY);
    private final TextFieldWidget seedField;
    private final ButtonWidget customizeButton;
    private final ButtonWidget worldIconsButton;
    private final CreateWorldScreen target;


    public WorldTab(TabUtils utils, CreateWorldScreen target, String key, Identifier icon, int posX, int width) {
        super(utils, posX, width, key, icon, WORLD_TAB_TITLE_TEXT);
        this.target = target;
        int buttonWidth = 210;
        int buttonHeight = 20;
        
        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(2);
        Positioner positioner = adder.copyPositioner().marginLeft((this.width - buttonWidth)/2).marginTop(this.posY);

        // World Type Button
        CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(WorldCreator.WorldType::getName).values(this.getWorldTypes()).narration(this::getWorldTypeNarrationMessage).build(0, 0, buttonWidth, buttonHeight, Text.translatable("selectWorld.mapType"), (cyclingButtonWidgetx, worldType) -> {
            worldCreator.setWorldType(worldType);
        }), 2, positioner);
        cyclingButtonWidget.setValue(worldCreator.getWorldType());

        worldCreator.addListener((creator) -> {
            WorldCreator.WorldType worldType = creator.getWorldType();
            cyclingButtonWidget.setValue(worldType);
            if (worldType.isAmplified()) {
                cyclingButtonWidget.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
            } else {
                cyclingButtonWidget.setTooltip(null);
            }

            cyclingButtonWidget.active = worldCreator.getWorldType().preset() != null;
        });


        // World Customize Button
        this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), (button) -> {
            this.openCustomizeScreen();
        }).size(buttonWidth, buttonHeight).build(), 2, positioner);

        worldCreator.addListener((creator) -> {
            this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null;
        });


        // World Seed Field
        adder.add((new TextWidget(ENTER_SEED_TEXT, renderer)), 2, positioner);
        this.seedField = adder.add(new TextFieldWidget(renderer, 0, 0, buttonWidth, 20, Text.translatable("selectWorld.enterSeed")) {
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
            }
        }, 2, positioner);
        this.seedField.setPlaceholder(SEED_INFO_TEXT);
        this.seedField.setText(worldCreator.getSeed());
        this.seedField.setChangedListener((seed) -> {
            worldCreator.setSeed(this.seedField.getText());
        });

        WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(buttonWidth).marginLeft(1);
        Objects.requireNonNull(worldCreator);

        // Generate Structures Button
        builder.add(MAP_FEATURES_TEXT, worldCreator::shouldGenerateStructures, worldCreator::setGenerateStructures).toggleable(() -> {
            return !worldCreator.isDebug();
        }).tooltip(MAP_FEATURES_INFO_TEXT);

        // Bonus Chest Button
        builder.add(BONUS_ITEMS_TEXT, worldCreator::isBonusChestEnabled, worldCreator::setBonusChestEnabled).toggleable(() -> {
            return !worldCreator.isHardcore() && !worldCreator.isDebug();
        });

        WorldScreenOptionGrid worldScreenOptionGrid = builder.build((widget) -> {
            adder.add(widget, 2, positioner);
        });
        worldCreator.addListener((creator) -> {
            worldScreenOptionGrid.refresh();
        });

        // World Icons Button
        this.worldIconsButton = adder.add(ButtonWidget.builder(Text.translatable("world.create.icon.title"), (button) -> {
            Path p = Path.of(new File(this.client.runDirectory, "worldicons/").toURI());
            this.client.setScreen(new WorldIconScreen(this.client, this.target, p, Text.translatable("world.create.icon.title")));
        }).size(buttonWidth, buttonHeight).build(), 2, positioner);

    }

    private void openCustomizeScreen() {
        LevelScreenProvider levelScreenProvider = worldCreator.getLevelScreenProvider();
        if (levelScreenProvider != null) {
            this.client.setScreen(levelScreenProvider.createEditScreen(this.target, this.worldCreator.getGeneratorOptionsHolder()));
        }
    }

    private CyclingButtonWidget.Values<WorldCreator.WorldType> getWorldTypes() {
        return new CyclingButtonWidget.Values<WorldCreator.WorldType>(){

            @Override
            public List<WorldCreator.WorldType> getCurrent() {
                return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean() ? worldCreator.getExtendedWorldTypes() : worldCreator.getNormalWorldTypes();
            }

            @Override
            public List<WorldCreator.WorldType> getDefaults() {
                return worldCreator.getNormalWorldTypes();
            }
        };
    }

    private MutableText getWorldTypeNarrationMessage(CyclingButtonWidget<WorldCreator.WorldType> worldTypeButton) {
        if (worldTypeButton.getValue().isAmplified()) {
            return ScreenTexts.joinSentences(worldTypeButton.getGenericNarrationMessage(), AMPLIFIED_GENERATOR_INFO_TEXT);
        }
        return worldTypeButton.getGenericNarrationMessage();
    }
}

