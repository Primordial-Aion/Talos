package engine.core;

import engine.util.Logger;
import engine.util.Config;
import engine.input.Input;
import org.lwjgl.glfw.GLFW;

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
        if (running) return;
        Logger.info("Initializing engine...");
        window.init();
        lastFrameTime = System.currentTimeMillis();
        fpsTime = System.currentTimeMillis();
        running = true;
        Logger.info("Engine initialized successfully");
    }
    
    public void run() {
        if (!running) init();
        
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
            
            // CRITICAL: Poll events FIRST to get mouse/keyboard input
            GLFW.glfwPollEvents();
            
            // Now read input and update game logic
            update(deltaTime);
            render();
            
            // Swap buffers AFTER rendering
            GLFW.glfwSwapBuffers(window.getWindowHandle());
            
            // Reset input state for NEXT frame (after we've read it)
            Input.update();
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
        engine.scene.Scene.get().cleanup();
        engine.ui.UIManager.get().cleanup();
        engine.render.Renderer.get().cleanup();
        engine.shader.ShaderProgram.cleanupAll();
        engine.audio.AudioManager.get().cleanup();
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