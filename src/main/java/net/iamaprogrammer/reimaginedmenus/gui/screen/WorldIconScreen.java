package net.iamaprogrammer.reimaginedmenus.gui.screen;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.iamaprogrammer.reimaginedmenus.gui.widgets.WorldIconListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(value= EnvType.CLIENT)
public class WorldIconScreen extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text DROP_INFO = Text.translatable("world.create.icon.dropinfo").formatted(Formatting.GRAY);
    private static final Text FOLDER_INFO = Text.translatable("world.create.icon.folderinfo");
    @Nullable
    private net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.DirectoryWatcher directoryWatcher;
    public static String SELECTED_ICON;
    private long refreshTimeout;
    private WorldIconListWidget availableIconsList;
    private Map<String, File> files = new HashMap<>();
    private final Path file;
    private ButtonWidget doneButton;
    private final Map<String, Identifier> iconTextures = Maps.newHashMap();

    private final MinecraftClient client;
    private final Screen parent;

    public WorldIconScreen(MinecraftClient client, Screen parent, Path file, Text title) {
        super(title);
        this.client = client;
        this.parent = parent;
        this.file = file;
        if (!file.toFile().exists()) {
            file.toFile().mkdirs();
        }
        this.directoryWatcher = DirectoryWatcher.create(this.file, this);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        this.closeDirectoryWatcher();
    }

    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        this.availableIconsList = new WorldIconListWidget(this.client, this, 200, this.height, Text.translatable("pack.available.title"));
        this.availableIconsList.setLeftPos((this.width - 200)/2);
        this.addSelectableChild(this.availableIconsList);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("world.create.icon.openfolder"), button -> Util.getOperatingSystem().open(this.file.toUri())).dimensions(this.width / 2 - 154, this.height - 48, 150, 20).tooltip(Tooltip.of(FOLDER_INFO)).build());
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 + 4, this.height - 48, 150, 20).build());
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.refreshTimeout = 20L;
                }
            } catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.file);
                LOGGER.warn(String.valueOf(iOException));
                this.closeDirectoryWatcher();

            }
        }
        if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
            this.refresh();
        }
    }

    private void updatePackLists() {
        this.updatePackList(this.availableIconsList);
        this.doneButton.active = true;
    }

    private void updatePackList(WorldIconListWidget widget) {
        List<String> removedItems = new ArrayList<>();
        widget.children().clear();
        WorldIconListWidget.WorldIconEntry resourcePackEntry = (WorldIconListWidget.WorldIconEntry)widget.getSelectedOrNull();
        String string = resourcePackEntry == null ? "" : resourcePackEntry.getName();
        widget.setSelected(null);
        files.forEach((name, file) -> {

            if (file.exists()) {
                WorldIconListWidget.WorldIconEntry entry = null;
                try {
                    entry = new WorldIconListWidget.WorldIconEntry(this.client, widget, file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                widget.children().add(entry);
                if (name.equals(string)) {
                    widget.setSelected(entry);
                }
            } else {
                removedItems.add(name);
                String nameToPath = Path.of(new File(this.client.runDirectory, "worldicons\\").toURI()) + "\\" + name;
                System.out.println(nameToPath);
                System.out.println(WorldIconScreen.SELECTED_ICON);
                WorldIconScreen.SELECTED_ICON = nameToPath.equals(WorldIconScreen.SELECTED_ICON) ? null : WorldIconScreen.SELECTED_ICON;
            }
        });

        removedItems.forEach((key) -> {
            files.remove(key);
        });
        removedItems.clear();
    }


    public void clearSelection() {
        this.availableIconsList.setSelected(null);
    }

    private void refresh() {
        this.updatePackLists();
        this.refreshTimeout = 1L;
        this.iconTextures.clear();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        this.availableIconsList.render(matrices, mouseX, mouseY, delta);
        net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, DROP_INFO, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        srcPaths.forEach(src -> {
            try (Stream<Path> stream = Files.walk(src, new FileVisitOption[0]);){
                stream.forEach(toCopy -> {
                    try {
                        Util.relativeCopy(src.getParent(), destPath, toCopy);
                    } catch (IOException iOException) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", toCopy, destPath, iOException);
                        mutableBoolean.setTrue();
                    }
                });
            } catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", src, (Object)destPath);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.addPackCopyFailure(client, destPath.toString());
        }
    }

    @Override
    public void filesDragged(List<Path> paths) {
        String string = paths.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.client.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.copyPacks(this.client, paths, this.file);
                this.refresh();
            }
            this.client.setScreen(this);
        }, Text.translatable("pack.dropConfirm"), Text.literal(string)));
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectoryWatcher
            implements AutoCloseable {
        private final WatchService watchService;
        private final Path path;
        private final WorldIconScreen wis;

        public DirectoryWatcher(Path path, WorldIconScreen wis) throws IOException {
            this.path = path;
            this.watchService = path.getFileSystem().newWatchService();
            this.wis = wis;
            try {
                this.watchDirectory(path);
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)){
                    for (Path path2 : directoryStream) {
                        if (!Files.isDirectory(path2, LinkOption.NOFOLLOW_LINKS)) {
                            this.wis.files.put(String.valueOf(path2.getFileName()), path2.toFile());
                            continue;
                        };
                        this.watchDirectory(path2);
                    }
                }
            } catch (Exception exception) {
                this.watchService.close();
                throw exception;
            }
        }

        @Nullable
        public static net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.DirectoryWatcher create(Path path, WorldIconScreen screen) {
            try {
                return new net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen.DirectoryWatcher(path, screen);
            } catch (IOException iOException) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)path, (Object)iOException);
                return null;
            }
        }

        private void watchDirectory(Path path) throws IOException {
            if (path != null) {
                path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            }
        }

        public boolean pollForChange() throws IOException {
            WatchKey watchKey;
            boolean bl = false;
            while ((watchKey = this.watchService.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : list) {
                    Path path = null;
                    bl = true;
                    if (watchKey.watchable() != this.path || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.path.resolve((Path)watchEvent.context()), LinkOption.NOFOLLOW_LINKS)) {
                        if (path != null) {
                            this.wis.files.put(String.valueOf(path.getFileName()), path.toFile());
                            continue;
                        }
                    };
                    this.watchDirectory(path);

                }
                watchKey.reset();
            }
            return bl;
        }

        @Override
        public void close() throws IOException {
            this.watchService.close();
        }
    }
}
