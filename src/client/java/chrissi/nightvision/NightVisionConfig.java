package chrissi.nightvision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NightVisionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("chrissi-night-vision.json");

    public boolean resetOnDeath = false;
    public boolean playSound = false;
    public boolean showStatusEffect = true;
    public boolean showToggleMessage = true;
    public boolean persistState = true;
    public boolean lastEnabledState = false;

    public static NightVisionConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                return GSON.fromJson(Files.readString(CONFIG_PATH), NightVisionConfig.class);
            } catch (Exception e) {
                return createDefault();
            }
        }
        return createDefault();
    }

    private static NightVisionConfig createDefault() {
        NightVisionConfig config = new NightVisionConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            NightVisionMod.LOGGER.error("Failed to save config", e);
        }
    }
}