package net.iamaprogrammer.reimaginedmenus.mixin;

import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsTabWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
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
	private OptionsListWidget<CreateWorldScreen> test;
	private CreateWorldScreen target =  ((CreateWorldScreen)(Object)this);

	private OptionsTabWidget navigator;

	@Nullable
	private GridWidget grid;

	protected CreateWorldScreenMixin(Text title) {
		super(title);
	}

	/**
	 * @author Iamaprogrammer
	 * @reason To completely re-style the menu.
	 */
	@Overwrite
	public void init() {
		//this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(new Tab[]{new CreateWorldScreen.GameTab(), new CreateWorldScreen.WorldTab(), new CreateWorldScreen.MoreTab()}).build();
		this.navigator = OptionsTabWidget.builder(this.tabManager, this.width).tabs(new Tab[]{new CreateWorldScreen.GameTab(), new CreateWorldScreen.WorldTab(), new CreateWorldScreen.MoreTab()}).build();
		CreateWorldScreen tab = target;


		this.test = new OptionsListWidget<>(this.client, this.target, 200, this.height, 16, Text.literal("World Settings"));
		this.grid = (new GridWidget()).setColumnSpacing(10);


		int generalTab = this.test.add(this.client, this.test, Text.literal("General"), this.GENERAL_SETTINGS_ICON, () -> {

		});

		int advancedTab = this.test.add(this.client, this.test, Text.literal("Advanced"), this.ADVANCED_SETTINGS_ICON, () -> {
			//this.openExperimentsScreen(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
		});

		int cheatsTab = this.test.add(this.client, this.test, Text.literal("Cheats"), this.CHEATS_SETTINGS_ICON, () -> {
			//this.openExperimentsScreen(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
		});

		//OptionsListWidget.OptionsPackEntry experimentsEntry = this.test.get(experimentsTab);






		//this.test.setLeftPos();
		this.addDrawableChild(this.test);
//		GridWidget.Adder adder = this.grid.createAdder(2);
//		adder.add(, 2);
//		adder.add(ButtonWidget.builder(Text.translatable("selectWorld.create"), (button) -> {
//			this.createLevel();
//		}).build());
//		adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
//			this.onCloseScreen();
//		}).build());
//		this.grid.forEachChild((child) -> {
//			child.setNavigationOrder(1);
//			this.addDrawableChild(child);
//		});
//		this.tabNavigation.selectTab(0, false);
//		this.worldCreator.update();
//		this.initTabNavigation();
	}



}


