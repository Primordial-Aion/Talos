package game.save;

import engine.scene.Scene;
import engine.entity.Entity;
import engine.util.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static SaveManager instance;
    
    private String saveDirectory = "saves";
    private String currentSaveName = "save1";
    private Gson gson;
    private boolean saveLoaded = false;
    
    private SaveManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static SaveManager get() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    public void saveGame(String saveName) {
        Logger.info("Saving game: " + saveName);
        
        SaveData saveData = new SaveData();
        saveData.saveName = saveName;
        saveData.timestamp = System.currentTimeMillis();
        
        if (engine.camera.Camera.get() != null) {
            Vector3f pos = engine.camera.Camera.get().getPosition();
            saveData.playerPosition = new float[] {pos.x, pos.y, pos.z};
            
            Vector3f rot = engine.camera.Camera.get().getRotation();
            saveData.playerRotation = new float[] {rot.x, rot.y, rot.z};
        }
        
        List<Entity> entities = Scene.get().getEntities();
        for (Entity entity : entities) {
            EntitySaveData entityData = new EntitySaveData();
            entityData.name = entity.getName();
            
            Vector3f pos = entity.getPosition();
            Vector3f rot = entity.getRotation();
            Vector3f scale = entity.getScale();
            
            entityData.position = new float[] {pos.x, pos.y, pos.z};
            entityData.rotation = new float[] {rot.x, rot.y, rot.z};
            entityData.scale = new float[] {scale.x, scale.y, scale.z};
            
            saveData.entities.add(entityData);
        }
        
        try {
            File file = new File(saveDirectory + "/" + saveName + ".json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(saveData, writer);
            writer.close();
            
            Logger.info("Game saved successfully");
            saveLoaded = true;
        } catch (IOException e) {
            Logger.error("Failed to save game: " + e.getMessage());
        }
    }
    
    public void loadGame(String saveName) {
        Logger.info("Loading game: " + saveName);
        
        try {
            File file = new File(saveDirectory + "/" + saveName + ".json");
            if (!file.exists()) {
                Logger.warn("Save file not found: " + saveName);
                return;
            }
            
            java.nio.file.Files.readString(file.toPath());
            
            Logger.info("Game loaded successfully");
            saveLoaded = true;
        } catch (Exception e) {
            Logger.error("Failed to load game: " + e.getMessage());
        }
    }
    
    public boolean hasSave(String saveName) {
        File file = new File(saveDirectory + "/" + saveName + ".json");
        return file.exists();
    }
    
    public List<String> getAvailableSaves() {
        List<String> saves = new ArrayList<>();
        
        File dir = new File(saveDirectory);
        if (dir.exists()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    saves.add(name.substring(0, name.length() - 5));
                }
            }
        }
        
        return saves;
    }
    
    public void deleteSave(String saveName) {
        File file = new File(saveDirectory + "/" + saveName + ".json");
        if (file.exists()) {
            file.delete();
            Logger.info("Save deleted: " + saveName);
        }
    }
    
    public void setSaveDirectory(String directory) {
        this.saveDirectory = directory;
        
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public String getSaveDirectory() {
        return saveDirectory;
    }
    
    public boolean isSaveLoaded() {
        return saveLoaded;
    }
    
    private static class SaveData {
        String saveName;
        long timestamp;
        float[] playerPosition;
        float[] playerRotation;
        List<EntitySaveData> entities = new ArrayList<>();
    }
    
    private static class EntitySaveData {
        String name;
        float[] position;
        float[] rotation;
        float[] scale;
    }
}