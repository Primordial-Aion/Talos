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
import engine.texture.Texture;
import engine.terrain.Terrain;
import engine.lighting.LightManager;
import engine.physics.Physics;
import engine.input.Input;
import engine.ui.UIManager;
import engine.debug.DebugOverlay;
import engine.util.Config;
import engine.util.Logger;
import engine.core.Window;
import engine.util.Config;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Game extends Engine {
    private static Game instance;
    
    private Scene scene;
    private Renderer renderer;
    private DebugOverlay debugOverlay;
    private boolean firstRun = true;
    
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
        
        Mesh cubeMesh2 = Mesh.createCube();
        Model cubeModel2 = new Model(cubeMesh2);
        
        Entity cube2 = new Entity(cubeModel2, new Vector3f(5, 2, 0));
        cube2.setName("test_cube_2");
        scene.addEntity(cube2);
        
        Camera.get().setPosition(new Vector3f(0, 5, 10));
        
        Logger.info("Test scene created");
    }
    
    @Override
    public void update(float deltaTime) {
        scene.update(deltaTime);
        
        handleInput();
        
        UIManager.get().update(deltaTime);
        
        Vector3f playerPos = Camera.get().getPosition();
        debugOverlay.update(getFps(), playerPos.x, playerPos.y, playerPos.z);
        
        if (Input.isKeyHeld(Input.Keys.ESCAPE)) {
            stop();
        }
    }
    
    private void handleInput() {
        if (Input.isKeyPressed(Input.Keys.ESCAPE)) {
            stop();
        }
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
        
        if (scene != null) {
            scene.cleanup();
        }
        
        UIManager.get().cleanup();
        
        if (debugOverlay != null) {
        }
        
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