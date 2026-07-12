package org.larrydev.gridlink.config;

import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static Config instance;

    private final int width;
    private final int height;
    private final int visibility;
    private final int maxUnits;
    private final String obstacleMode;

    private Config(int width, int height, int visibility, int maxUnits, String obstacleMode) {
        this.width = width;
        this.height = height;
        this.visibility = visibility;
        this.maxUnits = maxUnits;
        this.obstacleMode = obstacleMode;
    }

    public static void load(String resourceName) {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                // Try classpath prefixing with slash
                try (InputStream slashInput = Config.class.getResourceAsStream("/" + resourceName)) {
                    if (slashInput != null) {
                        loadFromStream(slashInput);
                        return;
                    }
                }
                // Try from local folder or absolute path if resource stream is null
                try (InputStream localInput = new java.io.FileInputStream(resourceName)) {
                    loadFromStream(localInput);
                    return;
                } catch (Exception ex) {
                    throw new RuntimeException("Sorry, unable to find " + resourceName, ex);
                }
            }
            loadFromStream(input);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading configuration from " + resourceName, ex);
        }
    }

    private static void loadFromStream(InputStream input) throws Exception {
        Properties prop = new Properties();
        prop.load(input);
        int width = Integer.parseInt(prop.getProperty("WIDTH", "0").trim());
        int height = Integer.parseInt(prop.getProperty("HEIGHT", "0").trim());
        int visibility = Integer.parseInt(prop.getProperty("VISIBILITY", "0").trim());
        int maxUnits = Integer.parseInt(prop.getProperty("MAX_UNITS", "0").trim());
        String obstacleMode = prop.getProperty("OBSTACLE_MODE", "").trim();

        instance = new Config(width, height, visibility, maxUnits, obstacleMode);
    }

    public static Config getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Config has not been loaded. Call Config.load() first.");
        }
        return instance;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getVisibility() {
        return visibility;
    }

    public int getMaxUnits() {
        return maxUnits;
    }

    public String getObstacleMode() {
        return obstacleMode;
    }

    public int getHalfWidth() {
        return width / 2;
    }

    public int getHalfHeight() {
        return height / 2;
    }
}
