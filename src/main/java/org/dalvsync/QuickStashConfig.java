package org.dalvsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QuickStashConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickstash.json");

    public int radius = 8;

    private static QuickStashConfig instance;

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                instance = GSON.fromJson(reader, QuickStashConfig.class);
            } catch (IOException e) {
                System.err.println("[QuickStash] Failed to load config, using default settings.");
                instance = new QuickStashConfig();
            }
        } else {
            instance = new QuickStashConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            System.err.println("[QuickStash] Failed to save config!");
        }
    }

    public static QuickStashConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }
}