package engine.model;

import engine.util.Logger;
import engine.util.FileUtils;
import engine.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private int vao;
    private int vbo;
    private int ebo;
    private int vertexCount;
    private int indexCount;
    private int indexBuffer[];
    private float vertices[];
    private boolean hasNormals = false;
    private boolean hasTexCoords = false;
    private boolean hasColors = false;
    
    private static final int POSITION_SIZE = 3;
    private static final int NORMAL_SIZE = 3;
    private static final int TEXCOORD_SIZE = 2;
    private static final int COLOR_SIZE = 3;
    
    private Mesh() {}
    
    public static Mesh create(float[] vertices, int[] indices) {
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    public static Mesh createCube() {
        float[] vertices = new float[] {
            -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
             1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
             1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
            -1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
             1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
             1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
            -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
             1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
             1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
             1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
             1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
             1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
             1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
             1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            -1.0f, -1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f
        };
        
        int[] indices = new int[] {
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12,
            16, 17, 18, 18, 19, 16,
            20, 21, 22, 22, 23, 20
        };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    public static Mesh createQuad() {
        float[] vertices = new float[] {
            -1.0f, 0.0f, 1.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,
             1.0f, 0.0f, 1.0f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
             1.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,
            -1.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f
        };
        int[] indices = new int[] { 0, 1, 2, 2, 3, 0 };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    public static Mesh createTriangle() {
        float[] vertices = new float[] {
            0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.5f, 0.0f,
            1.0f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,
            0.0f, 0.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f
        };
        int[] indices = new int[] { 0, 1, 2 };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    private void init(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indexBuffer = indices;
        this.vertexCount = vertices.length / 8;
        this.indexCount = indices.length;
        
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        
        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        
        ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * 4, 0);
        
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 8 * 4, 3 * 4);
        
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 8 * 4, 6 * 4);
        
        GL30.glBindVertexArray(0);
        
        hasNormals = true;
        hasTexCoords = true;
    }
    
    public void render() {
        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    public void renderInstanced(int instanceCount) {
        GL30.glBindVertexArray(vao);
        // Simple instanced rendering not supported, falling back to regular render
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    public void cleanup() {
        GL15.glDeleteBuffers(vbo);
        GL15.glDeleteBuffers(ebo);
        GL30.glDeleteVertexArrays(vao);
    }
    
    public int getVao() {
        return vao;
    }
    
    public int getVertexCount() {
        return vertexCount;
    }
    
    public int getIndexCount() {
        return indexCount;
    }
    
    public float[] getVertices() {
        return vertices;
    }
    
    public int[] getIndices() {
        return indexBuffer;
    }
}