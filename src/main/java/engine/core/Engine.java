package engine.core;

import engine.util.Logger;
import engine.util.Config;
import engine.input.Input;

public class Engine {
    private static Engine instance;
    
    private final Window window;
    private final Config config;
    
    private long lastFrameTime;
    private float deltaTime;
    private int fps;
    private int frameCount;
    private long fpsTime;
    
    private boolean running = false;
    
    private Engine() {
        this.window = Window.get();
        this.config = Config.get();
    }
    
    protected Engine(Window window, Config config) {
        this.window = window;
        this.config = config;
    }
    
    public static Engine get() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing engine...");
        window.init();
        lastFrameTime = System.currentTimeMillis();
        fpsTime = System.currentTimeMillis();
        running = true;
        Logger.info("Engine initialized successfully");
    }
    
    public void run() {
        init();
        
        while (!window.shouldClose() && running) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (currentTime - lastFrameTime) / 1000.0f;
            lastFrameTime = currentTime;
            
            frameCount++;
            if (currentTime - fpsTime >= 1000) {
                fps = frameCount;
                frameCount = 0;
                fpsTime = currentTime;
            }
            
            Input.update();
            
            update(deltaTime);
            render();
            
            window.update();
        }
        
        cleanup();
    }
    
    public void stop() {
        running = false;
    }
    
    protected void update(float deltaTime) {
    }
    
    protected void render() {
    }
    
    private void cleanup() {
        Logger.info("Cleaning up engine...");
        window.cleanup();
        Logger.info("Engine shutdown complete");
    }
    
    public Window getWindow() {
        return window;
    }
    
    public float getDeltaTime() {
        return deltaTime;
    }
    
    public int getFps() {
        return fps;
    }
}