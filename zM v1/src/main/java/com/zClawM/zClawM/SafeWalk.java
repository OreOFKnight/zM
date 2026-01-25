package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SafeWalk {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean wasForcingSneak = false;
    private boolean sneakAtEdges = true;
    private double edgeDistance = 0.1;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!zClawMMod.safeWalkEnabled || event.phase != TickEvent.Phase.START) {
            if (wasForcingSneak) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                wasForcingSneak = false;
            }
            return;
        }

        EntityPlayer player = event.player;
        if (player == null || !player.onGround || player.capabilities.isFlying) {
            if (wasForcingSneak) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                wasForcingSneak = false;
            }
            return;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (wasForcingSneak) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                wasForcingSneak = false;
            }
            return;
        }

        if (sneakAtEdges) {
            boolean atEdge = isAtEdge(player);
            
            if (atEdge && !wasForcingSneak) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                KeyBinding.onTick(mc.gameSettings.keyBindSneak.getKeyCode());
                wasForcingSneak = true;
            } else if (!atEdge && wasForcingSneak) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                wasForcingSneak = false;
            }
        } else if (wasForcingSneak) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            wasForcingSneak = false;
        }
    }

    private boolean isAtEdge(EntityPlayer player) {
        AxisAlignedBB box = player.getEntityBoundingBox();
        AxisAlignedBB adjustedBox = box.expand(0, -0.05, 0)
                .contract(edgeDistance, 0, edgeDistance);

        for (double x = adjustedBox.minX; x <= adjustedBox.maxX; x += 0.1) {
            for (double z = adjustedBox.minZ; z <= adjustedBox.maxZ; z += 0.1) {
                BlockPos pos = new BlockPos(x, adjustedBox.minY - 0.1, z);
                if (mc.theWorld.getBlockState(pos).getBlock().getMaterial().isSolid()) {
                    return false; 
                }
            }
        }
        return true; 
    }
}