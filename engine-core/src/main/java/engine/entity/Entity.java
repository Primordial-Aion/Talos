package engine.entity;

import engine.model.Model;
import engine.model.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class Entity {
    private static long nextId = 0;
    
    private final long id;
    private String name;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;
    private Model model;
    private boolean visible = true;
    private boolean dirty = true;
    private Matrix4f cachedTransform;
    private final List<Component> components;
    
    public Entity() {
        this.id = nextId++;
        this.name = "entity_" + id;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.cachedTransform = new Matrix4f();
        this.components = new ArrayList<>();
    }
    
    public Entity(Model model) {
        this();
        this.model = model;
    }
    
    public Entity(Model model, Vector3f position) {
        this(model);
        this.position.set(position);
    }
    
    public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this(model);
        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);
    }
    
    public void update(float deltaTime) {
        for (Component c : components) {
            c.init();
            c.update(deltaTime);
        }
    }
    
    public <T extends Component> T addComponent(T component) {
        component.setEntity(this);
        components.add(component);
        return component;
    }
    
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return componentClass.cast(c);
            }
        }
        return null;
    }
    
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        components.removeIf(c -> componentClass.isAssignableFrom(c.getClass()));
    }
    
    public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix, engine.shader.ShaderProgram shader) {
        if (!visible || model == null) return;
        
        Matrix4f modelMatrix = getTransformMatrix();
        
        shader.setUniformMat4("model", modelMatrix.get(new float[16]));
        shader.setUniformMat4("view", viewMatrix.get(new float[16]));
        shader.setUniformMat4("projection", projectionMatrix.get(new float[16]));
        
        model.render(modelMatrix, shader);
    }
    
    public Matrix4f getTransformMatrix() {
        cachedTransform.identity();
        cachedTransform.translate(position);
        cachedTransform.rotateXYZ(rotation);
        cachedTransform.scale(scale);
        dirty = false;
        return cachedTransform;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
        dirty = true;
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
        dirty = true;
    }
    
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        dirty = true;
    }
    
    public void setScale(float scale) {
        this.scale.set(scale, scale, scale);
        dirty = true;
    }
    
    public void translate(Vector3f offset) {
        this.position.add(offset);
        dirty = true;
    }
    
    public void rotate(Vector3f offset) {
        this.rotation.add(offset);
        dirty = true;
    }
    
    public Vector3f getPosition() {
        return new Vector3f(position);
    }
    
    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }
    
    public Vector3f getScale() {
        return new Vector3f(scale);
    }
    
    public Model getModel() {
        return model;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public long getId() {
        return id;
    }
    
    public boolean isDirty() {
        return dirty;
    }
}