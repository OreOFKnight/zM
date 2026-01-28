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

    // Blocos que não podemos colocar em cima
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

        ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return;
        if (System.currentTimeMillis() - lastPlace < 90) return;

        BlockPos targetPos = getBlockBelow();
        if (!isReplaceable(targetPos)) {
            targetPos = getBlockAround(); // tenta colocar ao redor
            if (targetPos == null) return;
        }

        BlockData data = getBlockData(targetPos);
        if (data == null) return;

        place(stack, data);
        lastPlace = System.currentTimeMillis();
    }

    private BlockPos getBlockBelow() {
        return new BlockPos(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 1,
                mc.thePlayer.posZ
        );
    }

    private BlockPos getBlockAround() {
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos neighbor = getBlockBelow().offset(face);
            if (isReplaceable(neighbor)) continue; // só pega blocos sólidos próximos
            return neighbor;
        }
        return null;
    }

    private void place(ItemStack stack, BlockData data) {
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

        if (success) mc.thePlayer.swingItem();
    }

    private BlockData getBlockData(BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(face);
            Block block = mc.theWorld.getBlockState(neighbor).getBlock();

            if (block instanceof BlockAir || block instanceof BlockLiquid) continue;
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
