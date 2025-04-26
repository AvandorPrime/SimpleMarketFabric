package com.avandortools.simplemarket.util;

import com.avandortools.simplemarket.SimpleMarket;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

//TODO: we should probably unit test this class.
public class SimpleMarketConfig {

    private static String defaultResourcePath = "config/simplemarket_default.json";
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("simplemarket.json");
    private static SimpleMarketConfig INSTANCE;
    private final Map<String, Object> loaded_config = new HashMap<>();

    // Load config from the file
    public static void load() {
        try {
            JsonObject defaultConfig = loadJson(SimpleMarket.class.getClassLoader().getResourceAsStream(defaultResourcePath));
            JsonObject userConfig = loadJson(CONFIG_PATH);
            mergeConfigs(defaultConfig, userConfig);

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = new Gson().fromJson(defaultConfig, type);  // Parse the JSON directly into a Map
            INSTANCE = new SimpleMarketConfig();
            INSTANCE.setLoadedConfig(map);  // Set the parsed map into the loaded_config field
        } catch (IOException e) {
            throw new RuntimeException("Failed to read simplemarketmod config", e);
        }
    }

    // Save config to the file
    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(INSTANCE, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save simplemarketmod config", e);
        }
    }

    // Method to get the loaded config (singleton pattern)
    public static SimpleMarketConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    // Set the loaded configuration from a map (used for Gson deserialization)
    public void setLoadedConfig(Map<String, Object> map) {
        this.loaded_config.clear();
        this.loaded_config.putAll(map);
        System.out.println("Loaded following config map: " + map);
    }

    private static JsonObject loadJson(Path path) throws IOException {
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else {
            throw new RuntimeException("Failed to read config file: " + path);
        }
    }

    private static JsonObject loadJson(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }


    private static void mergeConfigs(JsonObject defaultConfig, JsonObject userConfig) {
        // If the user config has keys, override the defaults with them
        for (Map.Entry<String, JsonElement> entry : userConfig.entrySet()) {
            defaultConfig.add(entry.getKey(), entry.getValue());
        }
    }

    public static void copyConfigIfNeeded() {
        // Resource path to the simplemarket.json file within the JAR or resources folder
        String resourcePath = "config/simplemarket.json";
        // Path to the file in the run/config directory
        Path runtimeConfigPath = FabricLoader.getInstance().getConfigDir().resolve( "simplemarket.json");

        // Always copy the config file during development for debugging
        if (!Files.exists(runtimeConfigPath) || true) { //TODO: Remove `true` after debugging
            try (InputStream inputStream = SimpleMarket.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Resource not found: " + resourcePath);
                }
                Files.createDirectories(runtimeConfigPath.getParent()); // Ensure the parent directory exists
                Files.copy(inputStream, runtimeConfigPath, StandardCopyOption.REPLACE_EXISTING); // or only if !exists
                System.out.println("Copied default config to run/config");
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy simplemarket.json: " + e.getMessage(), e);
            }
        }
    }

    public int getInt(String key) {
        Object raw = loaded_config.get(key);
        if (raw instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalStateException("Expected int at key '" + key + "'");
    }

    public String getString(String key) {
        Object raw = loaded_config.get(key);
        if (raw instanceof String str) {
            return str;
        }
        throw new IllegalStateException("Expected String at key '" + key + "'");
    }

    public boolean getBoolean(String key) {
        Object raw = loaded_config.get(key);
        if (raw instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalStateException("Expected boolean at key '" + key + "'");
    }

    // Method to get a ConfigNode for a specific key
    public ConfigNode get(String key) {
        Object value = loaded_config.get(key);
        if (value == null) {
            throw new IllegalStateException("Missing config key: " + key);
        }
        return new ConfigNode(value);
    }

    // Helper class to handle nested maps
    public static class ConfigNode {
        private final Object value;

        public ConfigNode(Object value) {
            this.value = value;
        }

        public int getInt(String key) {
            if (!(value instanceof Map)) {
                throw new IllegalStateException("Expected a map to get key: " + key);
            }
            Object nested = ((Map<?, ?>) value).get(key);
            if (!(nested instanceof Number)) {
                throw new IllegalStateException("Expected a number for key: " + key);
            }
            return ((Number) nested).intValue();
        }

        public String getString(String key) {
            if (!(value instanceof Map)) {
                throw new IllegalStateException("Expected a map to get key: " + key);
            }
            Object nested = ((Map<?, ?>) value).get(key);
            if (!(nested instanceof String)) {
                throw new IllegalStateException("Expected a string for key: " + key);
            }
            return (String) nested;
        }

        public boolean getBoolean(String key) {
            if (!(value instanceof Map)) {
                throw new IllegalStateException("Expected a map to get key: " + key);
            }
            Object nested = ((Map<?, ?>) value).get(key);
            if (!(nested instanceof Boolean)) {
                throw new IllegalStateException("Expected a boolean for key: " + key);
            }
            return (Boolean) nested;
        }
    }
}