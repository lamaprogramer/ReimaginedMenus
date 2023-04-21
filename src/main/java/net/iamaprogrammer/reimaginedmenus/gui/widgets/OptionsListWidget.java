package net.iamaprogrammer.reimaginedmenus.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class OptionsListWidget<E extends Screen> extends EntryListWidget<OptionsListWidget.OptionsPackEntry> {
    static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier("textures/gui/resource_packs.png");
    static final Text INCOMPATIBLE = Text.translatable("pack.incompatible");
    static final Text INCOMPATIBLE_CONFIRM = Text.translatable("pack.incompatible.confirm.title");
    private static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");

    private final E screen;
    private final Text title;
    private final int size;
    private final int width;
    private final int height;
    //final OptionsScreen screen;

    public OptionsListWidget(MinecraftClient client, E screen, int width, int height, int size, Text title) {
        super(client, width, height, height/2, height, size+10);
        this.screen = screen;
        this.title = title;
        this.size = size;
        this.width = width;
        this.height = height;
        this.centerListVertically = false;
        Objects.requireNonNull(client.textRenderer);
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        int textureWidth = this.width;
        int textureHeight = (this.height/2) - 20;

        int texturePositionX = this.left + (textureWidth/4);
        int texturePositionY = (textureHeight/4);

        RenderSystem.setShaderTexture(0, new Identifier("minecraft", "textures/misc/unknown_server.png"));
        DrawableHelper.drawTexture(matrices, texturePositionX, texturePositionY, 0.0F, 0.0F, 100, 75, 100, 75);

    }

    protected void renderHeader(MatrixStack matrices, int x, int y) {
        Text text = Text.empty().append(this.title).formatted(new Formatting[]{Formatting.UNDERLINE, Formatting.BOLD});
        this.client.textRenderer.draw(matrices, text, (float)(x + this.width / 2 - this.client.textRenderer.getWidth(text) / 2), (float)Math.min(this.top + 3, y), 16777215);
    }

    public int getRowWidth() {
        return this.width;
    }

    protected int getScrollbarPositionX() {
        return this.right - 6;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getSelectedOrNull() != null) {
            switch (keyCode) {
                case 32, 257 -> {
                    //((OptionsPackEntry) this.getSelectedOrNull()).toggle();
                    return true;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public int add(MinecraftClient client, OptionsListWidget widget, Text name, Identifier icon, OptionsPackEntry.PressAction action) {
        OptionsListWidget.OptionsPackEntry entry = new OptionsPackEntry(client, widget, name, icon, action);
        entry.setId(this.children().size());
        this.children().add(entry);
        return entry.getId();
    }

    public OptionsPackEntry get(int i) {
        return this.children().get(i);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        OptionsPackEntry entry = this.getHoveredEntry();
        if (entry != null) {
            this.appendNarrations(builder.nextMessage(), entry);
            entry.appendNarrations(builder);
        } else {
            OptionsPackEntry entry2 = this.getSelectedOrNull();
            if (entry2 != null) {
                this.appendNarrations(builder.nextMessage(), entry2);
                entry2.appendNarrations(builder);
            }
        }

        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class OptionsPackEntry extends AlwaysSelectedEntryListWidget.Entry<net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget.OptionsPackEntry> {
        private static final int field_32397 = 0;
        private static final int field_32398 = 32;
        private static final int field_32399 = 64;
        private static final int field_32400 = 96;
        private static final int field_32401 = 0;
        private static final int field_32402 = 32;
        private static final int field_32403 = 157;
        private static final int field_32404 = 157;
        private static final String ELLIPSIS = "...";
        private final net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget widget;

        protected final MinecraftClient client;
        private final OrderedText optionName;
        private final PressAction pressAction;
        private final Identifier tabIcon;
        private int id = 0;

        public OptionsPackEntry(MinecraftClient client, OptionsListWidget widget, Text name, Identifier icon, PressAction action) {
            this.client = client;
            this.widget = widget;
            this.tabIcon = icon;
            this.pressAction = action;
            this.optionName = trimTextToWidth(client, name);
        }

        public void setId(int i) {
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
            int i = client.textRenderer.getWidth(text);
            if (i > 157) {
                StringVisitable stringVisitable = StringVisitable.concat(new StringVisitable[]{client.textRenderer.trimToWidth(text, 157 - client.textRenderer.getWidth("...")), StringVisitable.plain("...")});
                return Language.getInstance().reorder(stringVisitable);
            } else {
                return text.asOrderedText();
            }
        }

        private static MultilineText createMultilineText(MinecraftClient client, Text text) {
            return MultilineText.create(client.textRenderer, text, 157, 2);
        }

        public Text getNarration() {
            return Text.translatable("narrator.select", new Object[]{this.optionName});
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
//            ResourcePackCompatibility resourcePackCompatibility = this.pack.getCompatibility();
//            if (!resourcePackCompatibility.isCompatible()) {
//                DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, -8978432);
//            }

            RenderSystem.setShaderTexture(0, this.tabIcon);
            DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, widget.size, widget.size, widget.size, widget.size);
            OrderedText orderedText = this.optionName;
            //MultilineText multilineText = this.description;
            if (((Boolean)this.client.options.getTouchscreen().getValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
                //RenderSystem.setShaderTexture(0, net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget.RESOURCE_PACKS_TEXTURE);
                DrawableHelper.fill(matrices, x, y, x + widget.size, y + widget.size, -1601138544);
                int i = mouseX - x;
                int j = mouseY - y;
//                if (!this.pack.getCompatibility().isCompatible()) {
//                    orderedText = this.incompatibleText;
//                    multilineText = this.compatibilityNotificationText;
//                }


                if (i < 32) {
                    DrawableHelper.drawTexture(matrices, x, y, 0.0F, 16.0F, widget.size, widget.size, 256, 256);
                } else {
                    DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, widget.size, widget.size, 256, 256);
                }

            }

            this.client.textRenderer.drawWithShadow(matrices, orderedText, (float)(x + widget.size + 2), (float)(y + 1), 16777215);
            //multilineText.drawWithShadow(matrices, x + 32 + 2, y + 12, 10, 8421504);
        }

        public String getName() {
            return this.optionName.toString();
        }

//        private boolean isSelectable() {
//            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
//        }

//        public void toggle() {
//            if (this.pack.canBeEnabled() && this.enable()) {
//                this.widget.screen.switchFocusedList(this.widget);
//            } else if (this.pack.canBeDisabled()) {
//                this.pack.disable();
//                this.widget.screen.switchFocusedList(this.widget);
//            }
//        }

//        void moveTowardStart() {
//            if (this.pack.canMoveTowardStart()) {
//                this.pack.moveTowardStart();
//            }
//
//        }
//
//        void moveTowardEnd() {
//            if (this.pack.canMoveTowardEnd()) {
//                this.pack.moveTowardEnd();
//            }
//
//        }

//        private boolean enable() {
//            if (this.pack.getCompatibility().isCompatible()) {
//                this.pack.enable();
//                return true;
//            } else {
//                Text text = this.pack.getCompatibility().getConfirmMessage();
//                this.client.setScreen(new ConfirmScreen((confirmed) -> {
//                    this.client.setScreen(this.widget.screen);
//                    if (confirmed) {
//                        this.pack.enable();
//                    }
//
//                }, net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget.INCOMPATIBLE_CONFIRM, text));
//                return false;
//            }
//        }

        @Environment(EnvType.CLIENT)
        public interface PressAction {
            void onPress();
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            } else {
                double d = mouseX - (double)this.widget.getRowLeft();
                double e = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
                if (d <= 16.0) {
                    //this.widget.screen.clearSelection();
                    //this.enable();
                    this.pressAction.onPress();
                    return true;
                }

                return false;
            }
        }
    }
}
