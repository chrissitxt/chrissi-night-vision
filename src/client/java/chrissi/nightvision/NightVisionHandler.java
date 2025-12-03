package chrissi.nightvision;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class NightVisionHandler {
    public static void setNightVision(MinecraftClient client, boolean enabled) {
        if (client.player == null) return;

        if (enabled) {
            boolean show = NightVisionMod.config.showStatusEffect;
            client.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION,
                    -1,
                    0,
                    false,
                    show,
                    show
            ));
        } else {
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}