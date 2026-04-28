package engine.debug;

import engine.render.Renderer;
import engine.util.Logger;
import engine.input.Input;

public class DebugOverlay {
    private static DebugOverlay instance;
    
    private boolean enabled = true;
    private boolean showWireframe = false;
    private boolean showCollisionBoxes = false;
    private boolean showFPS = true;
    private boolean showPosition = true;
    private int fps = 0;
    private int frames = 0;
    private long lastTime = 0;
    private StringBuilder text;
    
    private DebugOverlay() {
        this.text = new StringBuilder();
        this.lastTime = System.currentTimeMillis();
    }
    
    public static DebugOverlay get() {
        if (instance == null) {
            instance = new DebugOverlay();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing debug overlay...");
        Logger.info("Debug overlay initialized");
    }
    
    public void update(int fps, float playerX, float playerY, float playerZ) {
        this.fps = fps;
        
        long currentTime = System.currentTimeMillis();
        frames++;
        
        if (currentTime - lastTime >= 1000) {
            fps = frames;
            frames = 0;
            lastTime = currentTime;
        }
        
        if (Input.isKeyPressed(Input.Keys.F3)) {
            enabled = !enabled;
        }
        
        if (Input.isKeyPressed(Input.Keys.F4)) {
            showWireframe = !showWireframe;
            Renderer.get().setWireframeMode(showWireframe);
        }
        
        text.setLength(0);
        
        if (enabled) {
            text.append("=== DEBUG ===\n");
            text.append("F3 - Toggle Debug\n");
            text.append("F4 - Toggle Wireframe\n");
            text.append("\n");
            
            if (showFPS) {
                text.append("FPS: ").append(fps).append("\n");
            }
            
            if (showPosition) {
                text.append("Position: ")
                    .append(String.format("%.1f", playerX))
                    .append(", ")
                    .append(String.format("%.1f", playerY))
                    .append(", ")
                    .append(String.format("%.1f", playerZ))
                    .append("\n");
            }
        }
    }
    
    public String getText() {
        return text.toString();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isShowWireframe() {
        return showWireframe;
    }
    
    public boolean isShowCollisionBoxes() {
        return showCollisionBoxes;
    }
    
    public void setShowCollisionBoxes(boolean show) {
        this.showCollisionBoxes = show;
    }
    
    public boolean isShowFPS() {
        return showFPS;
    }
    
    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }
    
    public boolean isShowPosition() {
        return showPosition;
    }
    
    public void setShowPosition(boolean showPosition) {
        this.showPosition = showPosition;
    }
}