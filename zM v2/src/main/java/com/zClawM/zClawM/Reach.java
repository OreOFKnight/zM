package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class Reach {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static double min = 3.0;
    private static double max = 4.0;
    private static double chance = 75.0;

    private static boolean weaponOnly = false;
    private static boolean movingOnly = false;
    private static boolean sprintOnly = false;
    private static boolean hitThroughBlocks = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(MouseEvent event) {
        if (!zClawMMod.reachEnabled) return;
        if (event.button >= 0 && event.buttonstate && nullCheck()) handleReach();
    }

    private static void handleReach() {
        if (!nullCheck()) return;
        if (weaponOnly && !holdingWeapon()) return;
        if (movingOnly && !isMoving()) return;
        if (sprintOnly && !mc.thePlayer.isSprinting()) return;
        if (Math.random() * 100 > chance) return;

        double reach = min + (Math.random() * (max - min));
        Object[] data = getEntity(reach);

        if (data == null) return;

        Entity entity = (Entity) data[0];
        Vec3 hitVec = (Vec3) data[1];

        if (!hitThroughBlocks) {
            MovingObjectPosition block = mc.thePlayer.rayTrace(reach, 1.0F);
            if (block != null && block.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                double blockDist = mc.thePlayer.getPositionEyes(1F).distanceTo(block.hitVec);
                double entityDist = mc.thePlayer.getPositionEyes(1F).distanceTo(hitVec);
                if (blockDist < entityDist) return;
            }
        }

        mc.objectMouseOver = new MovingObjectPosition(entity, hitVec);
        mc.pointedEntity = entity;
    }

    private static Object[] getEntity(double reach) {
        Entity view = mc.getRenderViewEntity();
        if (view == null || mc.theWorld == null) return null;

        Vec3 eyes = view.getPositionEyes(1F);
        Vec3 look = view.getLook(1F);
        Vec3 reachVec = eyes.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        Entity target = null;
        Vec3 hitVec = null;
        double closest = Double.MAX_VALUE;

        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                view,
                view.getEntityBoundingBox().addCoord(
                        look.xCoord * reach,
                        look.yCoord * reach,
                        look.zCoord * reach
                ).expand(1, 1, 1)
        );

        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            if (!(e instanceof EntityLivingBase) && !(e instanceof EntityItemFrame)) continue;

            float border = e.getCollisionBorderSize();
            AxisAlignedBB bb = e.getEntityBoundingBox().expand(border, border, border);
            MovingObjectPosition mop = bb.calculateIntercept(eyes, reachVec);

            if (mop == null) continue;

            double dist = eyes.distanceTo(mop.hitVec);
            if (dist < closest) {
                closest = dist;
                target = e;
                hitVec = mop.hitVec;
            }
        }

        if (target == null) return null;
        return new Object[]{target, hitVec};
    }

    private static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    private static boolean holdingWeapon() {
        if (mc.thePlayer.getCurrentEquippedItem() == null) return false;
        String name = mc.thePlayer.getCurrentEquippedItem().getUnlocalizedName().toLowerCase();
        return name.contains("sword") || name.contains("axe");
    }

    private static boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public static void setMin(double v) {
        min = Math.max(0, v);
        if (min > max) max = min;
    }

    public static void setMax(double v) {
        max = Math.max(min, v);
    }

    public static void setChance(double v) {
        chance = Math.max(0, Math.min(100, v));
    }

    public static double getMin() { return min; }
    public static double getMax() { return max; }
    public static double getChance() { return chance; }
}
