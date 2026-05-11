package engine.shader;

import engine.util.FileUtils;
import engine.util.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import org.joml.Vector3f;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {
    private static final ShaderProgram DEFAULT = new ShaderProgram();
    private static final ShaderProgram TERRAIN = new ShaderProgram();
    private static final ShaderProgram UI = new ShaderProgram();
    
    private int programId;
    private String name;
    private final Map<String, Integer> uniformCache = new HashMap<>();
    
    public static final String DEFAULT_VERTEX = "shaders/default.vert";
    public static final String DEFAULT_FRAGMENT = "shaders/default.frag";
    public static final String TERRAIN_VERTEX = "shaders/terrain.vert";
    public static final String TERRAIN_FRAGMENT = "shaders/terrain.frag";
    public static final String UI_VERTEX = "shaders/ui.vert";
    public static final String UI_FRAGMENT = "shaders/ui.frag";
    
    private ShaderProgram() {}
    
    public static void init() {
        Logger.info("Initializing shader programs...");
        
        try {
            DEFAULT.init("default", DEFAULT_VERTEX, DEFAULT_FRAGMENT);
        } catch (Exception e) {
            DEFAULT.initDefaultShaders();
        }
        
        try {
            TERRAIN.init("terrain", TERRAIN_VERTEX, TERRAIN_FRAGMENT);
        } catch (Exception e) {
            TERRAIN.initDefaultTerrainShaders();
        }
        
        try {
            UI.init("ui", UI_VERTEX, UI_FRAGMENT);
        } catch (Exception e) {
            UI.initDefaultUIShaders();
        }
        
        Logger.info("Shader programs initialized");
    }
    
    private void init(String name, String vertexPath, String fragmentPath) {
        this.name = name;
        
        String vertexSource = FileUtils.readFileAsString(vertexPath);
        String fragmentSource = FileUtils.readFileAsString(fragmentPath);
        
        int vertexShaderId = compileShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
        
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL32.glBindFragDataLocation(programId, 0, "outColor");
        GL20.glLinkProgram(programId);
        
        int linkStatus = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        if (linkStatus == GL11.GL_FALSE) {
            String infoLog = GL20.glGetProgramInfoLog(programId);
            throw new RuntimeException("Shader linking failed for " + name + ": " + infoLog);
        }
        
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        
        Logger.info("Shader program loaded: " + name);
    }
    
    private int compileShader(String source, int type) {
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
    
    private void initDefaultShaders() {
        this.name = "default";
        initBuiltin("default");
    }
    
    private void initDefaultTerrainShaders() {
        this.name = "terrain";
        initBuiltin("terrain");
    }
    
    private void initDefaultUIShaders() {
        this.name = "ui";
        initBuiltin("ui");
    }
    
    private void initBuiltin(String type) {
        String vertexSource = "";
        String fragmentSource = "";
        
        if (type.equals("default")) {
            vertexSource = """
                #version 330 core
                layout(location = 0) in vec3 position;
                layout(location = 1) in vec3 normal;
                layout(location = 2) in vec2 texCoords;
                
                uniform mat4 model;
                uniform mat4 view;
                uniform mat4 projection;
                
                out vec3 fragPos;
                out vec3 normal_out;
                out vec2 texCoords_out;
                
                void main() {
                    fragPos = vec3(model * vec4(position, 1.0));
                    normal_out = mat3(transpose(inverse(model))) * normal;
                    texCoords_out = texCoords;
                    gl_Position = projection * view * vec4(fragPos, 1.0);
                }
                """;
                fragmentSource = """
                    #version 330 core
                    out vec4 fragColor;
                    
                    in vec3 fragPos;
                    in vec3 normal_out;
                    in vec2 texCoords_out;
                    
                    uniform vec3 lightDir;
                    uniform vec3 lightColor;
                    uniform vec3 ambientColor;
                    uniform sampler2D texture0;
                    uniform bool useTexture;
                    uniform vec3 objectColor; // Color for untextured objects
                    
                    void main() {
                        vec3 normal = normalize(normal_out);
                        // Light direction should point FROM the fragment TO the light source
                        vec3 lightDirection = normalize(-lightDir);
                        
                        float diff = max(dot(normal, lightDirection), 0.0);
                        vec3 diffuse = diff * lightColor;
                        
                        vec3 result = ambientColor + diffuse;
                        
                        if (useTexture) {
                            vec4 texColor = texture(texture0, texCoords_out);
                            fragColor = vec4(result, 1.0) * texColor;
                        } else {
                            fragColor = vec4(result * objectColor, 1.0);
                        }
                    }
                    """;
        } else if (type.equals("terrain")) {
            vertexSource = """
                #version 330 core
                layout(location = 0) in vec3 position;
                layout(location = 1) in vec3 normal;
                layout(location = 2) in vec2 texCoords;
                layout(location = 3) in vec2 blendCoords;
                
                uniform mat4 model;
                uniform mat4 view;
                uniform mat4 projection;
                
                out vec3 fragPos;
                out vec3 normal_out;
                out vec2 texCoords_out;
                out vec2 blendCoords_out;
                
                void main() {
                    fragPos = vec3(model * vec4(position, 1.0));
                    normal_out = mat3(transpose(inverse(model))) * normal;
                    texCoords_out = texCoords;
                    blendCoords_out = blendCoords;
                    gl_Position = projection * view * vec4(fragPos, 1.0);
                }
                """;
            fragmentSource = """
                #version 330 core
                out vec4 fragColor;
                
                in vec3 fragPos;
                in vec3 normal_out;
                in vec2 texCoords_out;
                in vec2 blendCoords_out;
                
                uniform vec3 lightDir;
                uniform vec3 lightColor;
                uniform vec3 ambientColor;
                uniform sampler2D tex0;
                uniform sampler2D tex1;
                uniform sampler2D tex2;
                uniform sampler2D blendMap;
                
                void main() {
                    vec3 normal = normalize(normal_out);
                    vec3 lightDirection = normalize(-lightDir);
                    
                    float diff = max(dot(normal, lightDirection), 0.0);
                    vec3 diffuse = diff * lightColor;
                    
                    vec4 t0 = texture(tex0, texCoords_out * 10.0);
                    vec4 t1 = texture(tex1, texCoords_out * 10.0);
                    vec4 t2 = texture(tex2, texCoords_out * 10.0);
                    vec4 blend = texture(blendMap, blendCoords_out);
                    
                    vec4 baseColor = t0 * blend.r + t1 * blend.g + t2 * blend.b;
                    
                    vec3 result = (ambientColor + diffuse);
                    fragColor = vec4(result, 1.0) * baseColor;
                }
                """;
        } else if (type.equals("ui")) {
            vertexSource = """
                #version 330 core
                layout(location = 0) in vec2 position;
                layout(location = 1) in vec2 texCoords;
                
                out vec2 texCoords_out;
                
                uniform mat4 projection;
                
                void main() {
                    gl_Position = projection * vec4(position, 0.0, 1.0);
                    texCoords_out = texCoords;
                }
                """;
            fragmentSource = """
                #version 330 core
                out vec4 fragColor;
                
                in vec2 texCoords_out;
                
                uniform sampler2D texture0;
                uniform vec4 color;
                uniform bool useTexture;
                
                void main() {
                    vec4 texColor = useTexture ? texture(texture0, texCoords_out) : vec4(1.0);
                    fragColor = texColor * color;
                }
                """;
        }
        
        int vertexShaderId = compileShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
        
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL32.glBindFragDataLocation(programId, 0, "outColor");
        GL20.glLinkProgram(programId);
        
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        
        Logger.info("Built-in shader program created: " + type);
    }
    
    public void bind() {
        GL20.glUseProgram(programId);
    }
    
    public static void unbind() {
        GL20.glUseProgram(0);
    }
    
    public void cleanup() {
        GL20.glDeleteProgram(programId);
    }
    
    public int getProgramId() {
        return programId;
    }
    
    public static ShaderProgram getDefault() {
        return DEFAULT;
    }
    
    public static ShaderProgram getTerrain() {
        return TERRAIN;
    }
    
    public static ShaderProgram getUI() {
        return UI;
    }

    public static void cleanupAll() {
        DEFAULT.cleanup();
        TERRAIN.cleanup();
        UI.cleanup();
    }
    
    private int getUniformLocation(String name) {
        Integer cached = uniformCache.get(name);
        if (cached != null) return cached;
        int location = GL20.glGetUniformLocation(programId, name);
        uniformCache.put(name, location);
        return location;
    }

    public void setUniform(String name, float value) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform1f(location, value);
        }
    }
    
    public void setUniform(String name, int value) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform1i(location, value);
        }
    }
    
    public void setUniform(String name, boolean value) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform1i(location, value ? 1 : 0);
        }
    }
    
    public void setUniform(String name, float x, float y, float z) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform3f(location, x, y, z);
        }
    }
    
    public void setUniform(String name, float x, float y, float z, float w) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform4f(location, x, y, z, w);
        }
    }
    
    public void setUniformMat4(String name, float[] matrix) {
        int location = getUniformLocation(name);
        if (location != -1) {
            GL20.glUniformMatrix4fv(location, false, matrix);
        }
    }
    
    public void setUniform3(String name, Vector3f vector) {
        setUniform(name, vector.x, vector.y, vector.z);
    }
}
