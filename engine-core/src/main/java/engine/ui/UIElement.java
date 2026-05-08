package engine.ui;

import engine.render.Renderer;
import engine.shader.ShaderProgram;
import engine.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class UIElement {
    private int vao, vbo;
    private Vector2f position;
    private Vector2f size;
    private Vector4f color;
    private Texture texture;
    private boolean visible = true;
    
    public UIElement() {
        this.position = new Vector2f(0, 0);
        this.size = new Vector2f(100, 100);
        this.color = new Vector4f(1, 1, 1, 1);
    }
    
    public UIElement(Vector2f position, Vector2f size) {
        this.position = position;
        this.size = size;
        this.color = new Vector4f(1, 1, 1, 1);
        initBuffers();
    }
    
    private void initBuffers() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        
        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        
        float[] vertices = {
            position.x, position.y, 0, 0,
            position.x + size.x, position.y, 1, 0,
            position.x + size.x, position.y + size.y, 1, 1,
            position.x, position.y + size.y, 0, 1
        };
        
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_DYNAMIC_DRAW);
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * 4, 0);
        
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * 4, 2 * 4);
        
        GL30.glBindVertexArray(0);
    }
    
    public void render(ShaderProgram shader) {
        if (!visible) return;
        
        updateBuffers();
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        shader.bind();
        
        engine.core.Window window = engine.core.Window.get();
        Matrix4f ortho = new Matrix4f().ortho(0, window.getWidth(), window.getHeight(), 0, -1, 1);
        shader.setUniformMat4("projection", ortho.get(new float[16]));
        
        if (texture != null) {
            texture.bind(0);
            shader.setUniform("texture0", 0);
        }
        
        shader.setUniform("useTexture", texture != null);
        shader.setUniform("color", color.x, color.y, color.z, color.w);
        
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);
        GL30.glBindVertexArray(0);
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    private void updateBuffers() {
        float[] vertices = {
            position.x, position.y, 0, 0,
            position.x + size.x, position.y, 1, 0,
            position.x + size.x, position.y + size.y, 1, 1,
            position.x, position.y + size.y, 0, 1
        };
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
    }
    
    public void setPosition(Vector2f position) {
        this.position.set(position);
    }
    
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
    
    public void setSize(Vector2f size) {
        this.size.set(size);
        initBuffers();
    }
    
    public void setSize(float width, float height) {
        this.size.set(width, height);
        initBuffers();
    }
    
    public void setColor(Vector4f color) {
        this.color.set(color);
    }
    
    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public Vector2f getPosition() {
        return position;
    }
    
    public Vector2f getSize() {
        return size;
    }
    
    public Vector4f getColor() {
        return color;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void cleanup() {
        if (vbo != 0) GL15.glDeleteBuffers(vbo);
        if (vao != 0) GL30.glDeleteVertexArrays(vao);
    }
}
