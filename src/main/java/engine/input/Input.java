package engine.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.Arrays;

public class Input {
    private static final int NUM_KEYS = 512;
    private static final int NUM_BUTTONS = 8;
    
    private static final boolean[] keysPressed = new boolean[NUM_KEYS];
    private static final boolean[] keysHeld = new boolean[NUM_KEYS];
    private static final boolean[] keysReleased = new boolean[NUM_KEYS];
    
    private static final boolean[] mouseButtonsPressed = new boolean[NUM_BUTTONS];
    private static final boolean[] mouseButtonsHeld = new boolean[NUM_BUTTONS];
    private static final boolean[] mouseButtonsReleased = new boolean[NUM_BUTTONS];
    
    private static double mouseX = 0;
    private static double mouseY = 0;
    private static double mouseDeltaX = 0;
    private static double mouseDeltaY = 0;
    private static double scrollDX = 0;
    private static double scrollDY = 0;
    private static boolean mouseLocked = false;
    
    public static GLFWKeyCallback onKeyEvent = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key >= 0 && key < NUM_KEYS) {
                if (action == GLFW.GLFW_PRESS) {
                    keysPressed[key] = true;
                    keysHeld[key] = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    keysReleased[key] = true;
                    keysHeld[key] = false;
                }
            }
        }
    };
    
    public static GLFWMouseButtonCallback onMouseButton = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button >= 0 && button < NUM_BUTTONS) {
                if (action == GLFW.GLFW_PRESS) {
                    mouseButtonsPressed[button] = true;
                    mouseButtonsHeld[button] = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    mouseButtonsReleased[button] = true;
                    mouseButtonsHeld[button] = false;
                }
            }
        }
    };
    
    public static GLFWCursorPosCallback onMouseMove = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            if (mouseLocked) {
                mouseDeltaX = xpos - mouseX;
                mouseDeltaY = ypos - mouseY;
            }
            mouseX = xpos;
            mouseY = ypos;
        }
    };
    
    public static GLFWScrollCallback onMouseScroll = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            scrollDX = xoffset;
            scrollDY = yoffset;
        }
    };
    
    public static void update() {
        Arrays.fill(keysPressed, false);
        Arrays.fill(keysReleased, false);
        Arrays.fill(mouseButtonsPressed, false);
        Arrays.fill(mouseButtonsReleased, false);
        mouseDeltaX = 0;
        mouseDeltaY = 0;
        scrollDX = 0;
        scrollDY = 0;
    }
    
    public static boolean isKeyPressed(int key) {
        if (key >= 0 && key < NUM_KEYS) {
            return keysPressed[key];
        }
        return false;
    }
    
    public static boolean isKeyHeld(int key) {
        if (key >= 0 && key < NUM_KEYS) {
            return keysHeld[key];
        }
        return false;
    }
    
    public static boolean isKeyReleased(int key) {
        if (key >= 0 && key < NUM_KEYS) {
            return keysReleased[key];
        }
        return false;
    }
    
    public static boolean isMouseButtonPressed(int button) {
        if (button >= 0 && button < NUM_BUTTONS) {
            return mouseButtonsPressed[button];
        }
        return false;
    }
    
    public static boolean isMouseButtonHeld(int button) {
        if (button >= 0 && button < NUM_BUTTONS) {
            return mouseButtonsHeld[button];
        }
        return false;
    }
    
    public static boolean isMouseButtonReleased(int button) {
        if (button >= 0 && button < NUM_BUTTONS) {
            return mouseButtonsReleased[button];
        }
        return false;
    }
    
    public static double getMouseX() {
        return mouseX;
    }
    
    public static double getMouseY() {
        return mouseY;
    }
    
    public static double getMouseDeltaX() {
        return mouseDeltaX;
    }
    
    public static double getMouseDeltaY() {
        return mouseDeltaY;
    }
    
    public static double getScrollDX() {
        return scrollDX;
    }
    
    public static double getScrollDY() {
        return scrollDY;
    }
    
    public static void setMouseLocked(boolean locked) {
        mouseLocked = locked;
    }
    
    public static boolean isMouseLocked() {
        return mouseLocked;
    }
    
    public static class Keys {
        public static final int SPACE = GLFW.GLFW_KEY_SPACE;
        public static final int APOSTROPHE = GLFW.GLFW_KEY_APOSTROPHE;
        public static final int COMMA = GLFW.GLFW_KEY_COMMA;
        public static final int MINUS = GLFW.GLFW_KEY_MINUS;
        public static final int PERIOD = GLFW.GLFW_KEY_PERIOD;
        public static final int SLASH = GLFW.GLFW_KEY_SLASH;
        public static final int NUM_0 = GLFW.GLFW_KEY_0;
        public static final int NUM_1 = GLFW.GLFW_KEY_1;
        public static final int NUM_2 = GLFW.GLFW_KEY_2;
        public static final int NUM_3 = GLFW.GLFW_KEY_3;
        public static final int NUM_4 = GLFW.GLFW_KEY_4;
        public static final int NUM_5 = GLFW.GLFW_KEY_5;
        public static final int NUM_6 = GLFW.GLFW_KEY_6;
        public static final int NUM_7 = GLFW.GLFW_KEY_7;
        public static final int NUM_8 = GLFW.GLFW_KEY_8;
        public static final int NUM_9 = GLFW.GLFW_KEY_9;
        public static final int SEMICOLON = GLFW.GLFW_KEY_SEMICOLON;
        public static final int EQUAL = GLFW.GLFW_KEY_EQUAL;
        public static final int A = GLFW.GLFW_KEY_A;
        public static final int B = GLFW.GLFW_KEY_B;
        public static final int C = GLFW.GLFW_KEY_C;
        public static final int D = GLFW.GLFW_KEY_D;
        public static final int E = GLFW.GLFW_KEY_E;
        public static final int F = GLFW.GLFW_KEY_F;
        public static final int G = GLFW.GLFW_KEY_G;
        public static final int H = GLFW.GLFW_KEY_H;
        public static final int I = GLFW.GLFW_KEY_I;
        public static final int J = GLFW.GLFW_KEY_J;
        public static final int K = GLFW.GLFW_KEY_K;
        public static final int L = GLFW.GLFW_KEY_L;
        public static final int M = GLFW.GLFW_KEY_M;
        public static final int N = GLFW.GLFW_KEY_N;
        public static final int O = GLFW.GLFW_KEY_O;
        public static final int P = GLFW.GLFW_KEY_P;
        public static final int Q = GLFW.GLFW_KEY_Q;
        public static final int R = GLFW.GLFW_KEY_R;
        public static final int S = GLFW.GLFW_KEY_S;
        public static final int T = GLFW.GLFW_KEY_T;
        public static final int U = GLFW.GLFW_KEY_U;
        public static final int V = GLFW.GLFW_KEY_V;
        public static final int W = GLFW.GLFW_KEY_W;
        public static final int X = GLFW.GLFW_KEY_X;
        public static final int Y = GLFW.GLFW_KEY_Y;
        public static final int Z = GLFW.GLFW_KEY_Z;
        public static final int LEFT_BRACKET = GLFW.GLFW_KEY_LEFT_BRACKET;
        public static final int BACKSLASH = GLFW.GLFW_KEY_BACKSLASH;
        public static final int RIGHT_BRACKET = GLFW.GLFW_KEY_RIGHT_BRACKET;
        public static final int GRAVE_ACCENT = GLFW.GLFW_KEY_GRAVE_ACCENT;
        public static final int ESCAPE = GLFW.GLFW_KEY_ESCAPE;
        public static final int ENTER = GLFW.GLFW_KEY_ENTER;
        public static final int TAB = GLFW.GLFW_KEY_TAB;
        public static final int BACKSPACE = GLFW.GLFW_KEY_BACKSPACE;
        public static final int INSERT = GLFW.GLFW_KEY_INSERT;
        public static final int DELETE = GLFW.GLFW_KEY_DELETE;
        public static final int RIGHT = GLFW.GLFW_KEY_RIGHT;
        public static final int LEFT = GLFW.GLFW_KEY_LEFT;
        public static final int DOWN = GLFW.GLFW_KEY_DOWN;
        public static final int UP = GLFW.GLFW_KEY_UP;
        public static final int PAGE_UP = GLFW.GLFW_KEY_PAGE_UP;
        public static final int PAGE_DOWN = GLFW.GLFW_KEY_PAGE_DOWN;
        public static final int HOME = GLFW.GLFW_KEY_HOME;
        public static final int END = GLFW.GLFW_KEY_END;
        public static final int CAPS_LOCK = GLFW.GLFW_KEY_CAPS_LOCK;
        public static final int F1 = GLFW.GLFW_KEY_F1;
        public static final int F2 = GLFW.GLFW_KEY_F2;
        public static final int F3 = GLFW.GLFW_KEY_F3;
        public static final int F4 = GLFW.GLFW_KEY_F4;
        public static final int F5 = GLFW.GLFW_KEY_F5;
        public static final int F6 = GLFW.GLFW_KEY_F6;
        public static final int F7 = GLFW.GLFW_KEY_F7;
        public static final int F8 = GLFW.GLFW_KEY_F8;
        public static final int F9 = GLFW.GLFW_KEY_F9;
        public static final int F10 = GLFW.GLFW_KEY_F10;
        public static final int F11 = GLFW.GLFW_KEY_F11;
        public static final int F12 = GLFW.GLFW_KEY_F12;
        public static final int LEFT_SHIFT = GLFW.GLFW_KEY_LEFT_SHIFT;
        public static final int LEFT_CONTROL = GLFW.GLFW_KEY_LEFT_CONTROL;
        public static final int LEFT_ALT = GLFW.GLFW_KEY_LEFT_ALT;
        public static final int LEFT_SUPER = GLFW.GLFW_KEY_LEFT_SUPER;
        public static final int RIGHT_SHIFT = GLFW.GLFW_KEY_RIGHT_SHIFT;
        public static final int RIGHT_CONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL;
        public static final int RIGHT_ALT = GLFW.GLFW_KEY_RIGHT_ALT;
        public static final int RIGHT_SUPER = GLFW.GLFW_KEY_RIGHT_SUPER;
    }
    
    public static class Mouse {
        public static final int BUTTON_1 = GLFW.GLFW_MOUSE_BUTTON_1;
        public static final int BUTTON_2 = GLFW.GLFW_MOUSE_BUTTON_2;
        public static final int BUTTON_3 = GLFW.GLFW_MOUSE_BUTTON_3;
        public static final int BUTTON_4 = GLFW.GLFW_MOUSE_BUTTON_4;
        public static final int BUTTON_5 = GLFW.GLFW_MOUSE_BUTTON_5;
        public static final int LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
        public static final int RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        public static final int MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
    }
}