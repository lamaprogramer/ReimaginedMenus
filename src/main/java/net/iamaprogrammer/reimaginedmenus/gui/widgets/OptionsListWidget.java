package net.iamaprogrammer.reimaginedmenus.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.world.gen.WorldPreset;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Optional;

public class OptionsListWidget extends EntryListWidget<OptionsListWidget.OptionsPackEntry> {
    static final Identifier VERTICAL_SEPARATOR_TEXTURE = new Identifier("reimaginedmenus","textures/gui/vertical_separator.png");
    private static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");
    private final Identifier DEFAULT_WORLD_IMAGE = new Identifier("reimaginedmenus", "textures/misc/normal.png");
    private final WorldCreator worldCreator;
    private final Text title;
    private final int size;
    private final int listWidth;
    private final int listHeight;
    private final int top;
    //final OptionsScreen screen;

    public OptionsListWidget(MinecraftClient client, WorldCreator worldCreator, int width, int height, int size, Text title) {
        super(client, width, height, height/2, size+10);
        this.worldCreator = worldCreator;
        this.title = title;
        this.size = size;
        this.listWidth = width;
        this.listHeight = height;
        this.top = height/2;
        this.centerListVertically = false;
        Objects.requireNonNull(client.textRenderer);
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        int texturePositionX = (this.listWidth - 128)/2;
        int texturePositionY = ((this.listHeight / 2)- 72)/2;
        // small dimensions: 192, 108

        if (this.worldCreator != null) {
            Identifier texture = new Identifier("reimaginedmenus", "textures/misc/normal.png");
            if (worldCreator.getWorldType().preset() != null) {
                Optional<RegistryKey<WorldPreset>> key = worldCreator.getWorldType().preset().getKey();
                if (key.isPresent()) {
                    String path = key.get().getValue().getPath();
                    Identifier resource = new Identifier("reimaginedmenus", "textures/misc/"+path+".png");
                    try {
                        this.client.getResourceManager().getResourceOrThrow(resource);
                        texture = resource;
                    } catch (FileNotFoundException ignored) {}
                }
            }
            context.drawTexture(texture, texturePositionX, texturePositionY, 0.0F, 0.0F, 128, 72, 128, 72);
        } else {
            context.drawTexture(this.DEFAULT_WORLD_IMAGE, texturePositionX, texturePositionY, 0.0F, 0.0F, 128, 72, 128, 72);
        }
        context.drawTexture(VERTICAL_SEPARATOR_TEXTURE, this.listWidth, 0, 0.0F, 0.0F, 2, this.listHeight, 2, 32);

    }

    public void selectTab(int id) {
        super.setSelected(super.children().get(id));
        super.setFocused(super.children().get(id));
    }

    protected void renderHeader(DrawContext context, int x, int y) {
        Text text = Text.empty().append(this.title).formatted(new Formatting[]{Formatting.UNDERLINE, Formatting.BOLD});
        context.drawText(this.client.textRenderer, text, (x + this.width / 2 - this.client.textRenderer.getWidth(text) / 2), Math.min(this.top + 3, y), 16777215, true);
    }

    public int getRowWidth() {
        return this.listWidth;
    }

    protected int getScrollbarPositionX() {
        return this.getRight() - 6;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void add(MinecraftClient client, OptionsListWidget widget, Text name, Identifier icon, int id, OptionsPackEntry.PressAction action) {
        OptionsListWidget.OptionsPackEntry entry = new OptionsPackEntry(client, widget, name, this.listWidth, icon, id, action);
        entry.setId(this.children().size());
        this.children().add(entry);
        //return entry.getId();
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
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

    public static class OptionsPackEntry extends AlwaysSelectedEntryListWidget.Entry<net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget.OptionsPackEntry> {
        private final net.iamaprogrammer.reimaginedmenus.gui.widgets.OptionsListWidget widget;

        protected final MinecraftClient client;
        private final OrderedText optionName;
        private final PressAction pressAction;
        private final Identifier tabIcon;
        private int id = 0;

        private final int width;

        public OptionsPackEntry(MinecraftClient client, OptionsListWidget widget, Text name, int width, Identifier icon, int id, PressAction action) {
            this.client = client;
            this.widget = widget;
            this.tabIcon = icon;
            this.width = width;
            this.pressAction = action;
            this.id = id;
            this.optionName = trimTextToWidth(client, name);
        }

        public void setId(int i) {
            this.id = i;
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

        public Text getNarration() {
            return Text.translatable("narrator.select", new Object[]{this.optionName});
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTexture(this.tabIcon, x+((entryHeight-widget.size)/2), y +((entryHeight-widget.size)/2), 0.0F, 0.0F, widget.size, widget.size, widget.size, widget.size);
            OrderedText orderedText = this.optionName;

            if (((Boolean)this.client.options.getTouchscreen().getValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
                context.fill(x+((entryHeight-widget.size)/2), y+((entryHeight-widget.size)/2), x + widget.size +((entryHeight-widget.size)/2), y + widget.size +((entryHeight-widget.size)/2), -1601138544);

            }
            int textPosX = (entryWidth - this.client.textRenderer.getWidth(orderedText))/2;
            context.drawText(this.client.textRenderer, orderedText, (x + textPosX), (y + ((entryHeight-7)/2)), 16777215, true);
        }


        public interface PressAction {
            void onPress(int id);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            } else {
                double d = mouseX - (double)this.widget.getRowLeft();
                double e = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
                if (d <= this.width) {
                    this.pressAction.onPress(this.id);
                    return true;
                }

                return false;
            }
        }
    }
}
