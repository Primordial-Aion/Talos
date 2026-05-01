package engine.animation;

import engine.entity.Entity;
import engine.util.Logger;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Animator {
    private static Animator instance;
    
    private Map<String, Animation> animations;
    private Animation currentAnimation;
    private Entity target;
    
    private Animator() {
        this.animations = new HashMap<>();
    }
    
    public static Animator get() {
        if (instance == null) {
            instance = new Animator();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing animator...");
        animations.clear();
        Logger.info("Animator initialized");
    }
    
    public void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }
    
    public void playAnimation(String name) {
        currentAnimation = animations.get(name);
        if (currentAnimation != null) {
            currentAnimation.play();
        }
    }
    
    public void stopAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
    }
    
    public void update(float deltaTime) {
        if (currentAnimation == null || target == null) return;
        
        currentAnimation.update(deltaTime);
        
        if (!currentAnimation.isPlaying()) return;
        
        Keyframe[] keyframes = currentAnimation.getSurroundingKeyframes();
        float t = currentAnimation.getInterpolationFactor();
        
        if (keyframes[0] != null && keyframes[1] != null) {
            float[] pos0 = keyframes[0].getTransform("position");
            float[] pos1 = keyframes[1].getTransform("position");
            
            if (pos0 != null && pos1 != null) {
                float x = lerp(pos0[0], pos1[0], t);
                float y = lerp(pos0[1], pos1[1], t);
                float z = lerp(pos0[2], pos1[2], t);
                target.setPosition(new Vector3f(x, y, z));
            }
            
            float[] rot0 = keyframes[0].getTransform("rotation");
            float[] rot1 = keyframes[1].getTransform("rotation");
            
            if (rot0 != null && rot1 != null) {
                float x = lerp(rot0[0], rot1[0], t);
                float y = lerp(rot0[1], rot1[1], t);
                float z = lerp(rot0[2], rot1[2], t);
                target.setRotation(new Vector3f(x, y, z));
            }
            
            float[] scale0 = keyframes[0].getTransform("scale");
            float[] scale1 = keyframes[1].getTransform("scale");
            
            if (scale0 != null && scale1 != null) {
                float x = lerp(scale0[0], scale1[0], t);
                float y = lerp(scale0[1], scale1[1], t);
                float z = lerp(scale0[2], scale1[2], t);
                target.setScale(new Vector3f(x, y, z));
            }
        }
    }
    
    public void setTarget(Entity target) {
        this.target = target;
    }
    
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
    
    public Map<String, Animation> getAnimations() {
        return animations;
    }
    
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}