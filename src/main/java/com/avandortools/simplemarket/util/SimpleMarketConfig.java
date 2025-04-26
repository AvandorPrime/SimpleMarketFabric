package com.avandortools.simplemarket.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SimpleMarketConfig {

    private static final Path CONFIG_PATH_DEFAULTS = Path.of("config/simplemarketmod_default.json");
    private static final Path CONFIG_PATH = Path.of("config/simplemarketmod.json");
    private static SimpleMarketConfig INSTANCE;

    private final Map<String, Object> loaded_config = new HashMap<>();

    // Load config from the file
    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> map = gson.fromJson(reader, type);  // Parse the JSON directly into a Map
                INSTANCE = new SimpleMarketConfig();
                INSTANCE.setLoadedConfig(map);  // Set the parsed map into the loaded_config field
            } catch (IOException e) {
                throw new RuntimeException("Failed to read simplemarketmod config", e);
            }
        } else {
            INSTANCE = new SimpleMarketConfig(); // default config
            save(); // write it out so users can edit
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