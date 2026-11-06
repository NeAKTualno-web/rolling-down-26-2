package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(
            method = "updateSwimming",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;isSprinting()Z",
                    ordinal = 0
            )
    )
    private boolean rollingDownInTheDeep$disableSprintCheck(Entity instance, Operation<Boolean> original) {
        if (SwimConfig.INSTANCE.persistentSwimming) {
            // Override a client-side check that would usually send a packet to
            // stop the player from being in swim mode without sprinting.
            return instance instanceof LocalPlayer &&
                    RollingDownInTheDeep.enabled() ||
                    original.call(instance);
        } else {
            return original.call(instance);
        }
    }
}
