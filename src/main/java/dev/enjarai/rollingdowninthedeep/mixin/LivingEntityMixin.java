package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    protected abstract float getWaterSlowDown();

    @Shadow
    public abstract float getSpeed();

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean onClimbable();

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    protected abstract void jumpOutOfFluid(double oldY);

    /// Replaces vanilla underwater travel for the local player while free-swimming:
    /// movement follows the camera orientation (including roll), there is no gravity
    /// and the vertical drag is the same as the horizontal one.
    @Inject(method = "travelInWater", at = @At("HEAD"), cancellable = true)
    private void rollingDownInTheDeep$freeSwim(Vec3 input, double baseGravity, boolean isFalling, double oldY, CallbackInfo ci) {
        if (!((Object) this instanceof LocalPlayer player) || !RollingDownInTheDeep.shouldRoll()) {
            return;
        }

        float slowDown = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
        float speed = 0.02F;
        float waterWalker = (float) this.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (!this.onGround()) {
            waterWalker *= 0.5F;
        }

        if (waterWalker > 0.0F) {
            slowDown += (0.54600006F - slowDown) * waterWalker;
            speed += (this.getSpeed() - speed) * waterWalker;
        }

        if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            slowDown = 0.96F;
        }

        if (SwimConfig.INSTANCE.applySpeedEffects) {
            // Speed/Slowness effects, beacons and attribute modifiers scale the swim speed.
            // The sprint bonus is divided out, as persistent swimming keeps us sprinting.
            double base = this.isSprinting() ? 0.13 : 0.1;
            double factor = this.getAttributeValue(Attributes.MOVEMENT_SPEED) / base;
            speed *= (float) Math.clamp(factor, 0.25, 4.0);
        }

        Vector3f velocity = RollingDownInTheDeep.movementInputToVelocity(
                player, new Vector3f((float) input.x, (float) input.y, (float) input.z), speed);
        this.setDeltaMovement(this.getDeltaMovement().add(velocity.x, velocity.y, velocity.z));
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 movement = this.getDeltaMovement();
        if (this.horizontalCollision && this.onClimbable()) {
            movement = new Vec3(movement.x, 0.2, movement.z);
        }

        this.setDeltaMovement(movement.multiply(slowDown, slowDown, slowDown));
        this.jumpOutOfFluid(oldY);
        ci.cancel();
    }

    /// Cancel the upwards velocity added by holding jump
    @WrapWithCondition(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpInLiquid(Lnet/minecraft/tags/TagKey;)V")
    )
    private boolean rollingDownInTheDeep$cancelUpwardsSwim(LivingEntity instance, TagKey<Fluid> fluid) {
        return !((Object) this instanceof LocalPlayer && RollingDownInTheDeep.shouldRoll());
    }
}
