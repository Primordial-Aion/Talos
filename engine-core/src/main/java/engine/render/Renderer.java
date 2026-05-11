package engine.render;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class Renderer {
    private static Renderer instance;
    
    private float r = 0.1f, g = 0.1f, b = 0.1f, a = 1.0f;
    private boolean depthTestingEnabled = true;
    private boolean cullingEnabled = true;
    private boolean wireframeMode = false;
    private boolean depthTestingApplied = true;
    private boolean cullingApplied = true;
    private boolean wireframeApplied = false;
    
    private Renderer() {}
    
    public static Renderer get() {
        if (instance == null) {
            instance = new Renderer();
        }
        return instance;
    }
    
    public void init() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CCW);
        setClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    }
    
    public void beginFrame() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        if (depthTestingEnabled != depthTestingApplied) {
            if (depthTestingEnabled) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            depthTestingApplied = depthTestingEnabled;
        }
        
        if (cullingEnabled != cullingApplied) {
            if (cullingEnabled) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            } else {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }
            cullingApplied = cullingEnabled;
        }
        
        if (wireframeMode != wireframeApplied) {
            if (wireframeMode) {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            } else {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
            wireframeApplied = wireframeMode;
        }
    }
    
    public void endFrame() {
    }

    public void cleanup() {
        depthTestingApplied = false;
        cullingApplied = false;
        wireframeApplied = false;
    }
    
    public void setClearColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        GL11.glClearColor(r, g, b, a);
    }
    
    public void setViewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }
    
    public void setDepthTesting(boolean enabled) {
        this.depthTestingEnabled = enabled;
    }
    
    public void setCulling(boolean enabled) {
        this.cullingEnabled = enabled;
    }
    
    public void setWireframeMode(boolean enabled) {
        this.wireframeMode = enabled;
    }
    
    public boolean isDepthTestingEnabled() {
        return depthTestingEnabled;
    }
    
    public boolean isCullingEnabled() {
        return cullingEnabled;
    }
    
    public boolean isWireframeMode() {
        return wireframeMode;
    }
    
    public float[] getClearColor() {
        return new float[] {r, g, b, a};
    }
}