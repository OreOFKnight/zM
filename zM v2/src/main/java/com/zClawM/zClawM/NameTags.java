package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.EnumChatFormatting;

import java.awt.Color;

public class NameTags {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!zClawMMod.nametagsEnabled || mc.theWorld == null || mc.thePlayer == null) return;

        for (Object entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                EntityPlayer player = (EntityPlayer) entity;

                if (player.isDead || player.getHealth() <= 0) continue;

                String rawName = player.getDisplayName().getFormattedText();
                rawName = EnumChatFormatting.getTextWithoutFormattingCodes(rawName).trim().toLowerCase();
                if (rawName.toLowerCase().startsWith("[bot]")) continue;
                if (rawName.toLowerCase().startsWith("[npc]")) continue;

                double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
                double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
                double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

                renderNametag(player, x, y, z);
            }
        }
    }

    private void renderNametag(EntityPlayer player, double x, double y, double z) {
        double distance = mc.thePlayer.getDistanceToEntity(player);

        String name = player.getDisplayName().getFormattedText();

        double scale = distance / 4.0;

        if (scale < 1.0) scale = 1.0;
        if (scale > 30.0) scale = 30.0;

        GlStateManager.pushMatrix();
        GlStateManager.translate(
                (float) (x - mc.getRenderManager().viewerPosX),
                (float) (y - mc.getRenderManager().viewerPosY + player.height + 0.6),
                (float) (z - mc.getRenderManager().viewerPosZ)
        );

        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        float base = 0.02F;
        GlStateManager.scale(-base * scale, -base * scale, base * scale);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        FontRenderer fontRenderer = mc.fontRendererObj;
        int nameWidth = fontRenderer.getStringWidth(name);

        fontRenderer.drawStringWithShadow(name, -nameWidth / 2, -10, Color.WHITE.getRGB());

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private int getPing(EntityPlayer player) {
        try {
            if (mc.isSingleplayer()) return 0;

            if (mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(player.getUniqueID()) != null) {
                return mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime();
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(r, g, b, a);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(left, bottom);
        GL11.glVertex2f(right, bottom);
        GL11.glVertex2f(right, top);
        GL11.glVertex2f(left, top);
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
    }
}
