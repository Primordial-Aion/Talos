package engine.ui;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIPanel {
    private String title;
    private Vector2f position;
    private Vector2f size;
    private Vector4f backgroundColor;
    private Vector4f borderColor;
    private boolean visible;
    private List<UIElement> children;
    
    public UIPanel() {
        this.position = new Vector2f(100, 100);
        this.size = new Vector2f(300, 200);
        this.backgroundColor = new Vector4f(0.2f, 0.2f, 0.2f, 0.9f);
        this.borderColor = new Vector4f(0.5f, 0.5f, 0.5f, 1);
        this.visible = true;
        this.children = new ArrayList<>();
    }
    
    public UIPanel(String title, Vector2f position, Vector2f size) {
        this();
        this.title = title;
        this.position = position;
        this.size = size;
    }
    
    public void addElement(UIElement element) {
        children.add(element);
    }
    
    public void removeElement(UIElement element) {
        children.remove(element);
    }
    
    public List<UIElement> getElements() {
        return children;
    }
    
    public void render(engine.shader.ShaderProgram shader) {
        if (!visible) return;
        
        for (UIElement element : children) {
            element.setColor(backgroundColor);
            element.render(shader);
        }
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setPosition(Vector2f position) {
        this.position = position;
    }
    
    public Vector2f getPosition() {
        return position;
    }
    
    public void setSize(Vector2f size) {
        this.size = size;
    }
    
    public Vector2f getSize() {
        return size;
    }
    
    public void setBackgroundColor(Vector4f color) {
        this.backgroundColor = color;
    }
    
    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBorderColor(Vector4f color) {
        this.borderColor = color;
    }
    
    public Vector4f getBorderColor() {
        return borderColor;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
}