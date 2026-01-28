package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

public class FastPlace {

    private static final String[] FIELD_NAMES = {
            "rightClickDelayTimer", 
            "field_71467_ac"      
    };

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (!zClawMMod.fastPlaceEnabled)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (Mouse.isButtonDown(1)) {
            try {
                ReflectionHelper.setPrivateValue(
                        Minecraft.class,
                        mc,
                        0,
                        FIELD_NAMES
                );
            } catch (Exception e) {
            }
        }
    }
}
