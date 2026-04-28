package engine.ui;

import engine.util.Logger;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIManager {
    private static UIManager instance;
    
    private Map<String, UIElement> elements;
    private UIElement crosshair;
    private UIElement fpsCounter;
    private UIElement debugInfo;
    private List<UIPanel> panels;
    private boolean initialized = false;
    
    private UIManager() {
        this.elements = new HashMap<>();
        this.panels = new ArrayList<>();
    }
    
    public static UIManager get() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }
    
    public void init() {
        if (initialized) return;
        
        Logger.info("Initializing UI manager...");
        
        crosshair = new UIElement(new Vector2f(636, 356), new Vector2f(8, 8));
        crosshair.setColor(1, 1, 1, 1);
        elements.put("crosshair", crosshair);
        
        debugInfo = new UIElement(new Vector2f(10, 10), new Vector2f(200, 100));
        debugInfo.setColor(0, 0, 0, 0.5f);
        elements.put("debug", debugInfo);
        
        initialized = true;
        Logger.info("UI manager initialized");
    }
    
    public void addElement(String name, UIElement element) {
        elements.put(name, element);
    }
    
    public void removeElement(String name) {
        elements.remove(name);
    }
    
    public UIElement getElement(String name) {
        return elements.get(name);
    }
    
    public void addPanel(UIPanel panel) {
        panels.add(panel);
    }
    
    public void removePanel(UIPanel panel) {
        panels.remove(panel);
    }
    
    public List<UIPanel> getPanels() {
        return panels;
    }
    
    public UIElement getCrosshair() {
        return crosshair;
    }
    
    public void update(float deltaTime) {
    }
    
    public void render(engine.shader.ShaderProgram shader) {
        for (UIElement element : elements.values()) {
            element.render(shader);
        }
        
        for (UIPanel panel : panels) {
            panel.render(shader);
        }
    }
    
    public void cleanup() {
        for (UIElement element : elements.values()) {
            element.cleanup();
        }
        elements.clear();
        panels.clear();
        initialized = false;
    }
}