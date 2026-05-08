# How To Implement And Render A 2D Scene

## 1. Goal

This engine is a 3D OpenGL engine, but you can make a clean 2D game by treating the X/Z plane as your game board and looking straight down at it with an orthographic camera.

Use this mental model:

| Game idea | Engine coordinate |
| --- | --- |
| Horizontal screen movement | X axis |
| Vertical screen movement | Z axis |
| Height above the board | Y axis |
| 2D background/board | A flat quad at Y = 0 |
| Player/food/enemies | Cubes or flat quads placed slightly above Y = 0 |

The result is a 2D-style scene rendered by the engine's normal `Scene`, `Entity`, `Model`, `Mesh`, `Renderer`, and `ShaderProgram` systems.

## 2. Required Imports

Use these imports in a simple 2D game class:

```java
import engine.core.Engine;
import engine.core.Window;
import engine.entity.Entity;
import engine.lighting.Light;
import engine.lighting.LightManager;
import engine.model.Mesh;
import engine.model.Model;
import engine.render.Renderer;
import engine.scene.Scene;
import engine.shader.ShaderProgram;
import engine.util.Config;
import org.joml.Matrix4f;
import org.joml.Vector3f;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `Engine` | Base class that owns the main loop. Override `init`, `update`, and `render`. |
| `Window` | Gives window size and lets the engine create the OpenGL context. |
| `Entity` | A thing in the scene. It has position, rotation, scale, and a model. |
| `Mesh` | Raw geometry. Use `Mesh.createQuad()` for planes and `Mesh.createCube()` for boxes. |
| `Model` | Wraps a mesh and stores color/texture state. |
| `Scene` | Stores and renders entities. |
| `Renderer` | Clears frames and controls depth/culling/wireframe settings. |
| `ShaderProgram` | Loads default shaders and gives access to the default render shader. |
| `Light`, `LightManager` | Control basic scene lighting. Even 2D-style cube scenes need light. |
| `Config` | Sets initial window width, height, title, debug options, etc. |
| `Matrix4f` | JOML matrix class used for view and projection matrices. |
| `Vector3f` | JOML 3D vector used for positions, colors, and scale. |

## 3. Scene Dimensions And Boundaries

Pick a world size first. A good beginner board is 48 by 48 world units:

```java
private static final float PLANE_HALF_EXTENT = 24.0f;
private static final float PLAYER_HALF_SIZE = 0.8f;
```

This means:

| Value | Meaning |
| --- | --- |
| `PLANE_HALF_EXTENT` | The board extends from `-24` to `+24` on X and Z. |
| `PLAYER_HALF_SIZE` | If the player cube is 1.6 units wide, keep its center at least 0.8 units from the edge. |
| Player min X/Z | `-PLANE_HALF_EXTENT + PLAYER_HALF_SIZE` |
| Player max X/Z | `PLANE_HALF_EXTENT - PLAYER_HALF_SIZE` |

Use a clamp whenever an entity moves:

```java
private float clamp(float value, float min, float max) {
    return Math.max(min, Math.min(max, value));
}

private Vector3f clampToBoard(Vector3f position) {
    float max = PLANE_HALF_EXTENT - PLAYER_HALF_SIZE;
    position.x = clamp(position.x, -max, max);
    position.z = clamp(position.z, -max, max);
    return position;
}
```

Do not use Y for 2D movement. Keep Y stable:

| Entity | Suggested Y |
| --- | --- |
| Plane | `0.0f` |
| Flat food/coin quad | `0.02f` to avoid z-fighting |
| Cube player | `PLAYER_HALF_SIZE` |
| Cube food | `FOOD_HALF_SIZE` |

## 4. Colors

Colors in this engine are floats from `0.0f` to `1.0f`, not `0` to `255`.

```java
Model planeModel = new Model(Mesh.createQuad());
planeModel.setColor(0.17f, 0.42f, 0.35f); // muted green
```

Useful starter colors:

| Object | RGB float color |
| --- | --- |
| Background clear | `0.08f, 0.09f, 0.11f` |
| Plane | `0.17f, 0.42f, 0.35f` |
| Player | `0.95f, 0.28f, 0.22f` |
| Food/coin | `1.0f, 0.84f, 0.18f` |
| Enemy | `0.45f, 0.14f, 0.78f` |
| Neutral wall | `0.55f, 0.58f, 0.62f` |

## 5. Basic 2D Scene Class

This is the smallest complete shape for a 2D-style scene:

```java
public class Simple2DScene extends Engine {
    private static final float PLANE_HALF_EXTENT = 24.0f;

    private Scene scene;

    public Simple2DScene() {
        super(Window.get(), Config.get());
    }

    @Override
    public void init() {
        super.init();

        Renderer renderer = Renderer.get();
        renderer.init();
        renderer.setCulling(false);
        renderer.setClearColor(0.08f, 0.09f, 0.11f, 1.0f);

        ShaderProgram.init();

        scene = Scene.get();
        scene.init();
        scene.setTerrain(null);

        configureLighting();
        createBoard();
    }

    private void configureLighting() {
        LightManager lightManager = scene.getLightManager();
        Light sun = lightManager.getSunLight();
        if (sun != null) {
            sun.setPosition(new Vector3f(-0.4f, 1.0f, -0.5f).normalize());
            sun.setColor(new Vector3f(1.0f, 0.96f, 0.86f));
            sun.setAmbientColor(new Vector3f(0.42f, 0.45f, 0.50f));
        }
    }

    private void createBoard() {
        Model planeModel = new Model(Mesh.createQuad());
        planeModel.setColor(0.17f, 0.42f, 0.35f);

        Entity plane = new Entity(planeModel, new Vector3f(0.0f, 0.0f, 0.0f));
        plane.setName("board");
        plane.setScale(new Vector3f(PLANE_HALF_EXTENT, 1.0f, PLANE_HALF_EXTENT));

        scene.addEntity(plane);
    }

    @Override
    protected void update(float deltaTime) {
        scene.update(deltaTime);
    }

    @Override
    protected void render() {
        Renderer.get().beginFrame();

        Window window = getWindow();
        float aspect = (float) window.getWidth() / Math.max(1, window.getHeight());
        float halfHeight = 28.0f;
        float halfWidth = halfHeight * aspect;

        Matrix4f view = new Matrix4f().lookAt(
                new Vector3f(0.0f, 45.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, -1.0f)
        );
        Matrix4f projection = new Matrix4f()
                .ortho(-halfWidth, halfWidth, -halfHeight, halfHeight, 0.1f, 100.0f);

        scene.render(ShaderProgram.getDefault(), view, projection);
        Renderer.get().endFrame();
    }

    public static void main(String[] args) {
        Config.get().windowWidth = 960;
        Config.get().windowHeight = 720;
        Config.get().windowTitle = "Simple 2D Scene";
        new Simple2DScene().run();
    }
}
```

## 6. Orthographic Camera Explained

The important 2D part is this:

```java
Matrix4f projection = new Matrix4f()
        .ortho(-halfWidth, halfWidth, -halfHeight, halfHeight, 0.1f, 100.0f);
```

Perspective projection makes distant objects look smaller. Orthographic projection does not. For 2D games, orthographic projection is usually correct because your board should look like a flat map.

The top-down view is created here:

```java
Matrix4f view = new Matrix4f().lookAt(
        new Vector3f(0.0f, 45.0f, 0.0f),
        new Vector3f(0.0f, 0.0f, 0.0f),
        new Vector3f(0.0f, 0.0f, -1.0f)
);
```

Read it as:

| Argument | Meaning |
| --- | --- |
| Eye | Camera position. Here it is high above the board. |
| Center | What the camera looks at. Here it looks at world origin. |
| Up | Which world direction should appear as screen-up. |

For this top-down setup, screen-up is negative Z.

## 7. Common Mistakes

| Mistake | Fix |
| --- | --- |
| Board is invisible | Call `renderer.setCulling(false)` for flat quads, or check camera direction. |
| Objects flicker on board | Put entities slightly above the plane or use cube Y = half-size. |
| Scene looks 3D/perspective | Use `Matrix4f.ortho`, not `perspective`. |
| Player leaves board | Clamp X/Z after movement. |
| Colors look too dark | Increase ambient light with `sun.setAmbientColor(...)`. |
| Nothing renders | Call `ShaderProgram.init()` after `Renderer.init()` and before render. |

