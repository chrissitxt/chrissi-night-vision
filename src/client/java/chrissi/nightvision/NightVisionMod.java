package chrissi.nightvision;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightVisionMod implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-night-vision";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding toggleKey;
    private static boolean isEnabled = false;
    public static NightVisionConfig config;

    @Override
    public void onInitializeClient() {
        config = NightVisionConfig.load();

        if (config.persistState && config.lastEnabledState) {
            isEnabled = true;
        }

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chrissi-night-vision.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (isEnabled && !client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                NightVisionHandler.setNightVision(client, true);
            }

            if (!isEnabled && client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }

            if (client.player.isDead() && isEnabled && config.resetOnDeath) {
                isEnabled = false;
            }

            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled;
                NightVisionHandler.setNightVision(client, isEnabled);

                if (config.playSound) {
                    client.player.playSound(
                            isEnabled ? SoundEvents.BLOCK_NOTE_BLOCK_PLING.value() : SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(),
                            0.3f,
                            isEnabled ? 2.0f : 0.5f
                    );
                }

                if (config.showToggleMessage) {
                    client.player.sendMessage(
                            Text.literal("Night Vision: ").append(
                                    Text.literal(isEnabled ? "ON" : "OFF")
                                            .styled(style -> style.withColor(isEnabled ? 0x00FF00 : 0xFF0000))
                            ),
                            true
                    );
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            if (config.persistState) {
                config.lastEnabledState = isEnabled;
                config.save();
            }
        });
    }
}