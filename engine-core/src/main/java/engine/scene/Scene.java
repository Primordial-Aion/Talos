package engine.scene;

import engine.entity.Entity;
import engine.terrain.Terrain;
import engine.lighting.LightManager;
import engine.camera.Camera;
import engine.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final float[] reusableMatrix16 = new float[16];
    private final org.joml.Matrix4f identityMatrix = new org.joml.Matrix4f();
    private static Scene instance;
    
    private String name;
    private List<Entity> entities;
    private Map<String, Entity> entityMap;
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;
    private Terrain terrain;
    private Camera camera;
    private LightManager lightManager;
    private boolean loaded = false;
    
    private Scene() {
        this.entities = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }
    
    public static Scene get() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing scene...");
        
        entities.clear();
        entityMap.clear();
        
        if (camera == null) {
            camera = Camera.get();
        }
        
        if (lightManager == null) {
            lightManager = LightManager.get();
            lightManager.init();
        }
        
        terrain = Terrain.get();
        terrain.init();
        
        loaded = true;
        Logger.info("Scene initialized: " + name);
    }
    
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
        
        if (camera != null) {
            camera.update(deltaTime);
        }
        
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();
        
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
    }
    
    public void render(engine.shader.ShaderProgram shader, org.joml.Matrix4f viewMatrix, org.joml.Matrix4f projectionMatrix) {
        // Render terrain with terrain shader if available
        if (terrain != null && terrain.getMesh() != null) {
            engine.shader.ShaderProgram terrainShader = engine.shader.ShaderProgram.getTerrain();
            terrainShader.bind();
            
            terrainShader.setUniform3("lightDir", lightManager.getSunDirection());
            terrainShader.setUniform3("lightColor", lightManager.getSunColor());
            terrainShader.setUniform3("ambientColor", lightManager.getAmbientColor());
            terrainShader.setUniformMat4("view", viewMatrix.get(reusableMatrix16));
            terrainShader.setUniformMat4("projection", projectionMatrix.get(reusableMatrix16));
            terrainShader.setUniformMat4("model", identityMatrix.identity().get(reusableMatrix16));
            
            terrain.render();
            engine.shader.ShaderProgram.unbind();
        }
        
        // Render entities with default shader
        shader.bind();
        shader.setUniform3("lightDir", lightManager.getSunDirection());
        shader.setUniform3("lightColor", lightManager.getSunColor());
        shader.setUniform3("ambientColor", lightManager.getAmbientColor());
        shader.setUniformMat4("view", viewMatrix.get(reusableMatrix16));
        shader.setUniformMat4("projection", projectionMatrix.get(reusableMatrix16));
        
        for (Entity entity : entities) {
            entity.render(viewMatrix, projectionMatrix, shader);
        }
        
        engine.shader.ShaderProgram.unbind();
    }
    
    public void addEntity(Entity entity) {
        if (!entities.contains(entity) && !entitiesToAdd.contains(entity)) {
            entitiesToAdd.add(entity);
            entityMap.put(entity.getName(), entity);
            Logger.debug("Entity added to scene: " + entity.getName());
        }
    }
    
    public void removeEntity(Entity entity) {
        if (entities.contains(entity) && !entitiesToRemove.contains(entity)) {
            entitiesToRemove.add(entity);
            entityMap.remove(entity.getName());
            Logger.debug("Entity removed from scene: " + entity.getName());
        }
    }
    
    public Entity getEntity(String name) {
        return entityMap.get(name);
    }
    
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
        return terrain;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public LightManager getLightManager() {
        return lightManager;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void clear() {
        entities.clear();
        entityMap.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
    }
    
    public void cleanup() {
        for (Entity entity : entities) {
            entity.cleanup();
        }
        entities.clear();
        entityMap.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        if (lightManager != null) {
            lightManager.cleanup();
        }
        if (terrain != null && terrain.getMesh() != null) {
            terrain.getMesh().cleanup();
        }
        loaded = false;
    }
}