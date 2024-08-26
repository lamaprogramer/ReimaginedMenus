package net.iamaprogrammer.reimaginedmenus.mixin;

import com.google.gson.JsonObject;
import net.iamaprogrammer.reimaginedmenus.gui.WorldScreenManager;
import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.AdvancedTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.GeneralTab;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.WorldTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.iamaprogrammer.reimaginedmenus.util.MenuSettings;
import net.iamaprogrammer.reimaginedmenus.util.ProportionManager;
import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
	@Unique
	private static final JsonObject icons = MenuSettings.getIcons();
	@Unique
	private static final Identifier GENERAL_SETTINGS_ICON = MenuSettings.getIconFromJson("general_settings_icon", icons, Identifier.of("minecraft", "textures/block/crafting_table_top.png"));
	@Unique
	private static final Identifier WORLD_SETTINGS_ICON = MenuSettings.getIconFromJson("world_settings_icon", icons, Identifier.of("minecraft", "textures/block/furnace_front.png"));
	@Unique
	private static final Identifier ADVANCED_SETTINGS_ICON = MenuSettings.getIconFromJson("advanced_settings_icon", icons, Identifier.of("minecraft", "textures/block/enchanting_table_top.png"));

	@Shadow @Final private TabManager tabManager;
	@Shadow @Final private WorldCreator worldCreator;
	@Shadow protected abstract void createLevel();
	@Shadow public abstract void onCloseScreen();
	@Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);
	@Shadow protected abstract void renderDarkening(DrawContext context);

	@Unique
	private final CreateWorldScreen target =  ((CreateWorldScreen)(Object)this);

	@Unique
	private WorldScreenManager screenManager;
	@Unique
	private GridWidget grid;

    protected CreateWorldScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	private void reimaginedmenus_Init(CallbackInfo ci) {
        ProportionManager proportionManager = new ProportionManager();
		proportionManager.addColumnProportion(1.0f/3.0f); // 1/3
		proportionManager.addColumnProportion(2.0f/3.0f); // 2/3

		TabUtils utils = new TabUtils(this.client, this.worldCreator, this.textRenderer);
		this.screenManager = new WorldScreenManager(this.width, this.height, proportionManager);
		this.screenManager.addTab(new GeneralTab(utils, target, "world.create.tab.general", GENERAL_SETTINGS_ICON, this.screenManager.getNavigatorWidth(), this.screenManager.getTabMenuWidth()));
		this.screenManager.addTab(new WorldTab(utils, target,  "world.create.tab.world", WORLD_SETTINGS_ICON, this.screenManager.getNavigatorWidth(), this.screenManager.getTabMenuWidth()));
		this.screenManager.addTab(new AdvancedTab(utils, target, "world.create.tab.advanced", ADVANCED_SETTINGS_ICON, this.screenManager.getNavigatorWidth(), this.screenManager.getTabMenuWidth()));
		this.screenManager.init(utils, this.tabManager);

        int buttonWidth = (this.screenManager.getTabMenuWidth() / 2) - 20;
        int buttonHeight = 20;

		this.addDrawableChild(this.screenManager.getOptionsListWidget());
		this.addDrawableChild(this.screenManager.getNavigator());

		this.grid = new GridWidget().setColumnSpacing(8);
		GridWidget.Adder adder = this.grid.createAdder(2);

		adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"),
			button -> this.createLevel()
		).size(buttonWidth, buttonHeight).build(), this.grid.getMainPositioner());

		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
			this.onCloseScreen();
			WorldIconScreen.SELECTED_ICON = null;
		}).size(buttonWidth, buttonHeight).build());

		this.grid.forEachChild(child -> {
			child.setNavigationOrder(1);
			this.addDrawableChild(child);
		});

		this.worldCreator.update();
		initTabNavigation();

		ci.cancel();
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"), index = 1)
	private int reimaginedmenus_ModifyDivider(int x) {
		return this.screenManager.getNavigatorWidth()+1;
	}

	@Redirect(method = "renderDarkening", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;renderDarkening(Lnet/minecraft/client/gui/DrawContext;IIII)V"))
	private void reimaginedmenus_RemoveDarkening(CreateWorldScreen instance, DrawContext drawContext, int x, int y, int width, int height) {}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		this.clearChildren();
		this.init();
		this.initTabNavigation();
	}

	@Override
	public void initTabNavigation() {
		if (this.screenManager.getNavigator() == null || this.grid == null) {
			return;
		}
		this.screenManager.getNavigator().init();
		this.grid.refreshPositions();
		SimplePositioningWidget.setPos(this.grid, this.screenManager.getNavigatorWidth(), this.height - 36, this.screenManager.getTabMenuWidth(), 36);
		ScreenRect screenRect = new ScreenRect(new ScreenPos(this.screenManager.getNavigatorWidth(), 0), this.screenManager.getTabMenuWidth(), this.height);
		this.tabManager.setTabArea(screenRect);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Element prevBtn = this.getFocused();

		if (Screen.hasShiftDown()) {
			switch (keyCode) {
				case 265: {
					if (this.screenManager.getCurrentTab() != 0) {
						this.screenManager.setCurrentTab(this.screenManager.getCurrentTab()-1);
						this.screenManager.selectCurrentTab();
					}
					return true;
				}
				case 264: {
					if (this.screenManager.getOptionsListWidget().children().size() > this.screenManager.getCurrentTab() + 1) {
						this.screenManager.setCurrentTab(this.screenManager.getCurrentTab()+1);
						this.screenManager.selectCurrentTab();
					}
					return true;
				}
			}
		}
		if (keyCode == 263 || keyCode == 262) {
			super.keyPressed(keyCode, scanCode, modifiers);
			if (this.getFocused() instanceof OptionsTabWidget) {
				this.setFocused(prevBtn);
			}
			this.screenManager.selectCurrentTab(false);
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


