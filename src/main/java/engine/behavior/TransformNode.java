package engine.behavior;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TransformNode {
    private TransformNode parent;
    private List<TransformNode> children;
    
    private Vector3f localPosition;
    private Vector3f localRotation;
    private Vector3f localScale;
    
    private Vector3f worldPosition;
    private Vector3f worldRotation;
    private Vector3f worldScale;
    
    private Matrix4f cachedLocalMatrix;
    private Matrix4f cachedWorldMatrix;
    private boolean dirty = true;
    
    public TransformNode() {
        this.children = new ArrayList<>();
        this.localPosition = new Vector3f(0, 0, 0);
        this.localRotation = new Vector3f(0, 0, 0);
        this.localScale = new Vector3f(1, 1, 1);
        this.worldPosition = new Vector3f(0, 0, 0);
        this.worldRotation = new Vector3f(0, 0, 0);
        this.worldScale = new Vector3f(1, 1, 1);
        this.cachedLocalMatrix = new Matrix4f();
        this.cachedWorldMatrix = new Matrix4f();
    }
    
    public void update(float deltaTime) {
        updateWorldTransforms();
        for (TransformNode child : children) {
            child.update(deltaTime);
        }
    }
    
    private void updateWorldTransforms() {
        if (dirty || (parent != null && parent.dirty)) {
            if (parent != null) {
                worldPosition.set(parent.worldPosition).add(localPosition);
                worldRotation.set(parent.worldRotation).add(localRotation);
                worldScale.set(parent.worldScale).mul(localScale);
            } else {
                worldPosition.set(localPosition);
                worldRotation.set(localRotation);
                worldScale.set(localScale);
            }
            dirty = false;
        }
    }
    
    public void setParent(TransformNode parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
        markDirty();
    }
    
    public TransformNode getParent() {
        return parent;
    }
    
    public void markDirty() {
        this.dirty = true;
        for (TransformNode child : children) {
            child.markDirty();
        }
    }
    
    public void setLocalPosition(Vector3f position) {
        this.localPosition.set(position);
        markDirty();
    }
    
    public void setLocalPosition(float x, float y, float z) {
        this.localPosition.set(x, y, z);
        markDirty();
    }
    
    public void setLocalRotation(Vector3f rotation) {
        this.localRotation.set(rotation);
        markDirty();
    }
    
    public void setLocalRotation(float x, float y, float z) {
        this.localRotation.set(x, y, z);
        markDirty();
    }
    
    public void setLocalScale(Vector3f scale) {
        this.localScale.set(scale);
        markDirty();
    }
    
    public void setLocalScale(float x, float y, float z) {
        this.localScale.set(x, y, z);
        markDirty();
    }
    
    public void translateLocal(Vector3f offset) {
        localPosition.add(offset);
        markDirty();
    }
    
    public void rotateLocal(Vector3f rotation) {
        localRotation.add(rotation);
        markDirty();
    }
    
    public Vector3f getLocalPosition() {
        return new Vector3f(localPosition);
    }
    
    public Vector3f getLocalRotation() {
        return new Vector3f(localRotation);
    }
    
    public Vector3f getLocalScale() {
        return new Vector3f(localScale);
    }
    
    public Vector3f getWorldPosition() {
        if (dirty) updateWorldTransforms();
        return new Vector3f(worldPosition);
    }
    
    public Vector3f getWorldRotation() {
        if (dirty) updateWorldTransforms();
        return new Vector3f(worldRotation);
    }
    
    public Vector3f getWorldScale() {
        if (dirty) updateWorldTransforms();
        return new Vector3f(worldScale);
    }
    
    public Matrix4f getLocalMatrix() {
        cachedLocalMatrix.identity();
        cachedLocalMatrix.translate(localPosition);
        cachedLocalMatrix.rotateXYZ(localRotation);
        cachedLocalMatrix.scale(localScale);
        return cachedLocalMatrix;
    }
    
    public Matrix4f getWorldMatrix() {
        if (parent != null) {
            cachedWorldMatrix.set(parent.getWorldMatrix());
            cachedWorldMatrix.translate(localPosition);
            cachedWorldMatrix.rotateXYZ(localRotation);
            cachedWorldMatrix.scale(localScale);
        } else {
            cachedWorldMatrix.set(getLocalMatrix());
        }
        return cachedWorldMatrix;
    }
    
    public List<TransformNode> getChildren() {
        return new ArrayList<>(children);
    }
    
    public void addChild(TransformNode child) {
        child.setParent(this);
    }
    
    public void removeChild(TransformNode child) {
        child.setParent(null);
    }
    
    public int getChildCount() {
        return children.size();
    }
}