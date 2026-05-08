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
            // Front face
            -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
             1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
             1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
            // Back face
            -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
            -1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
             1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
             1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
            // Top face
            -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
             1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
             1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
            // Bottom face
            -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
             1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
             1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
            // Right face
             1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
             1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
             1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            // Left face
            -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
            -1.0f, -1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
            -1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f
        };
        
        int[] indices = new int[] {
            0, 1, 2, 2, 3, 0,       // Front
            4, 5, 6, 6, 7, 4,       // Back
            8, 9, 10, 10, 11, 8,    // Top
            12, 13, 14, 14, 15, 12, // Bottom
            16, 17, 18, 18, 19, 16, // Right
            20, 21, 22, 22, 23, 20  // Left
        };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    public static Mesh createQuad() {
        float[] vertices = new float[] {
            -1.0f, 0.0f, 1.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, // 0
             1.0f, 0.0f, 1.0f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f, // 1
             1.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f, // 2
            -1.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f  // 3
        };
        // CCW: 0, 1, 2 and 0, 2, 3 (looking from +Y)
        int[] indices = new int[] { 0, 1, 2, 2, 3, 0 }; 
        // Wait, (0,1,2) is (-1,1) -> (1,1) -> (1,-1). Looking from +Y, this is CW.
        // To be CCW: (0, 2, 1) and (0, 3, 2)
        int[] ccwIndices = new int[] { 0, 2, 1, 0, 3, 2 };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, ccwIndices);
        return mesh;
    }
    
    public static Mesh createTriangle() {
        float[] vertices = new float[] {
            0.0f, 1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  0.5f, 1.0f,
            1.0f, 0.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f,
            0.0f, 0.0f, 0.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f
        };
        // CCW: 0, 2, 1
        int[] indices = new int[] { 0, 2, 1 };
        
        Mesh mesh = new Mesh();
        mesh.init(vertices, indices);
        return mesh;
    }
    
    private void init(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indexBuffer = indices;
        this.vertexCount = vertices.length / 8;
        this.indexCount = indices.length;
        
        java.nio.FloatBuffer vertexBuffer = org.lwjgl.system.MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        
        java.nio.IntBuffer indexBufferData = org.lwjgl.system.MemoryUtil.memAllocInt(indices.length);
        indexBufferData.put(indices).flip();

        try {
            vao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vao);
            
            vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
            
            ebo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferData, GL15.GL_STATIC_DRAW);
            
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * 4, 0);
            
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 8 * 4, 3 * 4);
            
            GL20.glEnableVertexAttribArray(2);
            GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 8 * 4, 6 * 4);
            
            GL30.glBindVertexArray(0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        } finally {
            org.lwjgl.system.MemoryUtil.memFree(vertexBuffer);
            org.lwjgl.system.MemoryUtil.memFree(indexBufferData);
        }
        
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