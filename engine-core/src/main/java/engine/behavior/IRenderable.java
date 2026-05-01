package engine.behavior;

public interface IRenderable {
    void render();
    boolean isVisible();
    void setVisible(boolean visible);
    int getRenderPriority();
}