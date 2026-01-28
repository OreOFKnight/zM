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
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public final class AimAssist {

    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityPlayer target = null;

    private final float range = 6.7F;
    private final float maxFov = 60.0F; 
    private final float horizontalSpeed = 6.5F; 
    private final float verticalSpeed = 5.0F; 
    private final float stickiness = 0.85F; 

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!zClawMMod.aimAssistEnabled) {
            target = null;
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null) return;

        updateTarget();

        if (target != null && !target.isDead && mc.thePlayer.canEntityBeSeen(target)) {
            aimAtTargetSmooth();
        }
    }

    private void updateTarget() {
        target = null;
        float bestScore = Float.MAX_VALUE;

        @SuppressWarnings("unchecked")
        List<EntityPlayer> players = mc.theWorld.playerEntities;

        for (EntityPlayer player : players) {
            if (player == mc.thePlayer) continue;
            if (player.isDead) continue;
            if (player.isInvisible()) continue;
            if (!mc.thePlayer.canEntityBeSeen(player)) continue;

            String rawName = player.getDisplayName().getFormattedText();
            rawName = EnumChatFormatting.getTextWithoutFormattingCodes(rawName).trim().toLowerCase();
            if (rawName.toLowerCase().startsWith("[bot]")) continue;
            if (rawName.toLowerCase().startsWith("[npc]")) continue;


            float distance = mc.thePlayer.getDistanceToEntity(player);
            if (distance > range) continue;

            if (!isLookingAt(player, 1.0F)) continue;

            float[] rotations = getRotationsNeeded(player);
            if (rotations == null) continue;

            float yawDiff = Math.abs(getAngleDifference(rotations[0], mc.thePlayer.rotationYaw));
            if (yawDiff > maxFov) continue;

            float pitchDiff = Math.abs(rotations[1] - mc.thePlayer.rotationPitch);
            float angleScore = yawDiff + pitchDiff * 0.7F; 
            float distanceScore = distance * 0.3F;
            float totalScore = angleScore + distanceScore;

            if (totalScore < bestScore) {
                bestScore = totalScore;
                target = player;
            }
        }
    }

    private boolean isLookingAt(EntityPlayer player, float partialTicks) {
        Vec3 eyes = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 look = mc.thePlayer.getLook(partialTicks);
        Vec3 reachVec = eyes.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

        AxisAlignedBB bb = player.getEntityBoundingBox().expand(0.4, 0.4, 0.4);
        MovingObjectPosition mop = bb.calculateIntercept(eyes, reachVec);

        return mop != null;
    }

    private void aimAtTargetSmooth() {
        if (target == null || target.isDead) return;

        float[] needed = getRotationsNeeded(target);
        if (needed == null) return;

        float targetYaw = needed[0];
        float targetPitch = needed[1];

        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;

        float yawDiff = getAngleDifference(targetYaw, currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        float totalAngle = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

        float proximity = MathHelper.clamp_float(1.0F - (totalAngle / maxFov), 0.3F, 1.0F);
        float adaptiveStickiness = stickiness * proximity;

        float effectiveHorizontalSpeed = horizontalSpeed * adaptiveStickiness;
        float effectiveVerticalSpeed = verticalSpeed * adaptiveStickiness;

        float yawStep = MathHelper.clamp_float(yawDiff, -effectiveHorizontalSpeed, effectiveHorizontalSpeed);
        float pitchStep = MathHelper.clamp_float(pitchDiff, -effectiveVerticalSpeed, effectiveVerticalSpeed);

        float yawNoise = (mc.theWorld.rand.nextFloat() - 0.5F) * 0.15F;
        float pitchNoise = (mc.theWorld.rand.nextFloat() - 0.5F) * 0.12F;

        mc.thePlayer.rotationYaw = currentYaw + yawStep + yawNoise;
        mc.thePlayer.rotationPitch = MathHelper.clamp_float(currentPitch + pitchStep + pitchNoise, -90F, 90F);
    }

    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null || mc.thePlayer == null) return null;

        double motionX = (entity.posX - entity.lastTickPosX) * 0.3;
        double motionZ = (entity.posZ - entity.lastTickPosZ) * 0.3;

        double targetX = entity.posX + motionX;
        double targetY = entity.posY + entity.getEyeHeight() - 0.15D;
        double targetZ = entity.posZ + motionZ;

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