package com.zclawm.mod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ToggleSprint {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (!zClawMMod.toggleSprintEnabled)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (mc.gameSettings.keyBindForward.isKeyDown()
                && !mc.thePlayer.isSneaking()
                && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {

            mc.thePlayer.setSprinting(true);
        }
    }
}
