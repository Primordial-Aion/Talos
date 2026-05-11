package engine.behavior;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import engine.model.Model;
import engine.behavior.TransformNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameObject {
    private static long nextId = 0;
    
    private final long id;
    private String name;
    private String prefabId;
    
    private TransformNode transform;
    private Model model;
    private boolean visible = true;
    private boolean active = true;
    
    private List<Consumer<Float>> updateHooks;
    private List<Consumer<GameObject>> interactHooks;
    private List<Consumer<GameObject>> destroyHooks;
    private List<Consumer<String>> animationEventHooks;
    
    public GameObject() {
        this.id = nextId++;
        this.name = "GameObject_" + id;
        this.transform = new TransformNode();
        this.transform.setOwner(this);
        this.updateHooks = new ArrayList<>();
        this.interactHooks = new ArrayList<>();
        this.destroyHooks = new ArrayList<>();
        this.animationEventHooks = new ArrayList<>();
    }
    
    public GameObject(String name) {
        this();
        this.name = name;
    }
    
    public void update(float deltaTime) {
        if (!active) return;
        
        transform.update(deltaTime);
        
        for (Consumer<Float> hook : updateHooks) {
            hook.accept(deltaTime);
        }
    }
    
    public void onCreate() {
    }
    
    public void onInteract(GameObject interactor) {
        for (Consumer<GameObject> hook : interactHooks) {
            hook.accept(interactor);
        }
    }
    
    public void onDestroy() {
        for (Consumer<GameObject> hook : destroyHooks) {
            hook.accept(this);
        }
    }
    
    public void onAnimationEvent(String event) {
        for (Consumer<String> hook : animationEventHooks) {
            hook.accept(event);
        }
    }
    
    public void onSave(java.util.Map<String, Object> data) {
        data.put("id", id);
        data.put("name", name);
        data.put("prefabId", prefabId);
        data.put("position", new float[] {transform.getLocalPosition().x, transform.getLocalPosition().y, transform.getLocalPosition().z});
        data.put("rotation", new float[] {transform.getLocalRotation().x, transform.getLocalRotation().y, transform.getLocalRotation().z});
        data.put("scale", new float[] {transform.getLocalScale().x, transform.getLocalScale().y, transform.getLocalScale().z});
        data.put("visible", visible);
        data.put("active", active);
    }
    
    public void onLoad(java.util.Map<String, Object> data) {
        if (data.containsKey("name")) this.name = (String) data.get("name");
        if (data.containsKey("prefabId")) this.prefabId = (String) data.get("prefabId");
        if (data.containsKey("position")) {
            float[] pos = (float[]) data.get("position");
            transform.setLocalPosition(pos[0], pos[1], pos[2]);
        }
        if (data.containsKey("rotation")) {
            float[] rot = (float[]) data.get("rotation");
            transform.setLocalRotation(rot[0], rot[1], rot[2]);
        }
        if (data.containsKey("scale")) {
            float[] scale = (float[]) data.get("scale");
            transform.setLocalScale(scale[0], scale[1], scale[2]);
        }
        if (data.containsKey("visible")) this.visible = (boolean) data.get("visible");
        if (data.containsKey("active")) this.active = (boolean) data.get("active");
    }
    
    public GameObject onUpdate(Consumer<Float> hook) {
        updateHooks.add(hook);
        return this;
    }
    
    public GameObject onInteract(Consumer<GameObject> hook) {
        interactHooks.add(hook);
        return this;
    }
    
    public GameObject onDestroy(Consumer<GameObject> hook) {
        destroyHooks.add(hook);
        return this;
    }
    
    public GameObject onAnimationEvent(Consumer<String> hook) {
        animationEventHooks.add(hook);
        return this;
    }
    
    public void setPosition(float x, float y, float z) {
        transform.setLocalPosition(x, y, z);
    }
    
    public void setPosition(Vector3f position) {
        transform.setLocalPosition(position);
    }
    
    public void setRotation(float x, float y, float z) {
        transform.setLocalRotation(x, y, z);
    }
    
    public void setRotation(Vector3f rotation) {
        transform.setLocalRotation(rotation);
    }
    
    public void setScale(float scale) {
        transform.setLocalScale(scale, scale, scale);
    }
    
    public void setScale(float x, float y, float z) {
        transform.setLocalScale(x, y, z);
    }
    
    public void setScale(org.joml.Vector3f scale) {
        transform.setLocalScale(scale);
    }
    
    public Vector3f getPosition() {
        return transform.getLocalPosition();
    }
    
    public Vector3f getWorldPosition() {
        return transform.getWorldPosition();
    }
    
    public Vector3f getRotation() {
        return transform.getLocalRotation();
    }
    
    public Vector3f getScale() {
        return transform.getLocalScale();
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
    
    public TransformNode getTransform() {
        return transform;
    }
    
    public void setTransformParent(GameObject parent) {
        transform.setParent(parent.transform);
    }
    
    public GameObject getParent() {
        TransformNode parent = transform.getParent();
        if (parent == null) return null;
        return parent.getOwner();
    }
    
    public void addChild(GameObject child) {
        transform.addChild(child.transform);
    }
    
    public List<GameObject> getChildren() {
        List<GameObject> result = new ArrayList<>();
        for (TransformNode child : transform.getChildren()) {
            result.add(child.getOwner());
        }
        return result;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setPrefabId(String prefabId) {
        this.prefabId = prefabId;
    }
    
    public String getPrefabId() {
        return prefabId;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public long getId() {
        return id;
    }
    
    public Matrix4f getModelMatrix() {
        return transform.getLocalMatrix();
    }
    
    public Matrix4f getWorldModelMatrix() {
        return transform.getWorldMatrix();
    }
}