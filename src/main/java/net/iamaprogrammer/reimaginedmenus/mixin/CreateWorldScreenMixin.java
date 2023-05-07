package net.iamaprogrammer.reimaginedmenus.mixin;

import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.AdvancedTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.GeneralTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.WorldTab;
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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

	@Shadow @Final private TabManager tabManager;
	@Shadow @Final private WorldCreator worldCreator;

	@Shadow protected abstract void createLevel();

	@Shadow public abstract void onCloseScreen();

	@Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

	private final Identifier GENERAL_SETTINGS_ICON = new Identifier("minecraft", "textures/block/crafting_table_top.png");
	private final Identifier ADVANCED_SETTINGS_ICON = new Identifier("minecraft", "textures/block/furnace_front.png");
	private final Identifier CHEATS_SETTINGS_ICON = new Identifier("minecraft", "textures/block/enchanting_table_top.png");
	private final CreateWorldScreen target =  ((CreateWorldScreen)(Object)this);
	private OptionsTabWidget navigator;
	private OptionsListWidget tabMenu;
	private int currentTab = 0;
	private Element prevBtn;
	@Nullable
	private GridWidget grid;

	private int tabMenuWidth;

	private int createWorldWidth;
	private int createWorldHeight;



	private int navigatorWidth;

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
		this.navigatorWidth = (int)(this.width/1.5);


		this.createWorldWidth = (this.navigatorWidth/2) - 20;
		this.createWorldHeight = 20;

		this.cancelWidth = (this.navigatorWidth/2) - 20;
		this.cancelHeight = 20;


		this.tabMenu = new OptionsListWidget(this.client, this.worldCreator, this.tabMenuWidth, this.height, 20, Text.translatable("world.create.settings"));
		this.addDrawableChild(this.tabMenu);

		GeneralTab generalTab = new GeneralTab(this.client, this.worldCreator, this.target, this.textRenderer, this.navigatorWidth, 0);
		WorldTab worldTab = new WorldTab(this.client, target, this.worldCreator, this.textRenderer, this.navigatorWidth, 0);
		AdvancedTab advancedTab = new AdvancedTab(this.client, target, this.worldCreator, this.textRenderer, this.navigatorWidth, 0);

		this.navigator = OptionsTabWidget.builder(this.tabManager, this.tabMenuWidth, 0).tabs(generalTab, worldTab, advancedTab).build();
		this.addDrawableChild(this.navigator);


		this.tabMenu.add(this.client, this.tabMenu, Text.translatable("world.create.tab.general"), this.GENERAL_SETTINGS_ICON, () -> {
			this.navigator.selectTab(0, true);
			this.currentTab = 0;
		});

		this.tabMenu.add(this.client, this.tabMenu, Text.translatable("world.create.tab.world"), this.ADVANCED_SETTINGS_ICON, () -> {
			this.navigator.selectTab(1, true);
			this.currentTab = 1;
		});

		this.tabMenu.add(this.client, this.tabMenu, Text.translatable("world.create.tab.advanced"), this.CHEATS_SETTINGS_ICON, () -> {
			this.navigator.selectTab(2, true);
			this.currentTab = 2;
		});

		this.tabMenu.selectTab(this.currentTab);
		this.grid = new GridWidget().setColumnSpacing(8);

		GridWidget.Adder adder = this.grid.createAdder(2);
		Positioner positioner = adder.copyPositioner().marginLeft(this.navigatorWidth);
		adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> this.createLevel()).size(this.createWorldWidth, this.createWorldHeight).build(), positioner);
		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
			this.onCloseScreen();
			WorldIconScreen.SELECTED_ICON = null;
		}).size(this.cancelWidth, this.cancelHeight).build());
		this.grid.forEachChild(child -> {
			child.setNavigationOrder(1);
			this.addDrawableChild(child);
		});

		this.navigator.selectTab(currentTab, false);
		this.worldCreator.update();
		initTabNavigation();
	}


	@Inject(method = "render", at = @At("HEAD"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
	}

	@Override
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
		this.navigator.init();
		this.grid.refreshPositions();
		SimplePositioningWidget.setPos(this.grid, 0, this.height - 36, this.navigatorWidth, 36);
		int i = this.navigator.getNavigationFocus().getBottom();
		ScreenRect screenRect = new ScreenRect(0, i, this.navigatorWidth, this.grid.getY() - i);
		this.tabManager.setTabArea(screenRect);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//		if (this.navigator.trySwitchTabsWithKey(keyCode)) {
//			return true;
//		}
		this.prevBtn = this.getFocused();
		if (Screen.hasShiftDown()) {
			switch (keyCode) {
				case 265: {
					if (this.currentTab != 0) {
						this.currentTab--;
						this.navigator.selectTab(this.currentTab, true);
						this.tabMenu.selectTab(this.currentTab);
						this.tabMenu.setFocused(this.tabMenu.children().get(this.currentTab));
					}
					return true;
				}
				case 264: {
					if (this.tabMenu.children().size() > this.currentTab + 1) {
						this.currentTab++;
						this.navigator.selectTab(this.currentTab, true);
						this.tabMenu.selectTab(this.currentTab);
						this.tabMenu.setFocused(this.tabMenu.children().get(this.currentTab));
					}
					return true;
				}
			}
		}
		if (keyCode == 263 || keyCode == 262) {
			super.keyPressed(keyCode, scanCode, modifiers);
			if (this.getFocused() instanceof OptionsTabWidget) {
				this.setFocused(this.prevBtn);
			}
			this.navigator.selectTab(this.currentTab, false);
			this.tabMenu.selectTab(this.currentTab);
			this.tabMenu.setFocused(this.tabMenu.children().get(this.currentTab));
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


