package com.zclawm.mod;
import java.awt.Color;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyInputHandler {
    private KeyBinding openGuiKey;
    
    public KeyInputHandler() {
        openGuiKey = new KeyBinding("Abrir GUI zClawM", Keyboard.KEY_RSHIFT, "zClawM Mod");
        ClientRegistry.registerKeyBinding(openGuiKey);
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (zClawMMod.waitingForKeyBind) {
            int keyCode = Keyboard.getEventKey();
            if (keyCode != Keyboard.KEY_NONE && keyCode != Keyboard.KEY_ESCAPE) {
                assignKeyBind(keyCode);
            }
            zClawMMod.waitingForKeyBind = false;
            return;
        }
        
        checkModuleKeys();
        
        if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new zClawMGUI());
        }
    }
    
    private void checkModuleKeys() {
        int keyCode = Keyboard.getEventKey();
        boolean keyState = Keyboard.getEventKeyState();
        
        if (!keyState) return; 
        
        if (keyCode == zClawMMod.keyEsp && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.espPlayerEnabled = !zClawMMod.espPlayerEnabled;
            NotificationManager.showNotification(
                "ESP " + (zClawMMod.espPlayerEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.espPlayerEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyFastPlace && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.fastPlaceEnabled = !zClawMMod.fastPlaceEnabled;
            NotificationManager.showNotification(
                "FastPlace " + (zClawMMod.fastPlaceEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.fastPlaceEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keySprint && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.toggleSprintEnabled = !zClawMMod.toggleSprintEnabled;
            NotificationManager.showNotification(
                "Sprint " + (zClawMMod.toggleSprintEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.toggleSprintEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyTracer && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.tracerEnabled = !zClawMMod.tracerEnabled;
            NotificationManager.showNotification(
                "Tracer " + (zClawMMod.tracerEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.tracerEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keySafeWalk && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.safeWalkEnabled = !zClawMMod.safeWalkEnabled;
            NotificationManager.showNotification(
                "SafeWalk " + (zClawMMod.safeWalkEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.safeWalkEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyScaffold && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.scaffoldEnabled = !zClawMMod.scaffoldEnabled;
            NotificationManager.showNotification(
                "Scaffold " + (zClawMMod.scaffoldEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.scaffoldEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyScaffoldSafe && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.scaffoldSafeEnabled = !zClawMMod.scaffoldSafeEnabled;
            NotificationManager.showNotification(
                "ScaffoldSafe " + (zClawMMod.scaffoldSafeEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.scaffoldSafeEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyAimAssist && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.aimAssistEnabled = !zClawMMod.aimAssistEnabled;
            NotificationManager.showNotification(
                "AimAssist " + (zClawMMod.aimAssistEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.aimAssistEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyAimbot && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.aimbotEnabled = !zClawMMod.aimbotEnabled;
            NotificationManager.showNotification(
                "Aimbot " + (zClawMMod.aimbotEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.aimbotEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
        if (keyCode == zClawMMod.keyReach && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.reachEnabled = !zClawMMod.reachEnabled;
            NotificationManager.showNotification(
                "Reach " + (zClawMMod.reachEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.reachEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }   
        if (keyCode == zClawMMod.keyNametags && keyCode != Keyboard.KEY_NONE) {
            zClawMMod.nametagsEnabled = !zClawMMod.nametagsEnabled;
            NotificationManager.showNotification(
                "NameTags " + (zClawMMod.nametagsEnabled ? "§aAtivado" : "§cDesativado"),
                1500,
                zClawMMod.nametagsEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
            );
        }
    }
    
    private void assignKeyBind(int keyCode) {
        switch (zClawMMod.keyBindToConfigure) {
            case 0: zClawMMod.keyEsp = keyCode; break;
            case 1: zClawMMod.keyFastPlace = keyCode; break;
            case 2: zClawMMod.keySprint = keyCode; break;
            case 3: zClawMMod.keyTracer = keyCode; break;
            case 4: zClawMMod.keySafeWalk = keyCode; break;
            case 5: zClawMMod.keyScaffold = keyCode; break;
            case 6: zClawMMod.keyScaffoldSafe = keyCode; break;
            case 7: zClawMMod.keyAimAssist = keyCode; break;
            case 8: zClawMMod.keyAimbot = keyCode; break;
            case 9: zClawMMod.keyReach = keyCode; break;
            case 10: zClawMMod.keyNametags = keyCode; break;

        }
        zClawMMod.keyBindToConfigure = -1;
    }
}