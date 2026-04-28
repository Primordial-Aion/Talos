package engine.camera;

import engine.input.Input;
import engine.util.Config;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static Camera instance;
    
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f forward;
    private Vector3f up;
    private Vector3f right;
    
    private float fov;
    private float nearPlane;
    private float farPlane;
    private float moveSpeed;
    private float mouseSensitivity;
    
    private boolean active = true;
    
    private Camera() {
        this.position = new Vector3f(0, 5, 10);
        this.rotation = new Vector3f(0, 0, 0);
        this.forward = new Vector3f(0, 0, -1);
        this.up = new Vector3f(0, 1, 0);
        this.right = new Vector3f(1, 0, 0);
        
        this.fov = Config.get().enableDebug ? 70.0f : 70.0f;
        this.nearPlane = 0.1f;
        this.farPlane = 1000.0f;
        this.moveSpeed = Config.get().moveSpeed;
        this.mouseSensitivity = Config.get().mouseSensitivity;
    }
    
    public static Camera get() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }
    
    public void update(float deltaTime) {
        if (!active) return;
        
        float velocity = moveSpeed * deltaTime;
        
        if (Input.isKeyHeld(Input.Keys.W)) {
            position.add(forward.x * velocity, forward.y * velocity, forward.z * velocity);
        }
        if (Input.isKeyHeld(Input.Keys.S)) {
            position.sub(forward.x * velocity, forward.y * velocity, forward.z * velocity);
        }
        if (Input.isKeyHeld(Input.Keys.A)) {
            position.sub(right.x * velocity, right.y * velocity, right.z * velocity);
        }
        if (Input.isKeyHeld(Input.Keys.D)) {
            position.add(right.x * velocity, right.y * velocity, right.z * velocity);
        }
        if (Input.isKeyHeld(Input.Keys.LEFT_SHIFT) || Input.isKeyHeld(Input.Keys.LEFT_CONTROL)) {
            position.add(new Vector3f(0, velocity, 0));
        }
        if (Input.isKeyHeld(Input.Keys.SPACE) || Input.isKeyHeld(Input.Keys.LEFT_ALT)) {
            position.sub(new Vector3f(0, velocity, 0));
        }
        
        float mouseDX = (float) Input.getMouseDeltaX() * mouseSensitivity;
        float mouseDY = (float) Input.getMouseDeltaY() * mouseSensitivity;
        
        rotation.y += mouseDX;
        rotation.x -= mouseDY;
        
        rotation.x = Math.max(-89, Math.min(89, rotation.x));
        
        updateVectors();
    }
    
    public void updateVectors() {
        float pitch = (float) Math.toRadians(rotation.x);
        float yaw = (float) Math.toRadians(rotation.y);
        
        forward.x = (float) (Math.sin(yaw) * Math.cos(pitch));
        forward.y = (float) Math.sin(pitch);
        forward.z = (float) (-Math.cos(yaw) * Math.cos(pitch));
        forward.normalize();
        
        right.set(forward.z, 0, -forward.x);
        right.normalize();
        
        up.set(0, 1, 0);
    }
    
    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        
        float pitch = (float) Math.toRadians(rotation.x);
        float yaw = (float) Math.toRadians(rotation.y);
        
        float cosP = (float) Math.cos(pitch);
        float sinP = (float) Math.sin(pitch);
        float cosY = (float) Math.cos(yaw);
        float sinY = (float) Math.sin(yaw);
        
        float x = position.x;
        float y = position.y;
        float z = position.z;
        
        view.m20(sinY);
        view.m21(sinP);
        view.m22(-cosP * cosY);
        view.m23(0);
        
        view.m00(cosY);
        view.m02(sinY);
        view.m10(0);
        view.m12(cosP);
        view.m20(sinP * cosY);
        view.m21(cosP);
        view.m22(sinP * sinY);
        
        float tx = -(x * view.m00() + y * view.m10() + z * view.m20());
        float ty = -(x * view.m01() + y * view.m11() + z * view.m21());
        float tz = -(x * view.m02() + y * view.m12() + z * view.m22());
        
        view.m30(tx);
        view.m31(ty);
        view.m32(tz);
        view.m33(1);
        
        return view;
    }
    
    public Matrix4f getProjectionMatrix(float aspectRatio) {
        Matrix4f projection = new Matrix4f();
        projection.perspective((float) Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
        return projection;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
        updateVectors();
    }
    
    public void translate(Vector3f offset) {
        this.position.add(offset);
    }
    
    public Vector3f getPosition() {
        return new Vector3f(position);
    }
    
    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }
    
    public Vector3f getForward() {
        return new Vector3f(forward);
    }
    
    public Vector3f getUp() {
        return new Vector3f(up);
    }
    
    public Vector3f getRight() {
        return new Vector3f(right);
    }
    
    public float getFov() {
        return fov;
    }
    
    public void setFov(float fov) {
        this.fov = fov;
    }
    
    public float getNearPlane() {
        return nearPlane;
    }
    
    public float getFarPlane() {
        return farPlane;
    }
    
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }
    
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
    
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    
    public void setMouseSensitivity(float sensitivity) {
        this.mouseSensitivity = sensitivity;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
}