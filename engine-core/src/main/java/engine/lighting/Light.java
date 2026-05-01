package engine.lighting;

import org.joml.Vector3f;

public class Light {
    private Vector3f position;
    private Vector3f color;
    private Vector3f ambientColor;
    private float intensity;
    private boolean enabled;
    
    public Light() {
        this.position = new Vector3f(0, 10, 0);
        this.color = new Vector3f(1, 1, 1);
        this.ambientColor = new Vector3f(0.2f, 0.2f, 0.2f);
        this.intensity = 1.0f;
        this.enabled = true;
    }
    
    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
        this.ambientColor = new Vector3f(0.2f, 0.2f, 0.2f);
        this.intensity = 1.0f;
        this.enabled = true;
    }
    
    public Light(Vector3f position, Vector3f color, Vector3f ambientColor, float intensity) {
        this.position = position;
        this.color = color;
        this.ambientColor = ambientColor;
        this.intensity = intensity;
        this.enabled = true;
    }
    
    public void update(float deltaTime) {
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    public Vector3f getColor() {
        return color;
    }
    
    public void setColor(Vector3f color) {
        this.color = color;
    }
    
    public Vector3f getAmbientColor() {
        return ambientColor;
    }
    
    public void setAmbientColor(Vector3f ambientColor) {
        this.ambientColor = ambientColor;
    }
    
    public float getIntensity() {
        return intensity;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public static Light createSunLight() {
        Light sun = new Light();
        sun.setPosition(new Vector3f(-1, 1, -1));
        sun.setColor(new Vector3f(1.0f, 0.98f, 0.9f));
        sun.setAmbientColor(new Vector3f(0.3f, 0.3f, 0.35f));
        sun.setIntensity(1.0f);
        return sun;
    }
}