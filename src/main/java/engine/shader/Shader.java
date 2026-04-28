package engine.shader;

import engine.util.Logger;
import engine.util.FileUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    private static Shader activeShader = null;
    
    private final int programId;
    private final String name;
    private static final Map<String, Shader> loadedShaders = new HashMap<>();
    
    private Shader(String name, int programId) {
        this.name = name;
        this.programId = programId;
    }
    
    public static Shader loadShader(String name, String vertexPath, String fragmentPath) {
        if (loadedShaders.containsKey(name)) {
            return loadedShaders.get(name);
        }
        
        String vertexSource = FileUtils.readFileAsString(vertexPath);
        String fragmentSource = FileUtils.readFileAsString(fragmentPath);
        
        int vertexShaderId = compileShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
        
        int programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL32.glBindFragDataLocation(programId, 0, "outColor");
        GL20.glLinkProgram(programId);
        
        int linkStatus = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        if (linkStatus == GL11.GL_FALSE) {
            String infoLog = GL20.glGetProgramInfoLog(programId);
            throw new RuntimeException("Shader program linking failed: " + infoLog);
        }
        
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        
        Shader shader = new Shader(name, programId);
        loadedShaders.put(name, shader);
        
        Logger.info("Shader loaded: " + name);
        return shader;
    }
    
    private static int compileShader(String source, int type) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);
        
        int compileStatus = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
        if (compileStatus == GL11.GL_FALSE) {
            String infoLog = GL20.glGetShaderInfoLog(shaderId);
            String shaderType = type == GL20.GL_VERTEX_SHADER ? "vertex" : "fragment";
            throw new RuntimeException(shaderType + " shader compilation failed: " + infoLog);
        }
        
        return shaderId;
    }
    
    public void bind() {
        GL20.glUseProgram(programId);
        activeShader = this;
    }
    
    public static void unbind() {
        GL20.glUseProgram(0);
        activeShader = null;
    }
    
    public void setUniform(String name, float value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform1f(location, value);
        }
    }
    
    public void setUniform(String name, int value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform1i(location, value);
        }
    }
    
    public void setUniform(String name, float x, float y) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform2f(location, x, y);
        }
    }
    
    public void setUniform(String name, float x, float y, float z) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform3f(location, x, y, z);
        }
    }
    
    public void setUniform(String name, float x, float y, float z, float w) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform4f(location, x, y, z, w);
        }
    }
    
    public void setUniformMat4(String name, FloatBuffer matrix) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniformMatrix4fv(location, false, matrix);
        }
    }
    
    public void setUniformMat4(String name, org.joml.Matrix4f matrix) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            matrix.get(new float[16]);
            GL20.glUniformMatrix4fv(location, false, matrix.get(new float[16]));
        }
    }
    
    public void setUniform3(String name, org.joml.Vector3f vector) {
        setUniform(name, vector.x, vector.y, vector.z);
    }
    
    public void cleanup() {
        GL20.glDeleteProgram(programId);
        loadedShaders.remove(name);
    }
    
    public int getProgramId() {
        return programId;
    }
    
    public String getName() {
        return name;
    }
    
    public static Shader getActiveShader() {
        return activeShader;
    }
}