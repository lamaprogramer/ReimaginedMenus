package net.iamaprogrammer.reimaginedmenus.mixin;

import net.iamaprogrammer.reimaginedmenus.gui.tabs.WorldTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.GeneralTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.AdvancedTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

	@Shadow @Nullable private TabNavigationWidget tabNavigation;

	@Shadow @Final private TabManager tabManager;
	@Shadow abstract void openExperimentsScreen(DataConfiguration dataConfiguration);
	@Shadow @Final private WorldCreator worldCreator;

	@Shadow protected abstract void createLevel();

	@Shadow public abstract void onCloseScreen();

	@Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

	private final Identifier GENERAL_SETTINGS_ICON = new Identifier("minecraft", "textures/block/crafting_table_top.png");
	private final Identifier ADVANCED_SETTINGS_ICON = new Identifier("minecraft", "textures/block/furnace_front.png");
	private final Identifier CHEATS_SETTINGS_ICON = new Identifier("minecraft", "textures/block/enchanting_table_top.png");
	private OptionsListWidget<CreateWorldScreen> tabMenu;
	private final CreateWorldScreen target =  ((CreateWorldScreen)(Object)this);
	private OptionsTabWidget navigator;
	private int currentTab = 0;
	@Nullable
	private GridWidget grid;

	private int tabMenuWidth;
	private int tabMenuHeight;

	private int createWorldWidth;
	private int createWorldHeight;



	private int navigatorWidth;
	private int navigatorHeight;

	private int cancelWidth;
	private int cancelHeight;
	
	

	protected CreateWorldScreenMixin(Text title) {
		super(title);
	}

	/**
	 * @author Iamaprogrammer
	 * @reason To completely re-style the menu.
	 */

	@Overwrite
	public void init() {
		this.tabMenuWidth = this.width/3;
		this.tabMenuHeight = this.height;

		this.navigatorWidth = (int)(this.width/1.5);
		this.navigatorHeight = this.height;


		this.createWorldWidth = (this.navigatorWidth/2) - 20;
		this.createWorldHeight = 20;

		this.cancelWidth = (this.navigatorWidth/2) - 20;
		this.cancelHeight = 20;


		this.tabMenu = new OptionsListWidget<>(this.client, this.target, this.worldCreator, this.tabMenuWidth, this.height, 20, Text.literal("Settings"));
		this.addDrawableChild(this.tabMenu);

		GeneralTab generalTab = new GeneralTab(this.client, this.worldCreator, this.textRenderer, this.navigatorWidth, 0);
		WorldTab worldTab = new WorldTab(this.client, target, this.worldCreator, this.textRenderer, this.navigatorWidth, 0);
		AdvancedTab advancedTab = new AdvancedTab(this.client, target, this.worldCreator, this.textRenderer, this.navigatorWidth, 0);

		this.navigator = OptionsTabWidget.builder(this.tabManager, this.navigatorWidth, this.tabMenuWidth, 0).tabs(generalTab, worldTab, advancedTab).build();
		this.addDrawableChild(this.navigator);




		this.tabMenu.add(this.client, this.tabMenu, Text.literal("General"), this.GENERAL_SETTINGS_ICON, () -> {
			this.navigator.selectTab(0, true);
			this.currentTab = 0;
		});

		this.tabMenu.add(this.client, this.tabMenu, Text.literal("World"), this.ADVANCED_SETTINGS_ICON, () -> {
			this.navigator.selectTab(1, true);
			this.currentTab = 1;
		});

		this.tabMenu.add(this.client, this.tabMenu, Text.literal("Advanced"), this.CHEATS_SETTINGS_ICON, () -> {
			this.navigator.selectTab(2, true);
			this.currentTab = 2;
		});

		this.tabMenu.selectTab(this.currentTab);
		this.grid = new GridWidget().setColumnSpacing(8);

		GridWidget.Adder adder = this.grid.createAdder(2);
		Positioner positioner = adder.copyPositioner().marginLeft(this.navigatorWidth);
		adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).size(this.createWorldWidth, this.createWorldHeight).build(), positioner);
		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCloseScreen()).size(this.cancelWidth, this.cancelHeight).build());
		this.grid.forEachChild(child -> {
			child.setNavigationOrder(1);
			this.addDrawableChild(child);
		});

		//this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).size(this.createWorldWidth, this.createWorldHeight).position(((tabMenuWidth)-createWorldWidth)/2, (tabMenuHeight/2)-createWorldHeight).build());
		//this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCloseScreen()).size(this.cancelWidth, this.cancelHeight).position((this.tabMenuWidth) + (((this.navigatorWidth)-cancelWidth)/2), (this.height)-this.cancelHeight-8).build());

		this.navigator.selectTab(currentTab, false);
		this.worldCreator.update();
		initTabNavigation();

		//OptionsListWidget.OptionsPackEntry experimentsEntry = this.tabMenu.get(experimentsTab);
	}

	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.clearChildren();
		this.init();
		this.initTabNavigation();
	}

	@Override
	public void initTabNavigation() {
		if (this.navigator == null || this.grid == null) {
			return;
		}
		this.navigator.setWidth(this.navigatorWidth);
		this.navigator.init();
		this.grid.refreshPositions();
		SimplePositioningWidget.setPos(this.grid, 0, this.height - 36, this.navigatorWidth, 36);
		int i = this.navigator.getNavigationFocus().getBottom();
		ScreenRect screenRect = new ScreenRect(0, i, this.navigatorWidth, this.grid.getY() - i);
		this.tabManager.setTabArea(screenRect);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.navigator.trySwitchTabsWithKey(keyCode)) {
			return true;
		}
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			this.createLevel();
			return true;
		}
		return false;
	}
}


