package dev.enjarai.rollingdowninthedeep.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import nl.enjarai.doabarrelroll.api.RollRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    /// Apply roll to swimming players
    @Inject(
            method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at = @At("RETURN")
    )
    private void rollingDownInTheDeep$rollPlayerBody(
            AvatarRenderState state, PoseStack poseStack, float bodyRot, float entityScale, CallbackInfo ci
    ) {
        if (state.isVisuallySwimming) {
            poseStack.mulPose(Axis.YP.rotationDegrees(((RollRenderState) (Object) state).doABarrelRoll$getRoll()));
        }
    }
}
