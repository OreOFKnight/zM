package com.zclawm.mod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.List;

public class Scaffold {

    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastPlace;
    private boolean wasSneaking = false;
    private long sneakReleaseTime = 0;

    private final List<Block> invalid = Arrays.asList(
            Blocks.air,
            Blocks.water,
            Blocks.flowing_water,
            Blocks.lava,
            Blocks.flowing_lava,
            Blocks.fire,
            Blocks.torch,
            Blocks.redstone_torch,
            Blocks.unlit_redstone_torch
    );

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!zClawMMod.scaffoldEnabled) return;

        if (wasSneaking && sneakReleaseTime > 0 && System.currentTimeMillis() > sneakReleaseTime) {
            mc.getNetHandler().addToSendQueue(
                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING)
            );
            wasSneaking = false;
            sneakReleaseTime = 0;
        }

        ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return;

        if (System.currentTimeMillis() - lastPlace < 90) return;

        if (mc.thePlayer.rotationPitch < -60) return;

        boolean movingForward = mc.gameSettings.keyBindForward.isKeyDown();
        
        BlockPos targetPos;
        
        if (movingForward) {
            targetPos = getForwardBlockPos();
        } else {
            targetPos = new BlockPos(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 1,
                mc.thePlayer.posZ
            );
        }

        if (!isReplaceable(targetPos)) return;

        BlockData data = getBlockData(targetPos);
        if (data == null) return;

        if (data.face == EnumFacing.UP) {
            data = getAlternativeBlockData(targetPos);
            if (data == null) return;
        }

        place(stack, data, movingForward);
        lastPlace = System.currentTimeMillis();
    }

    private BlockPos getForwardBlockPos() {
        double yawRad = Math.toRadians(mc.thePlayer.rotationYaw);
        double xOffset = -Math.sin(yawRad) * 0.5;
        double zOffset = Math.cos(yawRad) * 0.5;
        
        return new BlockPos(
            mc.thePlayer.posX + xOffset,
            mc.thePlayer.posY - 1,
            mc.thePlayer.posZ + zOffset
        );
    }

    private void place(ItemStack stack, BlockData data, boolean movingForward) {
        boolean needsSneak = (data.face == EnumFacing.NORTH || data.face == EnumFacing.SOUTH || 
                             data.face == EnumFacing.EAST || data.face == EnumFacing.WEST);
        
        if (movingForward && mc.thePlayer.onGround) {
            needsSneak = false;
        }

        if (needsSneak && !wasSneaking) {
            mc.getNetHandler().addToSendQueue(
                new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING)
            );
            wasSneaking = true;
        }

        Vec3 hitVec = new Vec3(
                data.pos.getX() + 0.5 + data.face.getDirectionVec().getX() * 0.5,
                data.pos.getY() + 0.5 + data.face.getDirectionVec().getY() * 0.5,
                data.pos.getZ() + 0.5 + data.face.getDirectionVec().getZ() * 0.5
        );

        boolean success = mc.playerController.onPlayerRightClick(
                mc.thePlayer,
                mc.theWorld,
                stack,
                data.pos,
                data.face,
                hitVec
        );

        if (success) {
            mc.thePlayer.swingItem();
        }

        if (wasSneaking && needsSneak) {
            sneakReleaseTime = System.currentTimeMillis() + 50;
        }
    }

    private BlockData getBlockData(BlockPos pos) {
        EnumFacing[] preferredOrder = {
            EnumFacing.NORTH,
            EnumFacing.SOUTH, 
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.DOWN,
            EnumFacing.UP
        };

        for (EnumFacing face : preferredOrder) {
            BlockPos neighbor = pos.offset(face);
            Block block = mc.theWorld.getBlockState(neighbor).getBlock();

            if (block instanceof BlockAir) continue;
            if (block instanceof BlockLiquid) continue;
            if (invalid.contains(block)) continue;

            if (face != EnumFacing.UP) {
                return new BlockData(neighbor, face.getOpposite());
            }
        }
        return null;
    }

    private BlockData getAlternativeBlockData(BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(face);
            Block block = mc.theWorld.getBlockState(neighbor).getBlock();

            if (block instanceof BlockAir) continue;
            if (block instanceof BlockLiquid) continue;
            if (invalid.contains(block)) continue;

            return new BlockData(neighbor, face.getOpposite());
        }
        return null;
    }

    private boolean isReplaceable(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    private static class BlockData {
        public final BlockPos pos;
        public final EnumFacing face;

        public BlockData(BlockPos pos, EnumFacing face) {
            this.pos = pos;
            this.face = face;
        }
    }
}