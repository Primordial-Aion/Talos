package engine.lighting;

import engine.util.Logger;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private static LightManager instance;
    
    private List<Light> lights;
    private Light sunLight;
    
    private LightManager() {
        this.lights = new ArrayList<>();
    }
    
    public static LightManager get() {
        if (instance == null) {
            instance = new LightManager();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing lighting...");
        sunLight = Light.createSunLight();
        addLight(sunLight);
        Logger.info("Lighting initialized");
    }
    
    public void addLight(Light light) {
        lights.add(light);
        Logger.debug("Light added: " + lights.size());
    }
    
    public void removeLight(Light light) {
        lights.remove(light);
    }
    
    public List<Light> getLights() {
        return lights;
    }
    
    public Light getSunLight() {
        return sunLight;
    }
    
    public void setSunLightPosition(Vector3f position) {
        if (sunLight != null) {
            sunLight.setPosition(position);
        }
    }
    
    public Vector3f getSunDirection() {
        if (sunLight != null) {
            Vector3f pos = sunLight.getPosition();
            // Return normalized direction (position is treated as direction for directional light)
            return new Vector3f(pos).normalize();
        }
        return new Vector3f(1, 1, 1).normalize();
    }
    
    public Vector3f getSunColor() {
        if (sunLight != null) {
            return sunLight.getColor();
        }
        return new Vector3f(1, 1, 1);
    }
    
    public Vector3f getAmbientColor() {
        if (sunLight != null) {
            return sunLight.getAmbientColor();
        }
        return new Vector3f(0.2f, 0.2f, 0.2f);
    }
    
    public void cleanup() {
        lights.clear();
    }
}