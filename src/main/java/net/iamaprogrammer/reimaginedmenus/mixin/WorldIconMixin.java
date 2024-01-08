package net.iamaprogrammer.reimaginedmenus.mixin;

import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldIcon.class)
public abstract class WorldIconMixin {

    @Shadow protected abstract void assertOpen();

    @Shadow @Nullable private NativeImageBackedTexture texture;

    @Shadow @Final private TextureManager textureManager;

    @Shadow @Final private Identifier id;

    @Shadow public abstract void destroy();


    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    public void removeIconLimits(NativeImage image, CallbackInfo ci) {
        try {
            this.assertOpen();
            if (this.texture == null) {
                this.texture = new NativeImageBackedTexture(image);
            } else {
                this.texture.setImage(image);
                this.texture.upload();
            }

            this.textureManager.registerTexture(this.id, this.texture);
        } catch (Throwable var3) {
            image.close();
            this.destroy();
            throw var3;
        }
        ci.cancel();
    }
}
