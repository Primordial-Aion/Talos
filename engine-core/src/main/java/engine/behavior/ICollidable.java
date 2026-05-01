package engine.behavior;

import org.joml.Vector3f;

public interface ICollidable {
    boolean checkCollision(ICollidable other);
    boolean checkCollision(Vector3f point);
    float getBoundingRadius();
}