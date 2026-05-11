package engine.physics;

import engine.camera.Camera;
import engine.terrain.Terrain;
import engine.util.Config;
import org.joml.Vector3f;

public class Physics {
    private static Physics instance;
    
    private float gravity = -9.81f;
    private float groundCheckDistance = 0.5f;
    private Terrain terrain;
    private float playerHeight = 1.8f;
    private float stepHeight = 0.5f;
    private float playerRadius = 0.3f;
    
    private Physics() {}
    
    public static Physics get() {
        if (instance == null) {
            instance = new Physics();
        }
        return instance;
    }
    
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
    
    public float getGroundHeightAt(float x, float z) {
        if (terrain != null) {
            return terrain.getTerrainHeight(x, z);
        }
        return 0;
    }
    
    public boolean isOnGround(Vector3f position) {
        float groundHeight = getGroundHeightAt(position.x, position.z);
        float distanceToGround = position.y - groundHeight;
        return distanceToGround <= groundCheckDistance;
    }
    
    public void snapToGround(Vector3f position, float deltaTime) {
        float groundHeight = getGroundHeightAt(position.x, position.z);
        float targetY = groundHeight + playerHeight;
        
        if (position.y < targetY) {
            position.y = targetY;
        }
    }
    
    public boolean checkCollision(Vector3f position, Vector3f targetPosition) {
        float groundHeight = getGroundHeightAt(targetPosition.x, targetPosition.z);
        
        if (targetPosition.y < groundHeight + playerHeight) {
            targetPosition.y = groundHeight + playerHeight;
        }
        
        return true;
    }
    
    public boolean isWithinBounds(Vector3f position) {
        if (terrain == null) return true;
        
        float halfSize = terrain.getSize() / 2.0f;
        float x = position.x;
        float z = position.z;
        
        return x >= -halfSize && x <= halfSize && z >= -halfSize && z <= halfSize;
    }
    
    public Vector3f clampToBounds(Vector3f position) {
        if (terrain == null) return position;
        
        float halfSize = terrain.getSize() / 2.0f;
        float x = Math.max(-halfSize, Math.min(halfSize, position.x));
        float z = Math.max(-halfSize, Math.min(halfSize, position.z));
        
        return new Vector3f(x, position.y, z);
    }
    
    public float getGravity() {
        return gravity;
    }
    
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    public float getPlayerHeight() {
        return playerHeight;
    }
    
    public void setPlayerHeight(float height) {
        this.playerHeight = height;
    }
    
    public float getPlayerRadius() {
        return playerRadius;
    }
    
    public void setPlayerRadius(float radius) {
        this.playerRadius = radius;
    }
}