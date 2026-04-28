package game.player;

import engine.camera.Camera;
import engine.entity.Entity;
import engine.input.Input;
import engine.model.Model;
import engine.physics.Physics;
import org.joml.Vector3f;

public class Player extends Entity {
    private Camera camera;
    private Physics physics;
    private boolean isWalking = false;
    private boolean isRunning = false;
    private boolean isJumping = false;
    private float moveSpeed = 5.0f;
    private float runSpeed = 10.0f;
    private JumpState jumpState = JumpState.GROUNDED;
    private float jumpVelocity = 0;
    private float gravity = -20.0f;
    private float jumpForce = 8.0f;
    
    public enum JumpState {
        GROUNDED, JUMPING, FALLING
    }
    
    public Player() {
        super();
        this.camera = Camera.get();
        this.physics = Physics.get();
    }
    
    public Player(Model model) {
        super(model);
        this.camera = Camera.get();
        this.physics = Physics.get();
        setPosition(new Vector3f(0, physics.getPlayerHeight(), 0));
    }
    
    @Override
    public void update(float deltaTime) {
        handleInput(deltaTime);
        applyPhysics(deltaTime);
        
        camera.setPosition(getPosition());
    }
    
    private void handleInput(float deltaTime) {
        float speed = isRunning ? runSpeed : moveSpeed;
        
        if (Input.isKeyHeld(Input.Keys.LEFT_SHIFT)) {
            isRunning = true;
        } else {
            isRunning = false;
        }
        
        if (Input.isKeyPressed(Input.Keys.SPACE) && jumpState == JumpState.GROUNDED) {
            jumpState = JumpState.JUMPING;
            jumpVelocity = jumpForce;
            isJumping = true;
        }
        
        isWalking = Input.isKeyHeld(Input.Keys.W) || Input.isKeyHeld(Input.Keys.S) || 
                  Input.isKeyHeld(Input.Keys.A) || Input.isKeyHeld(Input.Keys.D);
    }
    
    private void applyPhysics(float deltaTime) {
        Vector3f pos = getPosition();
        
        physics.snapToGround(pos, deltaTime);
        
        switch (jumpState) {
            case JUMPING -> {
                jumpVelocity += gravity * deltaTime;
                pos.y += jumpVelocity * deltaTime;
                
                if (jumpVelocity <= 0) {
                    jumpState = JumpState.FALLING;
                }
            }
            case FALLING -> {
                jumpVelocity += gravity * deltaTime;
                pos.y += jumpVelocity * deltaTime;
                
                float groundY = physics.getGroundHeightAt(pos.x, pos.z) + physics.getPlayerHeight();
                if (pos.y <= groundY) {
                    pos.y = groundY;
                    jumpState = JumpState.GROUNDED;
                    jumpVelocity = 0;
                    isJumping = false;
                }
            }
            case GROUNDED -> {
                float groundY = physics.getGroundHeightAt(pos.x, pos.z) + physics.getPlayerHeight();
                if (pos.y > groundY + 0.1f) {
                    jumpState = JumpState.FALLING;
                }
            }
        }
        
        if (!physics.isWithinBounds(pos)) {
            pos = physics.clampToBounds(pos);
        }
        
        setPosition(pos);
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public boolean isWalking() {
        return isWalking;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean isJumping() {
        return isJumping;
    }
    
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }
    
    public void setRunSpeed(float speed) {
        this.runSpeed = speed;
    }
}