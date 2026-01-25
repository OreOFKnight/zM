package com.zclawm.mod;

import com.zclawm.mod.zClawMMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.io.IOException;

public class zClawMGUI extends GuiScreen {

    private static final ResourceLocation PROFILE =
            new ResourceLocation("zclawmmod:textures/gui/profile.png");

    @Override
    public void initGui() {
        this.buttonList.clear();

        int centerX = this.width / 2;

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 5;

        int startY = 120; 

        this.buttonList.add(new GuiButton(0, centerX - buttonWidth / 2, startY + 0 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("ESP", zClawMMod.espPlayerEnabled, zClawMMod.keyEsp)));

        this.buttonList.add(new GuiButton(1, centerX - buttonWidth / 2, startY + 1 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("FastPlace", zClawMMod.fastPlaceEnabled, zClawMMod.keyFastPlace)));

        this.buttonList.add(new GuiButton(2, centerX - buttonWidth / 2, startY + 2 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("Sprint", zClawMMod.toggleSprintEnabled, zClawMMod.keySprint)));

        this.buttonList.add(new GuiButton(3, centerX - buttonWidth / 2, startY + 3 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("Tracers", zClawMMod.tracerEnabled, zClawMMod.keyTracer)));

        this.buttonList.add(new GuiButton(4, centerX - buttonWidth / 2, startY + 4 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("SafeWalk", zClawMMod.safeWalkEnabled, zClawMMod.keySafeWalk)));

        this.buttonList.add(new GuiButton(5, centerX - buttonWidth / 2, startY + 5 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("Scaffold", zClawMMod.scaffoldEnabled, zClawMMod.keyScaffold)));

        this.buttonList.add(new GuiButton(6, centerX - buttonWidth / 2, startY + 6 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("AimAssist", zClawMMod.aimAssistEnabled, zClawMMod.keyAimAssist)));

        this.buttonList.add(new GuiButton(7, centerX - buttonWidth / 2, startY + 7 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("Aimbot", zClawMMod.aimbotEnabled, zClawMMod.keyAimbot)));

        this.buttonList.add(new GuiButton(8, centerX - buttonWidth / 2, startY + 8 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, getButtonText("Reach", zClawMMod.reachEnabled, zClawMMod.keyReach)));

        this.buttonList.add(new GuiButton(9, centerX - buttonWidth / 2, startY + 9 * (buttonHeight + spacing),
                buttonWidth, buttonHeight, "Limpar KeyBinds"));
    }

    private String getButtonText(String name, boolean enabled, int key) {
        String keyName = key == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(key);
        return name + ": " + (enabled ? "ON" : "OFF") + " [" + keyName + "]";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;

        Minecraft.getMinecraft().getTextureManager().bindTexture(PROFILE);
        Gui.drawModalRectWithCustomSizedTexture(
                centerX - 32,
                20,
                0, 0,
                64, 64,
                64, 64
        );

        this.fontRendererObj.drawString(
                "zClawM",
                centerX - this.fontRendererObj.getStringWidth("zClawM") / 2,
                90,
                Color.WHITE.getRGB()
        );

        this.fontRendererObj.drawString(
                "Clique esquerdo: Ligar/Desligar",
                10,
                this.height - 30,
                Color.LIGHT_GRAY.getRGB()
        );
        this.fontRendererObj.drawString(
                "Clique direito: Configurar tecla",
                10,
                this.height - 20,
                Color.LIGHT_GRAY.getRGB()
        );

        if (zClawMMod.waitingForKeyBind) {
            this.fontRendererObj.drawString(
                    "Pressione uma tecla... (ESC para cancelar)",
                    centerX - 100,
                    this.height - 45,
                    Color.YELLOW.getRGB()
            );
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);

            for (int i = 0; i < this.buttonList.size(); i++) {
                GuiButton button = this.buttonList.get(i);

                if (button.isMouseOver()) {
                    if (mouseButton == 0) handleLeftClick(button, i);
                    if (mouseButton == 1) handleRightClick(button, i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleLeftClick(GuiButton button, int id) {
        if (id == 9) {
            clearAllKeyBinds();
            updateButtonTexts();
            NotificationManager.showNotification("§eTodas as teclas foram removidas", 2000, Color.YELLOW.getRGB());
            return;
        }

        String moduleName = "";
        
        switch (id) {
            case 0: 
                zClawMMod.espPlayerEnabled = !zClawMMod.espPlayerEnabled;
                moduleName = "ESP";
                break;
            case 1: 
                zClawMMod.fastPlaceEnabled = !zClawMMod.fastPlaceEnabled;
                moduleName = "FastPlace";
                break;
            case 2: 
                zClawMMod.toggleSprintEnabled = !zClawMMod.toggleSprintEnabled;
                moduleName = "Sprint";
                break;
            case 3: 
                zClawMMod.tracerEnabled = !zClawMMod.tracerEnabled;
                moduleName = "Tracer";
                break;
            case 4: 
                zClawMMod.safeWalkEnabled = !zClawMMod.safeWalkEnabled;
                moduleName = "SafeWalk";
                break;
            case 5: 
                zClawMMod.scaffoldEnabled = !zClawMMod.scaffoldEnabled;
                moduleName = "Scaffold";
                break;
            case 6: 
                zClawMMod.aimAssistEnabled = !zClawMMod.aimAssistEnabled;
                moduleName = "AimAssist";
                break;
            case 7: 
                zClawMMod.aimbotEnabled = !zClawMMod.aimbotEnabled;
                moduleName = "Aimbot";
                break;
            case 8: 
                zClawMMod.reachEnabled = !zClawMMod.reachEnabled;
                moduleName = "Reach";
                break;
        }
        
        boolean isEnabled = false;
        switch (id) {
            case 0: isEnabled = zClawMMod.espPlayerEnabled; break;
            case 1: isEnabled = zClawMMod.fastPlaceEnabled; break;
            case 2: isEnabled = zClawMMod.toggleSprintEnabled; break;
            case 3: isEnabled = zClawMMod.tracerEnabled; break;
            case 4: isEnabled = zClawMMod.safeWalkEnabled; break;
            case 5: isEnabled = zClawMMod.scaffoldEnabled; break;
            case 6: isEnabled = zClawMMod.aimAssistEnabled; break;
            case 7: isEnabled = zClawMMod.aimbotEnabled; break;
            case 8: isEnabled = zClawMMod.reachEnabled; break;
        }
        
        NotificationManager.showNotification(
            moduleName + " " + (isEnabled ? "§aAtivado" : "§cDesativado"),
            1500,
            isEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB()
        );
        
        updateButtonText(button, id);
    }

    private void handleRightClick(GuiButton button, int id) {
        if (id == 8) return;
        
        String moduleName = "";
        switch (id) {
            case 0: moduleName = "ESP"; break;
            case 1: moduleName = "FastPlace"; break;
            case 2: moduleName = "Sprint"; break;
            case 3: moduleName = "Tracer"; break;
            case 4: moduleName = "SafeWalk"; break;
            case 5: moduleName = "Scaffold"; break;
            case 6: moduleName = "AimAssist"; break;
            case 7: moduleName = "Aimbot"; break;
            case 8: moduleName = "Reach";
        }
        
        zClawMMod.waitingForKeyBind = true;
        zClawMMod.keyBindToConfigure = id;
        button.displayString = "Pressione uma tecla...";
        
        NotificationManager.showNotification("§eConfigure a tecla para " + moduleName, 2000, Color.YELLOW.getRGB());
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (zClawMMod.waitingForKeyBind) {
            if (key != Keyboard.KEY_ESCAPE) {
                switch (zClawMMod.keyBindToConfigure) {
                    case 0: zClawMMod.keyEsp = key; break;
                    case 1: zClawMMod.keyFastPlace = key; break;
                    case 2: zClawMMod.keySprint = key; break;
                    case 3: zClawMMod.keyTracer = key; break;
                    case 4: zClawMMod.keySafeWalk = key; break;
                    case 5: zClawMMod.keyScaffold = key; break;
                    case 6: zClawMMod.keyAimAssist = key; break;
                    case 7: zClawMMod.keyAimbot = key; break;
                    case 8: zClawMMod.keyReach = key; break;
                }
            }
            zClawMMod.waitingForKeyBind = false;
            updateButtonTexts();
        } else {
            super.keyTyped(c, key);
        }
    }

    private void updateButtonText(GuiButton button, int id) {
        switch (id) {
            case 0: button.displayString = getButtonText("ESP", zClawMMod.espPlayerEnabled, zClawMMod.keyEsp); break;
            case 1: button.displayString = getButtonText("FastPlace", zClawMMod.fastPlaceEnabled, zClawMMod.keyFastPlace); break;
            case 2: button.displayString = getButtonText("Sprint", zClawMMod.toggleSprintEnabled, zClawMMod.keySprint); break;
            case 3: button.displayString = getButtonText("Tracers", zClawMMod.tracerEnabled, zClawMMod.keyTracer); break;
            case 4: button.displayString = getButtonText("SafeWalk", zClawMMod.safeWalkEnabled, zClawMMod.keySafeWalk); break;
            case 5: button.displayString = getButtonText("Scaffold", zClawMMod.scaffoldEnabled, zClawMMod.keyScaffold); break;
            case 6: button.displayString = getButtonText("AimAssist", zClawMMod.aimAssistEnabled, zClawMMod.keyAimAssist); break;
            case 7: button.displayString = getButtonText("Aimbot", zClawMMod.aimbotEnabled, zClawMMod.keyAimbot); break;
            case 8: button.displayString = getButtonText("Reach", zClawMMod.reachEnabled, zClawMMod.keyReach); break;
            case 9: button.displayString = "Limpar KeyBinds"; break;
        }
    }

    private void updateButtonTexts() {
        for (int i = 0; i < this.buttonList.size(); i++) {
            updateButtonText(this.buttonList.get(i), i);
        }
    }
    private void clearAllKeyBinds() {
        zClawMMod.keyEsp = Keyboard.KEY_NONE;
        zClawMMod.keyFastPlace = Keyboard.KEY_NONE;
        zClawMMod.keySprint = Keyboard.KEY_NONE;
        zClawMMod.keyTracer = Keyboard.KEY_NONE;
        zClawMMod.keySafeWalk = Keyboard.KEY_NONE;
        zClawMMod.keyScaffold = Keyboard.KEY_NONE;
        zClawMMod.keyAimAssist = Keyboard.KEY_NONE;
        zClawMMod.keyAimbot = Keyboard.KEY_NONE;
        zClawMMod.keyReach = Keyboard.KEY_NONE;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
