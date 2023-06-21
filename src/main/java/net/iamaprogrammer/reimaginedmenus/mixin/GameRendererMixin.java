package net.iamaprogrammer.reimaginedmenus.mixin;

import net.iamaprogrammer.reimaginedmenus.gui.screen.WorldIconScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow private boolean hasWorldIcon;

    @Shadow private long lastWorldIconUpdate;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "updateWorldIcon()V", at = @At(value = "HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        updateIcon(WorldIconScreen.SELECTED_ICON, WorldIconScreen.SELECTED_ICON != null);
        WorldIconScreen.SELECTED_ICON = null;
        ci.cancel();

    }

    public void updateIcon(String iconPath, boolean isCustom) {
        if (this.hasWorldIcon || !this.client.isInSingleplayer()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastWorldIconUpdate < 1000L) {
            return;
        }
        this.lastWorldIconUpdate = l;
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer == null || integratedServer.isStopped()) {
            return;
        }
        integratedServer.getIconFile().ifPresent(path -> {
            if (Files.isRegularFile(path, new LinkOption[0])) {
                this.hasWorldIcon = true;
            } else {
                imagePathToIcon(iconPath, path, isCustom);
            }
        });
    }

    private void imagePathToIcon(String path, Path destination, boolean isCustom) {
        try {
            NativeImage nativeImage = null;
            if (isCustom) {
                byte[] imageData = Files.readAllBytes(Paths.get(path));
                nativeImage = NativeImage.read(imageData);
                genImage(nativeImage, destination);
            } else {
                if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
                    nativeImage = ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer());
                    genImage(nativeImage, destination);
                }
            }

        } catch (IOException iOException) {
            LOGGER.warn("Couldn't save auto screenshot", iOException);
        }
    }

    private void genImage(NativeImage nativeImage, Path destination) {
        Util.getIoWorkerExecutor().execute(() -> {
            int i = nativeImage.getWidth();
            int j = nativeImage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
                k = (i - j) / 2;
                i = j;
            } else {
                l = (j - i) / 2;
                j = i;
            }
            try (NativeImage nativeImage2 = new NativeImage(256, 256, false);){
                if (nativeImage.getWidth() > 256 || nativeImage.getHeight() > 256) {
                    nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
                    nativeImage2.writeTo(destination);
                } else {
                    nativeImage.writeTo(destination);
                }
            } catch (IOException iOException) {
                LOGGER.warn("Couldn't save auto screenshot", iOException);
            } finally {
                nativeImage.close();
            }
        });
    }
}
