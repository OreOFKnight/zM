package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Tracer {

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        if (!zClawMMod.tracerEnabled)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.thePlayer == null || mc.getRenderManager() == null)
            return;

        double px = mc.getRenderManager().viewerPosX;
        double py = mc.getRenderManager().viewerPosY;
        double pz = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GL11.glLineWidth(1.5F);

        for (EntityPlayer player : mc.theWorld.playerEntities) {

            if (player == null || player == mc.thePlayer)
                continue;

            double x = player.lastTickPosX +
                    (player.posX - player.lastTickPosX) * event.partialTicks - px;
            double y = player.lastTickPosY +
                    (player.posY - player.lastTickPosY) * event.partialTicks - py;
            double z = player.lastTickPosZ +
                    (player.posZ - player.lastTickPosZ) * event.partialTicks - pz;

            drawLine(0, mc.thePlayer.getEyeHeight(), 0, x, y, z);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawLine(double x1, double y1, double z1,
                          double x2, double y2, double z2) {

        GL11.glBegin(GL11.GL_LINES);

        GL11.glColor3f(1f, 0f, 0f);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2 + 1.6, z2);

        GL11.glEnd();
    }
}