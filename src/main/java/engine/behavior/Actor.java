package engine.behavior;

import org.joml.Vector3f;
import engine.behavior.GameObject;
import engine.model.Model;

public class Actor extends GameObject {
    private Vector3f velocity;
    private float moveSpeed = 5.0f;
    private boolean grounded = true;
    
    private float health = 100.0f;
    private float maxHealth = 100.0f;
    
    public Actor() {
        super();
        this.velocity = new Vector3f(0, 0, 0);
    }
    
    public Actor(String name) {
        super(name);
        this.velocity = new Vector3f(0, 0, 0);
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        Vector3f pos = getPosition();
        pos.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
        setPosition(pos);
    }
    
    public void move(Vector3f direction, float deltaTime) {
        velocity.set(direction).mul(moveSpeed * deltaTime);
    }
    
    public void stop() {
        velocity.set(0, 0, 0);
    }
    
    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
    }
    
    public Vector3f getVelocity() {
        return new Vector3f(velocity);
    }
    
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }
    
    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }
    
    public boolean isGrounded() {
        return grounded;
    }
    
    public void takeDamage(float damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public void heal(float amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public void setHealth(float health) {
        this.health = health;
    }
    
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    @Override
    public void onSave(java.util.Map<String, Object> data) {
        super.onSave(data);
        data.put("health", health);
        data.put("maxHealth", maxHealth);
        data.put("moveSpeed", moveSpeed);
    }
    
    @Override
    public void onLoad(java.util.Map<String, Object> data) {
        super.onLoad(data);
        if (data.containsKey("health")) this.health = (float) data.get("health");
        if (data.containsKey("maxHealth")) this.maxHealth = (float) data.get("maxHealth");
        if (data.containsKey("moveSpeed")) this.moveSpeed = (float) data.get("moveSpeed");
    }
}