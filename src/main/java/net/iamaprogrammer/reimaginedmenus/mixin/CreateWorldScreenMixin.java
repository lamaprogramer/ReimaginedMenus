package net.iamaprogrammer.reimaginedmenus.mixin;

import com.google.common.collect.ImmutableList;
import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.iamaprogrammer.reimaginedmenus.gui.tabs.BasicTab;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.iamaprogrammer.reimaginedmenus.util.MenuSettings;
import net.iamaprogrammer.reimaginedmenus.util.TabUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

	@Shadow @Final private TabManager tabManager;
	@Shadow @Final private WorldCreator worldCreator;
	@Shadow protected abstract void createLevel();
	@Shadow public abstract void onCloseScreen();
	@Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

	@Shadow public abstract void renderBackgroundTexture(DrawContext context);

	private final CreateWorldScreen target =  ((CreateWorldScreen)(Object)this);
	private OptionsTabWidget navigator;
	private OptionsListWidget tabMenu;
	private int currentTab = 0;
	private Element prevBtn;

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

	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	protected void newInit(CallbackInfo ci) {
		this.tabMenuWidth = this.width/3;
		this.navigatorWidth = (int)(this.width/1.5);

		this.createWorldWidth = (this.navigatorWidth/2) - 20;
		this.createWorldHeight = 20;

		this.cancelWidth = (this.navigatorWidth/2) - 20;
		this.cancelHeight = 20;


		this.tabMenu = new OptionsListWidget(this.client, this.worldCreator, this.tabMenuWidth, this.height, 20, Text.translatable("world.create.settings"));
		this.addDrawableChild(this.tabMenu);

		TabUtils utils = new TabUtils(this.client, this.worldCreator, this.textRenderer);
		this.navigator = MenuSettings.init(utils, this.tabManager, this.target, this.navigatorWidth, this.tabMenuWidth);
		this.addDrawableChild(this.navigator);


		ImmutableList<BasicTab> tabs = this.navigator.getTabs();
		for (int i = 0; i < MenuSettings.numberOfTabs; i++) {
			BasicTab tab = tabs.get(i);
			this.tabMenu.add(this.client, this.tabMenu, Text.translatable(tab.translationKey), tab.icon, i, (id) -> {
				this.navigator.selectTab(id, true);
				this.currentTab = id;
			});
		}

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

		ci.cancel();
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"), index = 1)
	public int divider(int x) {
		return this.tabMenuWidth+1;
	}

	@Inject(method = "render", at = @At("HEAD"))
	public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		this.renderBackgroundTexture(context);
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


