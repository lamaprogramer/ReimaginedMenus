package net.iamaprogrammer.reimaginedmenus.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class WorldListWidgetMixin {

    @Shadow protected abstract void openReadingWorldScreen();

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private LevelSummary level;

    @Shadow @Final private SelectWorldScreen screen;

    @Redirect(method = "getIconTexture", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Preconditions;checkState(ZLjava/lang/Object;)V"), allow = 2)
    private void injected(boolean expression, Object errorMessage) {
    }

    @Inject(method = "recreate", at = @At("HEAD"), cancellable = true)
    public void recreate(CallbackInfo ci) {
        this.openReadingWorldScreen();
        try (LevelStorage.Session session = this.client.getLevelStorage().createSession(this.level.getName());){
            Pair<LevelInfo, GeneratorOptionsHolder> pair = this.client.createIntegratedServerLoader().loadForRecreation(session);
            LevelInfo levelInfo = pair.getFirst();
            GeneratorOptionsHolder generatorOptionsHolder = pair.getSecond();
            Path path = CreateWorldScreen.copyDataPack(session.getDirectory(WorldSavePath.DATAPACKS), this.client);
            if (generatorOptionsHolder.generatorOptions().isLegacyCustomizedType()) {
                this.client.setScreen(new ConfirmScreen(confirmed -> this.client.setScreen(confirmed ? CreateWorldScreen.create(this.client, this.screen, levelInfo, generatorOptionsHolder, path) : this.screen), Text.translatable("selectWorld.recreate.customized.title"), Text.translatable("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
            } else {

                this.client.setScreen(CreateWorldScreen.create(this.client, this.screen, levelInfo, generatorOptionsHolder, path));
            }
        } catch (Exception exception) {
            //LOGGER.error("Unable to recreate world", exception);
            this.client.setScreen(new NoticeScreen(() -> this.client.setScreen(this.screen), Text.translatable("selectWorld.recreate.error.title"), Text.translatable("selectWorld.recreate.error.text")));
        }
        ci.cancel();
    }
}
