# Java 3D Game Engine - User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Project Setup](#project-setup)
3. [Basic Concepts](#basic-concepts)
4. [Creating Your First Project](#creating-your-first-project)
5. [Core Systems](#core-systems)
6. [Game Objects](#game-objects)
7. [Terrain and Environment](#terrain-and-environment)
8. [Player and Physics](#player-and-physics)
9. [Save/Load System](#saveload-system)
10. [UI and HUD](#ui-and-hud)
11. [Running the Game](#running-the-game)

---

## 1. Introduction

This game engine is a Java-based 3D engine for creating farming simulator-style games. It provides:
- OpenGL rendering via LWJGL
- Entity-component system for game objects
- Terrain generation with heightmaps
- Player movement and physics
- Lighting and shadows
- UI system
- Save/load functionality

---

## 2. Project Setup

### Prerequisites
- Java 17 or higher
- Maven

### Dependencies (in pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.lwjgl</groupId>
        <artifactId>lwjgl</artifactId>
        <version>3.3.4</version>
    </dependency>
    <dependency>
        <groupId>org.lwjgl</groupId>
        <artifactId>lwjgl-glfw</artifactId>
        <version>3.3.4</version>
    </dependency>
    <dependency>
        <groupId>org.lwjgl</groupId>
        <artifactId>lwjgl-opengl</artifactId>
        <version>3.3.4</version>
    </dependency>
    <dependency>
        <groupId>org.joml</groupId>
        <artifactId>joml</artifactId>
        <version>1.10.5</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

---

## 3. Basic Concepts

### Core Classes
- `Engine` - Main game loop and initialization
- `Window` - GLFW window management
- `Scene` - Container for all game entities
- `Camera` - Player viewpoint
- `Renderer` - Handles OpenGL rendering
- `Entity` - Base class for all game objects

### Coordinate System
- Y-up coordinate system
- Units in meters
- Rotation in radians (converted from degrees internally)

---

## 4. Creating Your First Project

### Step 1: Create Main Class
Create `src/main/java/game/Game.java`:

```java
package game;

import engine.core.Engine;
import engine.core.Window;
import engine.render.Renderer;
import engine.scene.Scene;
import engine.shader.ShaderProgram;
import engine.camera.Camera;
import engine.entity.Entity;
import engine.model.Model;
import engine.model.Mesh;
import engine.terrain.Terrain;
import engine.lighting.LightManager;
import engine.physics.Physics;
import engine.input.Input;
import engine.ui.UIManager;
import engine.debug.DebugOverlay;
import engine.util.Config;
import engine.util.Logger;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Game extends Engine {
    private static Game instance;
    private Scene scene;
    private Renderer renderer;
    private DebugOverlay debugOverlay;
    
    private Game() {
        super(Window.get(), Config.get());
    }
    
    public static Game get() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    
    @Override
    public void init() {
        super.init();
        Logger.info("Starting game initialization...");
        Logger.setPrefix("[Game]");
        
        renderer = Renderer.get();
        renderer.init();
        
        ShaderProgram.init();
        
        scene = Scene.get();
        scene.init();
        
        Physics.get().setTerrain(scene.getTerrain());
        
        UIManager.get().init();
        
        debugOverlay = DebugOverlay.get();
        debugOverlay.init();
        
        createTestScene();
        Logger.info("Game initialized successfully");
    }
    
    private void createTestScene() {
        Logger.info("Creating test scene...");
        
        Mesh cubeMesh = Mesh.createCube();
        Model cubeModel = new Model(cubeMesh);
        
        Entity cube = new Entity(cubeModel, new Vector3f(0, 2, 0));
        cube.setName("test_cube");
        scene.addEntity(cube);
        
        Camera.get().setPosition(new Vector3f(0, 5, 10));
        Logger.info("Test scene created");
    }
    
    @Override
    public void update(float deltaTime) {
        scene.update(deltaTime);
        
        if (Input.isKeyPressed(Input.Keys.ESCAPE)) {
            stop();
        }
        
        Vector3f playerPos = Camera.get().getPosition();
        debugOverlay.update(getFps(), playerPos.x, playerPos.y, playerPos.z);
    }
    
    @Override
    public void render() {
        renderer.beginFrame();
        
        Window window = getWindow();
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        
        Camera camera = Camera.get();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix(aspectRatio);
        
        ShaderProgram defaultShader = ShaderProgram.getDefault();
        defaultShader.bind();
        
        LightManager lightManager = scene.getLightManager();
        defaultShader.setUniform3("lightDir", lightManager.getSunDirection());
        defaultShader.setUniform3("lightColor", lightManager.getSunColor());
        defaultShader.setUniform3("ambientColor", lightManager.getAmbientColor());
        
        scene.render(defaultShader, viewMatrix, projectionMatrix);
        
        renderer.endFrame();
    }
    
    public void cleanup() {
        Logger.info("Cleaning up game...");
        if (scene != null) scene.cleanup();
        UIManager.get().cleanup();
        Logger.info("Game cleanup complete");
    }
    
    public static void main(String[] args) {
        Logger.info("Starting Java Game Engine...");
        try {
            Game game = Game.get();
            game.init();
            game.run();
        } catch (Exception e) {
            Logger.error("Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
```

---

## 5. Core Systems

### Window
```java
Window window = Window.get();
window.init();
// Access dimensions
int width = window.getWidth();
int height = window.getHeight();
```

### Input Action Mapping
```java
// Map physical keys to logical actions
Input.mapAction("JUMP", Input.Keys.SPACE);
Input.mapAction("FIRE", Input.Mouse.LEFT);

// Check if action is active
if (Input.isActionPressed("JUMP")) { // Pressed this frame
    // jump
}
if (Input.isActionActive("FIRE")) { // Currently held
    // shoot
}
```

### Raw Input
```java
// Keyboard
Input.isKeyPressed(Input.Keys.W);     // Pressed this frame
Input.isKeyHeld(Input.Keys.W);        // Currently held down

// Mouse
double mouseX = Input.getMouseX();
double mouseY = Input.getMouseY();
double mouseDeltaX = Input.getMouseDeltaX();
double mouseDeltaY = Input.getMouseDeltaY();
```

### Camera
```java
Camera camera = Camera.get();
camera.setPosition(new Vector3f(0, 5, 10));
camera.setRotation(new Vector3f(0, 0, 0));
camera.setFov(70.0f);
camera.setMoveSpeed(5.0f);

// Auto-updated in game loop
camera.update(deltaTime);
```

---

## 6. Game Objects

### AssetManager (Model Loading)
The `AssetManager` allows you to instantly load `.obj` files into the engine:
```java
// Automatically parses OBJ into a Model and Mesh
Model carModel = AssetManager.loadModel("assets/models/car.obj");
```

### Entity Component System (ECS)
```java
// Create entity with model
Entity entity = new Entity(carModel, position);
entity.setName("player_car");
entity.setScale(1.0f);

// Add custom logic via Components
entity.addComponent(new DrivingPhysicsComponent());

// Add to scene
scene.addEntity(entity);
```

### Creating Custom Components
Instead of extending `Entity`, you should extend `Component` to build reusable algorithms:
```java
public class DrivingPhysicsComponent extends Component {
    @Override
    public void start() {
        // Initialization logic
    }
    
    @Override
    public void update(float deltaTime) {
        // Automatically called every frame!
        Vector3f pos = entity.getPosition();
        pos.z -= 10 * deltaTime;
        entity.setPosition(pos);
    }
}
```

---

## 7. Terrain and Environment

### Terrain Generation
```java
Terrain terrain = Terrain.get();
terrain.init();

// Flat terrain
terrain.generateFlatTerrain();

// From heightmap
float[] heightData = loadHeightmap();
terrain.generateFromHeightmap(heightData);

// Procedural noise
terrain.generateNoiseTerrain(seed);
```

### Get Height
```java
float height = terrain.getTerrainHeight(x, z);  // World coords
float height = terrain.getHeightAt(gridX, gridZ); // Grid coords
```

---

## 8. Player and Physics

### Player Movement
```java
// Automatic with Camera
Camera camera = Camera.get();
camera.update(deltaTime);

// With Physics snapping
Physics physics = Physics.get();
physics.snapToGround(position, deltaTime);
boolean onGround = physics.isOnGround(position);
```

---

## 9. Save/Load System

### Saving
```java
SaveManager saveManager = SaveManager.get();
saveManager.saveGame("save1");
```

### Loading
```java
SaveManager saveManager = SaveManager.get();
saveManager.loadGame("save1");

// Check available saves
List<String> saves = saveManager.getAvailableSaves();
```

---

## 10. UI and HUD

### UI Elements
```java
UIElement element = new UIElement(new Vector2f(100, 100), new Vector2f(200, 50));
element.setColor(1.0f, 1.0f, 1.0f, 0.5f);
UIManager.get().addElement("my_element", element);
```

### UIPanel
```java
UIPanel panel = new UIPanel("Inventory", new Vector2f(100, 100), new Vector2f(300, 400));
panel.setVisible(true);
UIManager.get().addPanel(panel);
```

---

## 11. Running the Game

### Build and Run
```bash
# Build
mvn package

# Run
mvn exec:java -Dexec.mainClass="game.Game"
```

### Controls
| Key | Action |
|-----|-------|
| W/A/S/D | Move |
| Mouse | Look around |
| ESC | Exit |
| F3 | Toggle debug |
| F4 | Toggle wireframe |
| SPACE | Jump |

---

## Quick Reference

### Package Structure
```
src/main/java/
  engine/
    core/        - Engine, Window
    input/       - Input
    math/        - MathUtilities
    render/      - Renderer
    shader/      - ShaderProgram
    texture/     - Texture
    model/       - Mesh, Model
    entity/      - Entity
    camera/      - Camera
    terrain/     - Terrain
    lighting/    - Light, LightManager
    physics/     - Physics
    scene/       - Scene
    ui/          - UIManager, UIElement
    audio/       - AudioManager
    debug/       - DebugOverlay
    animation/  - Animation, Animator
    util/        - Logger, Config, Constants
  game/
    Game.java    - Your game class
    objects/     - Custom game objects
    player/      - Player
    save/        - SaveManager
```

### Key Classes Quick Reference

| Class | Purpose |
|-------|---------|
| `Engine` | Main game loop |
| `Window` | Game window |
| `Scene` | World container |
| `Entity` | Base game object |
| `Camera` | Player view |
| `Terrain` | Ground |
| `ShaderProgram` | GLSL shaders |
| `Texture` | Images |
| `Model` | 3D geometry |

---

For more examples, see the test code in `game.Game.java`.