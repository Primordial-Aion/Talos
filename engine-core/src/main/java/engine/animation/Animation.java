package engine.animation;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private String name;
    private float duration;
    private float speed = 1.0f;
    private boolean playing = false;
    private boolean looping = false;
    private float currentTime = 0;
    private List<Keyframe> keyframes;
    
    public Animation(String name, float duration) {
        this.name = name;
        this.duration = duration;
        this.keyframes = new ArrayList<>();
    }
    
    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
    }
    
    public void play() {
        playing = true;
        currentTime = 0;
    }
    
    public void pause() {
        playing = false;
    }
    
    public void stop() {
        playing = false;
        currentTime = 0;
    }
    
    public void update(float deltaTime) {
        if (!playing) return;
        
        currentTime += deltaTime * speed;
        
        if (currentTime >= duration) {
            if (looping) {
                currentTime = currentTime % duration;
            } else {
                currentTime = duration;
                playing = false;
            }
        }
    }
    
    public Keyframe getCurrentKeyframe() {
        Keyframe prev = keyframes.get(0);
        Keyframe next = keyframes.get(keyframes.size() - 1);
        
        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (currentTime >= keyframes.get(i).getTime() && currentTime < keyframes.get(i + 1).getTime()) {
                prev = keyframes.get(i);
                next = keyframes.get(i + 1);
                break;
            }
        }
        
        return prev;
    }
    
    public Keyframe[] getSurroundingKeyframes() {
        Keyframe prev = keyframes.get(0);
        Keyframe next = keyframes.get(keyframes.size() - 1);
        
        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (currentTime >= keyframes.get(i).getTime() && currentTime < keyframes.get(i + 1).getTime()) {
                prev = keyframes.get(i);
                next = keyframes.get(i + 1);
                break;
            }
        }
        
        return new Keyframe[] {prev, next};
    }
    
    public float getInterpolationFactor() {
        Keyframe[] surrounding = getSurroundingKeyframes();
        float t0 = surrounding[0].getTime();
        float t1 = surrounding[1].getTime();
        
        return (currentTime - t0) / (t1 - t0);
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public void setLooping(boolean looping) {
        this.looping = looping;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public boolean isLooping() {
        return looping;
    }
    
    public float getCurrentTime() {
        return currentTime;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Keyframe> getKeyframes() {
        return keyframes;
    }
}