package engine.behavior;

public interface IUpdateable {
    void update(float deltaTime);
    boolean isEnabled();
    void setEnabled(boolean enabled);
}