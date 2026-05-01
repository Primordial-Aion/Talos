package engine.animation;

import java.util.HashMap;
import java.util.Map;

public class Keyframe {
    private float time;
    private Map<String, float[]> transforms;
    
    public Keyframe(float time) {
        this.time = time;
        this.transforms = new HashMap<>();
    }
    
    public void setPosition(float x, float y, float z) {
        transforms.put("position", new float[] {x, y, z});
    }
    
    public void setRotation(float x, float y, float z) {
        transforms.put("rotation", new float[] {x, y, z});
    }
    
    public void setScale(float x, float y, float z) {
        transforms.put("scale", new float[] {x, y, z});
    }
    
    public float getTime() {
        return time;
    }
    
    public float[] getTransform(String type) {
        return transforms.get(type);
    }
}