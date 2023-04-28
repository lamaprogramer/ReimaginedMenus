package net.iamaprogrammer.reimaginedmenus.mixin;

import net.minecraft.client.gui.screen.world.WorldListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldListWidget.WorldEntry.class)
public class WorldListWidgetMixin {

    @Redirect(method = "getIconTexture", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Preconditions;checkState(ZLjava/lang/Object;)V"), allow = 2)
    private void injected(boolean expression, Object errorMessage) {
    }
}
