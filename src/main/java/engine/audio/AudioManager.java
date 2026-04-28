package engine.audio;

import engine.util.Logger;

public class AudioManager {
    private static AudioManager instance;
    private boolean initialized = false;
    
    private AudioManager() {}
    
    public static AudioManager get() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    public void init() {
        if (initialized) return;
        Logger.info("Audio placeholder initialized");
        initialized = true;
    }
    
    public void playSound(String name) {}
    public void playMusic(String name) {}
    public void stopMusic() {}
    public void cleanup() {}
    public boolean isInitialized() { return initialized; }
}