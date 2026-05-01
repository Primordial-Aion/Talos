package engine.behavior;

public interface IAnimatable {
    void playAnimation(String name);
    void stopAnimation();
    boolean isAnimationPlaying();
    String getCurrentAnimation();
    void setAnimationSpeed(float speed);
}