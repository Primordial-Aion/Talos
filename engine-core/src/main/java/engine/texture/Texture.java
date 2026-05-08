package engine.texture;

import engine.util.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Texture {
    private final int id;
    private final String path;
    private int width;
    private int height;
    private int channels;
    private boolean loaded = false;
    
    private static final Map<String, Texture> textureCache = new HashMap<>();
    
    private Texture(String path, int id) {
        this.path = path;
        this.id = id;
    }
    
    public static Texture load(String path) {
        if (textureCache.containsKey(path)) {
            return textureCache.get(path);
        }
        
        try {
            int[] w = new int[1];
            int[] h = new int[1];
            int[] c = new int[1];
            
            ByteBuffer imageBuffer = STBImage.stbi_load(path, w, h, c, 4);
            if (imageBuffer == null) {
                Logger.error("Failed to load texture: " + path + " - " + STBImage.stbi_failure_reason());
                return createDefaultTexture();
            }
            
            int actualWidth = w[0];
            int actualHeight = h[0];
            
            int textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, actualWidth, actualHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            
            STBImage.stbi_image_free(imageBuffer);
            
            Texture texture = new Texture(path, textureId);
            texture.width = actualWidth;
            texture.height = actualHeight;
            texture.loaded = true;
            
            textureCache.put(path, texture);
            Logger.info("Texture loaded: " + path + " (" + actualWidth + "x" + actualHeight + ")");
            
            return texture;
        } catch (Exception e) {
            Logger.error("Failed to load texture: " + path + " - " + e.getMessage());
            return createDefaultTexture();
        }
    }
    
    private static Texture createDefaultTexture() {
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        ByteBuffer pixels = ByteBuffer.allocate(16);
        pixels.put(new byte[] {(byte)255, (byte)255, (byte)255, (byte)255});
        pixels.flip();
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        
        Texture texture = new Texture("default", textureId);
        texture.width = 1;
        texture.height = 1;
        texture.loaded = true;
        
        return texture;
    }
    
    public static Texture load(int w, int h, int channels) {
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        int format;
        if (channels == 4) format = GL11.GL_RGBA;
        else if (channels == 3) format = GL11.GL_RGB;
        else format = GL11.GL_RED;
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, w, h, 0, format, GL11.GL_UNSIGNED_BYTE, 0);
        
        Texture texture = new Texture("generated_" + w + "x" + h, textureId);
        texture.width = w;
        texture.height = h;
        texture.channels = channels;
        texture.loaded = true;
        
        return texture;
    }
    
    public void bind(int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }
    
    public void cleanup() {
        GL11.glDeleteTextures(id);
    }
    
    public int getId() {
        return id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Vector2f getSize() {
        return new Vector2f(width, height);
    }
    
    public boolean isLoaded() {
        return loaded;
    }
}