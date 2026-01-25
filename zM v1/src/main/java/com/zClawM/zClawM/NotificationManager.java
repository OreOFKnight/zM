package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import net.minecraft.client.gui.Gui;

public class NotificationManager {
    
    private static final List<Notification> notifications = new ArrayList<Notification>();

    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public static class Notification {
        private final String message;
        private final long creationTime;
        private final long duration;
        private final int color;
        
        public Notification(String message, long duration, int color) {
            this.message = message;
            this.creationTime = System.currentTimeMillis();
            this.duration = duration;
            this.color = color;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > creationTime + duration;
        }
        
        public String getMessage() { return message; }
        public int getColor() { return color; }
        public float getProgress() {
            float elapsed = System.currentTimeMillis() - creationTime;
            return Math.min(elapsed / (float)duration, 1.0f);
        }
    }
    
    public static void showNotification(String message) {
        showNotification(message, 2000);
    }
    
    public static void showNotification(String message, long duration) {
        showNotification(message, duration, Color.WHITE.getRGB());
    }
    
    public static void showNotification(String message, long duration, int color) {
        notifications.add(new Notification(message, duration, color));
    }
    
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.gameSettings.showDebugInfo) return;
        
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();
        
        int margin = 5;
        int notificationHeight = 20;
        int notificationWidth = 200;
        int x = screenWidth - notificationWidth - margin;
        int y = screenHeight - margin - notificationHeight;
        
        for (int i = notifications.size() - 1; i >= 0; i--) {
            if (notifications.get(i).isExpired()) {
                notifications.remove(i);
            }
        }

        for (int i = 0; i < Math.min(notifications.size(), 5); i++) {
            Notification notif = notifications.get(i);
            
            int currentY = y - (i * (notificationHeight + margin));
            
            drawRect(x, currentY, x + notificationWidth, currentY + notificationHeight, 0x80000000);
            
            float progress = notif.getProgress();
            int progressWidth = (int)(notificationWidth * (1.0f - progress));
            drawRect(x, currentY + notificationHeight - 2, x + progressWidth, currentY + notificationHeight, 0x80FFFFFF);
            
            String text = notif.getMessage();
            int textWidth = fr.getStringWidth(text);
            int textX = x + (notificationWidth - textWidth) / 2;
            int textY = currentY + (notificationHeight - 8) / 2;
            
            fr.drawString(text, textX, textY, notif.getColor());
        }
    }
    
    private void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}