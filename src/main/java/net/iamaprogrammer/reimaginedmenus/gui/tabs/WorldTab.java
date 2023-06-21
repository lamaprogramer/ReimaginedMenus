package net.iamaprogrammer.reimaginedmenus.gui.tabs;

import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class WorldTab extends GridScreenTab {
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

    private final MinecraftClient client;
    private final WorldCreator worldCreator;
    private final CreateWorldScreen target;


    private final int buttonWidth;
    private final int posX;
    private final int posY;


    public WorldTab(MinecraftClient client, CreateWorldScreen target, WorldCreator worldCreator, TextRenderer renderer, int posX, int posY) {
        super(WORLD_TAB_TITLE_TEXT);
        this.client = client;
        this.worldCreator = worldCreator;
        this.target = target;
        this.posX = posX;
        this.posY = posY;
        this.buttonWidth = (int)((this.posX/1.5) - 20);
        
        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(2);
        Positioner positioner = adder.copyPositioner().marginLeft(this.posX).marginTop(this.posY);
        CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(WorldCreator.WorldType::getName).values(this.getWorldTypes()).narration(this::getWorldTypeNarrationMessage).build(0, 0, this.buttonWidth, 20, Text.translatable("selectWorld.mapType"), (cyclingButtonWidgetx, worldType) -> {
            worldCreator.setWorldType(worldType);
        }), 2, positioner);
        cyclingButtonWidget.setValue(worldCreator.getWorldType());
        worldCreator.addListener((creator) -> {
            WorldCreator.WorldType worldType = creator.getWorldType();
            cyclingButtonWidget.setValue(worldType);
            if (worldType.isAmplified()) {
                cyclingButtonWidget.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
            } else {
                cyclingButtonWidget.setTooltip((Tooltip)null);
            }

            cyclingButtonWidget.active = worldCreator.getWorldType().preset() != null;
        });
        this.customizeButton = (ButtonWidget)adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), (button) -> {
            this.openCustomizeScreen();
        }).size(this.buttonWidth, 20).build(), 2, positioner);
        worldCreator.addListener((creator) -> {
            this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null;
        });

        adder.add((new TextWidget(ENTER_SEED_TEXT, renderer)), 2, positioner);
        this.seedField = (TextFieldWidget)adder.add(new TextFieldWidget(renderer, 0, 0, this.buttonWidth, 20, Text.translatable("selectWorld.enterSeed")) {
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
            }
        }, 2, positioner);
        this.seedField.setPlaceholder(SEED_INFO_TEXT);
        this.seedField.setText(worldCreator.getSeed());
        this.seedField.setChangedListener((seed) -> {
            worldCreator.setSeed(this.seedField.getText());
        });
        WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(this.buttonWidth).marginLeft(1);
        Text var10001 = MAP_FEATURES_TEXT;
        WorldCreator var10002 = worldCreator;
        Objects.requireNonNull(var10002);
        BooleanSupplier var7 = var10002::shouldGenerateStructures;
        WorldCreator var10003 = worldCreator;
        Objects.requireNonNull(var10003);
        builder.add(var10001, var7, var10003::setGenerateStructures).toggleable(() -> {
            return !worldCreator.isDebug();
        }).tooltip(MAP_FEATURES_INFO_TEXT);
        var10001 = BONUS_ITEMS_TEXT;
        var10002 = worldCreator;
        Objects.requireNonNull(var10002);
        var7 = var10002::isBonusChestEnabled;
        var10003 = worldCreator;
        Objects.requireNonNull(var10003);
        builder.add(var10001, var7, var10003::setBonusChestEnabled).toggleable(() -> {
            return !worldCreator.isHardcore() && !worldCreator.isDebug();
        });
        WorldScreenOptionGrid worldScreenOptionGrid = builder.build((widget) -> {
            adder.add(widget, 2, positioner);
       });
        worldCreator.addListener((creator) -> {
            worldScreenOptionGrid.refresh();
        });
        this.worldIconsButton = (ButtonWidget)adder.add(ButtonWidget.builder(Text.translatable("world.create.icon.title"), (button) -> {
            Path p = Path.of(new File(this.client.runDirectory, "worldicons/").toURI());
            this.client.setScreen(new WorldIconScreen(this.client, this.target, p, Text.translatable("world.create.icon.title")));
        }).size(this.buttonWidth, 20).build(), 2, positioner);

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

