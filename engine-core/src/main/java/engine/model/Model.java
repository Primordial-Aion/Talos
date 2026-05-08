package engine.model;

import engine.render.Renderer;
import engine.texture.Texture;
import engine.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Model {
    private Mesh mesh;
    private Texture texture;
    private String name;
    private boolean isInstanced = false;
    private int instanceCount = 0;
    private Vector3f color = new Vector3f(0.5f, 0.3f, 0.7f); // Default purple-ish
    
    public Model(Mesh mesh) {
        this.mesh = mesh;
        this.name = "model";
    }
    
    public Model(Mesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
        this.name = "model";
    }
    
    public void render(Matrix4f modelMatrix, ShaderProgram shader) {
        // Set the model matrix (already set by Entity.render(), but set again to be safe)
        shader.setUniformMat4("model", modelMatrix.get(new float[16]));
        
        if (texture != null) {
            texture.bind(0);
            shader.setUniform("useTexture", true);
            shader.setUniform("texture0", 0);
        } else {
            shader.setUniform("useTexture", false);
            shader.setUniform3("objectColor", color);
        }
        
        if (isInstanced) {
            mesh.renderInstanced(instanceCount);
        } else {
            mesh.render();
        }
    }
    
    public void setColor(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }
    
    public void setColor(Vector3f color) {
        this.color = color;
    }
    
    public Vector3f getColor() {
        return color;
    }
    
    public void cleanup() {
        if (mesh != null) {
            mesh.cleanup();
        }
        if (texture != null) {
            texture.cleanup();
        }
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setInstanced(boolean instanced) {
        this.isInstanced = instanced;
    }
    
    public void setInstanceCount(int count) {
        this.instanceCount = count;
    }
    
    public boolean isInstanced() {
        return isInstanced;
    }
    
    public int getInstanceCount() {
        return instanceCount;
    }
}