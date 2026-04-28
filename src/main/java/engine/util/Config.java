package engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
    private static Config instance;
    private final Gson gson;
    
    public int windowWidth = Constants.DEFAULT_WINDOW_WIDTH;
    public int windowHeight = Constants.DEFAULT_WINDOW_HEIGHT;
    public String windowTitle = Constants.WINDOW_TITLE;
    public boolean enableVsync = true;
    public boolean enableDebug = true;
    public float moveSpeed = Constants.DEFAULT_MOVE_SPEED;
    public float mouseSensitivity = Constants.DEFAULT_MOUSE_SENSITIVITY;
    
    private Config() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public static Config get() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
    
    public void save(String path) {
        String json = gson.toJson(this);
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(path), json.getBytes());
        } catch (java.io.IOException e) {
            Logger.warn("Could not save config: " + e.getMessage());
        }
    }
    
    public void load(String path) {
        try {
            String json = java.nio.file.Files.readString(java.nio.file.Paths.get(path));
            Config loaded = gson.fromJson(json, Config.class);
            if (loaded != null) {
                this.windowWidth = loaded.windowWidth;
                this.windowHeight = loaded.windowHeight;
                this.windowTitle = loaded.windowTitle;
                this.enableVsync = loaded.enableVsync;
                this.enableDebug = loaded.enableDebug;
                this.moveSpeed = loaded.moveSpeed;
                this.mouseSensitivity = loaded.mouseSensitivity;
            }
        } catch (Exception e) {
            Logger.warn("Could not load config: " + e.getMessage());
        }
    }
    
    public Gson getGson() {
        return gson;
    }
}