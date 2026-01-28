package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.EnumChatFormatting;

public class Esp {

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!zClawMMod.espPlayerEnabled) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.thePlayer == null || mc.getRenderManager() == null) return;

        try {
            double px = mc.getRenderManager().viewerPosX;
            double py = mc.getRenderManager().viewerPosY;
            double pz = mc.getRenderManager().viewerPosZ;

            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);

            GL11.glLineWidth(2.0F);

            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player == null || player == mc.thePlayer) continue;
                if (player.isDead) continue;


                String rawName = player.getDisplayName().getFormattedText();
                rawName = EnumChatFormatting.getTextWithoutFormattingCodes(rawName).trim().toLowerCase();
                if (rawName.toLowerCase().startsWith("[bot]")) continue;
                if (rawName.toLowerCase().startsWith("[npc]")) continue;

                double x = player.lastTickPosX +
                        (player.posX - player.lastTickPosX) * event.partialTicks - px;
                double y = player.lastTickPosY +
                        (player.posY - player.lastTickPosY) * event.partialTicks - py;
                double z = player.lastTickPosZ +
                        (player.posZ - player.lastTickPosZ) * event.partialTicks - pz;

                drawBox(x, y, z, player.width, player.height);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        } catch (Exception e) {
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    private void drawBox(double x, double y, double z, float w, float h) {
        GL11.glColor3f(1f, 0f, 0f);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(x - w / 2, y, z - w / 2);
        GL11.glVertex3d(x + w / 2, y, z - w / 2);
        GL11.glVertex3d(x + w / 2, y, z + w / 2);
        GL11.glVertex3d(x - w / 2, y, z + w / 2);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(x - w / 2, y + h, z - w / 2);
        GL11.glVertex3d(x + w / 2, y + h, z - w / 2);
        GL11.glVertex3d(x + w / 2, y + h, z + w / 2);
        GL11.glVertex3d(x - w / 2, y + h, z + w / 2);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x - w / 2, y, z - w / 2);
        GL11.glVertex3d(x - w / 2, y + h, z - w / 2);

        GL11.glVertex3d(x + w / 2, y, z - w / 2);
        GL11.glVertex3d(x + w / 2, y + h, z - w / 2);

        GL11.glVertex3d(x + w / 2, y, z + w / 2);
        GL11.glVertex3d(x + w / 2, y + h, z + w / 2);

        GL11.glVertex3d(x - w / 2, y, z + w / 2);
        GL11.glVertex3d(x - w / 2, y + h, z + w / 2);
        GL11.glEnd();
    }
}
