package com.zclawm.mod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;

@Mod(modid = zClawMMod.MODID, version = zClawMMod.VERSION, name = "zClawM Mod", clientSideOnly = true)
public class zClawMMod {
    public static boolean espPlayerEnabled = false;
    public static boolean fastPlaceEnabled = false;
    public static boolean toggleSprintEnabled = false;
    public static boolean tracerEnabled = false;
    public static boolean safeWalkEnabled = false;
    public static boolean scaffoldEnabled = false;
    public static boolean scaffoldSafeEnabled = false; 
    public static boolean aimAssistEnabled = false;
    public static boolean aimbotEnabled = false;
    public static boolean reachEnabled = false;
    public static boolean nametagsEnabled = false; 

    public static int keyEsp = Keyboard.KEY_NONE;
    public static int keyFastPlace = Keyboard.KEY_NONE;
    public static int keySprint = Keyboard.KEY_NONE;
    public static int keyTracer = Keyboard.KEY_NONE;
    public static int keySafeWalk = Keyboard.KEY_NONE;
    public static int keyScaffold = Keyboard.KEY_NONE;
    public static int keyScaffoldSafe = Keyboard.KEY_NONE; 
    public static int keyAimAssist = Keyboard.KEY_NONE;
    public static int keyAimbot = Keyboard.KEY_NONE;
    public static int keyReach = Keyboard.KEY_NONE;
    public static int keyNametags = Keyboard.KEY_NONE; 

    public static boolean viewBobbingDisabled = true; 
    public static boolean waitingForKeyBind = false;
    public static int keyBindToConfigure = -1; 
    public static final String MODID = "zclawmmod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        }
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new Esp());
            MinecraftForge.EVENT_BUS.register(new FastPlace());
            MinecraftForge.EVENT_BUS.register(new ToggleSprint());
            MinecraftForge.EVENT_BUS.register(new Tracer());
            MinecraftForge.EVENT_BUS.register(new SafeWalk());
            MinecraftForge.EVENT_BUS.register(new Scaffold());
            MinecraftForge.EVENT_BUS.register(new ScaffoldSafe());

            MinecraftForge.EVENT_BUS.register(new AimAssist());
            MinecraftForge.EVENT_BUS.register(new Aimbot());
            MinecraftForge.EVENT_BUS.register(new NotificationManager());
            MinecraftForge.EVENT_BUS.register(new Reach());
            MinecraftForge.EVENT_BUS.register(new NameTags());

            disableViewBobbing();
        }
    }
    
    public static void notifyModuleChange(String moduleName, boolean enabled) {
        if (Minecraft.getMinecraft().theWorld != null) {
            String status = enabled ? "§aAtivado" : "§cDesativado";
            NotificationManager.showNotification(moduleName + " " + status, 1500);
        }
    }
    
    public static void disableViewBobbing() {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
            if (mc != null && mc.gameSettings != null) {
                mc.gameSettings.viewBobbing = false;
                viewBobbingDisabled = true;
            }
        } catch (Exception e) {
        }
    }
    
    public static void enableViewBobbing() {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
            if (mc != null && mc.gameSettings != null) {
                mc.gameSettings.viewBobbing = true;
                viewBobbingDisabled = false;
            }
        } catch (Exception e) {
        }
    }
    
    public static void toggleViewBobbing() {
        if (viewBobbingDisabled) {
            enableViewBobbing();
        } else {
            disableViewBobbing();
        }
    }
    
    public static String getKeyName(int keyCode) {
        if (keyCode == Keyboard.KEY_NONE) {
            return "NONE";
        }
        return Keyboard.getKeyName(keyCode);
    }
}