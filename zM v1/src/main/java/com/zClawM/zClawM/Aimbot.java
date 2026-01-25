package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public final class Aimbot {

    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityPlayer target = null;

    private final float range = 4.5F;
    private final float maxFov = 90.0F; 
    private final float snapSpeed = 18.0F; 
    private final float trackingSpeed = 12.0F; 
    private final boolean perfectAim = true;

    private boolean isLocked = false;
    private int lockTicks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!zClawMMod.aimbotEnabled) {
            target = null;
            isLocked = false;
            lockTicks = 0;
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null) return;

        EntityPlayer previousTarget = target;
        updateTarget();

        if (previousTarget != target) {
            isLocked = false;
            lockTicks = 0;
        }

        if (target != null && !target.isDead && mc.thePlayer.canEntityBeSeen(target)) {
            aimAtTarget();
            lockTicks++;
        } else {
            isLocked = false;
            lockTicks = 0;
        }
    }

    private void updateTarget() {
        target = null;
        double closestDistSq = (range * range) + 1.0D;

        @SuppressWarnings("unchecked")
        List<EntityPlayer> players = mc.theWorld.playerEntities;

        for (EntityPlayer player : players) {
            if (player == mc.thePlayer) continue;
            if (player.isDead) continue;
            if (player.isInvisible()) continue;
            if (!mc.thePlayer.canEntityBeSeen(player)) continue;

            double dx = player.posX - mc.thePlayer.posX;
            double dy = player.posY - mc.thePlayer.posY;
            double dz = player.posZ - mc.thePlayer.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > range * range) continue;

            float[] rotations = getRotationsNeeded(player);
            if (rotations == null) continue;

            float yawDiff = Math.abs(getAngleDifference(rotations[0], mc.thePlayer.rotationYaw));
            if (yawDiff > maxFov) continue;

            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                target = player;
            }
        }
    }

    private void aimAtTarget() {
        if (target == null || target.isDead) return;

        float[] needed = getRotationsNeeded(target);
        if (needed == null) return;

        float targetYaw = needed[0];
        float targetPitch = needed[1];

        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;

        float yawDiff = getAngleDifference(targetYaw, currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        float distanceToTarget = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

        if (perfectAim && isLocked) {
            mc.thePlayer.rotationYaw = targetYaw;
            mc.thePlayer.rotationPitch = targetPitch;
            return;
        }

        float currentSpeed;
        if (!isLocked && distanceToTarget > 5.0F) {
            currentSpeed = snapSpeed;
        } else {
            currentSpeed = trackingSpeed;
            isLocked = true;
        }

        float moveDistance = Math.min(currentSpeed, distanceToTarget);
        float moveRatio = moveDistance / Math.max(distanceToTarget, 0.01F);

        float yawMove = yawDiff * moveRatio;
        float pitchMove = pitchDiff * moveRatio;

        mc.thePlayer.rotationYaw = currentYaw + yawMove;
        mc.thePlayer.rotationPitch = MathHelper.clamp_float(currentPitch + pitchMove, -90F, 90F);
    }

    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null || mc.thePlayer == null) return null;

        double motionX = (entity.posX - entity.lastTickPosX);
        double motionY = (entity.posY - entity.lastTickPosY);
        double motionZ = (entity.posZ - entity.lastTickPosZ);

        float distance = mc.thePlayer.getDistanceToEntity(entity);
        float predictionMultiplier = MathHelper.clamp_float(distance / range, 0.5F, 2.0F);

        double targetX = entity.posX + motionX * predictionMultiplier;
        double targetY = entity.posY + entity.getEyeHeight() - 0.1D + motionY * predictionMultiplier;
        double targetZ = entity.posZ + motionZ * predictionMultiplier;

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.posZ;

        double diffX = targetX - playerX;
        double diffZ = targetZ - playerZ;
        double diffY = targetY - playerY;

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0D / Math.PI));

        return new float[]{
            MathHelper.wrapAngleTo180_float(yaw),
            MathHelper.clamp_float(pitch, -90.0F, 90.0F)
        };
    }

    private float getAngleDifference(float a, float b) {
        float diff = a - b;
        while (diff < -180.0F) diff += 360.0F;
        while (diff >= 180.0F) diff -= 360.0F;
        return diff;
    }
}