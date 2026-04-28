package engine.core;

import engine.util.Logger;
import engine.util.Constants;
import engine.util.Config;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Window {
    private static Window instance;
    
    private long windowHandle;
    private int width;
    private int height;
    private String title;
    private boolean vsyncEnabled;
    private boolean fullscreen;
    private final AtomicBoolean shouldClose = new AtomicBoolean(false);
    private boolean resizable = true;
    private GLFWErrorCallback errorCallback;
    
    private Window() {
        this.width = Config.get().windowWidth;
        this.height = Config.get().windowHeight;
        this.title = Config.get().windowTitle;
        this.vsyncEnabled = Config.get().enableVsync;
    }
    
    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }
    
    public void init() {
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);
        
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        
        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }
        
        GLFW.glfwMakeContextCurrent(windowHandle);
        
        if (vsyncEnabled) {
            GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);
        }
        
        GLFW.glfwSetWindowCloseCallback(windowHandle, (window) -> shouldClose.set(true));
        
        GLFW.glfwSetWindowSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
        });
        
        GLFW.glfwSetKeyCallback(windowHandle, engine.input.Input.onKeyEvent);
        GLFW.glfwSetMouseButtonCallback(windowHandle, engine.input.Input.onMouseButton);
        GLFW.glfwSetCursorPosCallback(windowHandle, engine.input.Input.onMouseMove);
        GLFW.glfwSetScrollCallback(windowHandle, engine.input.Input.onMouseScroll);
        
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        
        var vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            GLFW.glfwSetWindowPos(windowHandle, 
                (vidMode.width() - width) / 2, 
                (vidMode.height() - height) / 2);
        }
        
        GLFW.glfwShowWindow(windowHandle);
        
        GL.createCapabilities();
        
        if (Config.get().enableDebug) {
            GLUtil.setupDebugMessageCallback();
        }
        
        Logger.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        Logger.info("Window created: " + width + "x" + height);
    }
    
    public void update() {
        GLFW.glfwSwapBuffers(windowHandle);
        GLFW.glfwPollEvents();
    }
    
    public boolean shouldClose() {
        return shouldClose.get();
    }
    
    public void close() {
        shouldClose.set(true);
    }
    
    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(windowHandle, title);
    }
    
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            var monitor = GLFW.glfwGetPrimaryMonitor();
            var vidMode = GLFW.glfwGetVideoMode(monitor);
            if (vidMode != null) {
                GLFW.glfwSetWindowMonitor(windowHandle, monitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
            }
        } else {
            GLFW.glfwSetWindowMonitor(windowHandle, MemoryUtil.NULL, 100, 100, width, height, 0);
        }
        this.fullscreen = fullscreen;
    }
    
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }
    
    public void cleanup() {
        if (windowHandle != MemoryUtil.NULL) {
            GLFW.glfwDestroyWindow(windowHandle);
        }
        if (errorCallback != null) {
            errorCallback.free();
        }
        GLFW.glfwTerminate();
    }
    
    public long getWindowHandle() {
        return windowHandle;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public boolean isVsyncEnabled() {
        return vsyncEnabled;
    }
    
    public void setVsyncEnabled(boolean vsync) {
        this.vsyncEnabled = vsync;
        GLFW.glfwSwapInterval(vsync ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }
}