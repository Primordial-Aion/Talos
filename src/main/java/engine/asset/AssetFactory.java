package engine.asset;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import engine.behavior.GameObject;
import engine.util.Logger;
import engine.behavior.Actor;

import java.util.Map;

public class AssetFactory {
    private static AssetFactory instance;
    
    private AssetFactory() {}
    
    public static AssetFactory get() {
        if (instance == null) {
            instance = new AssetFactory();
        }
        return instance;
    }
    
    public GameObject create(String assetId) {
        return create(assetId, null);
    }
    
    public GameObject create(String assetId, Map<String, Object> properties) {
        AssetRegistry.AssetManifest manifest = AssetRegistry.get().getManifest(assetId);
        
        if (manifest == null) {
            Logger.error("Asset not found: " + assetId);
            return null;
        }
        
        GameObject obj = new GameObject(assetId);
        obj.setPrefabId(assetId);
        
        if (manifest.model != null) {
            try {
                obj.setModel(engine.model.ModelLoader.loadObjModel(manifest.model));
            } catch (Exception e) {
                Logger.warn("Could not load model: " + manifest.model);
            }
        }
        
        if (properties != null) {
            for (Map.Entry<String, Object> prop : properties.entrySet()) {
                applyProperty(obj, prop.getKey(), prop.getValue());
            }
        }
        
        Logger.info("Created asset instance: " + assetId);
        return obj;
    }
    
    public GameObject createFromPrefab(String prefabId) {
        return createFromPrefab(prefabId, null);
    }
    
    public GameObject createFromPrefab(String prefabId, Map<String, Object> overrides) {
        return PrefabManager.get().createInstance(prefabId, overrides);
    }
    
    public Actor createCharacter(String assetId) {
        return createCharacter(assetId, null);
    }
    
    public Actor createCharacter(String assetId, Map<String, Object> properties) {
        GameObject obj = create(assetId, properties);
        
        if (obj == null) return null;
        
        Actor actor = new Actor(obj.getName());
        
        actor.setModel(obj.getModel());
        actor.setPosition(obj.getPosition());
        actor.setRotation(obj.getRotation());
        
        Vector3f scale = obj.getScale();
        actor.setScale(scale.x, scale.y, scale.z);
        
        Logger.info("Created character: " + assetId);
        return actor;
    }
    
    private void applyProperty(GameObject obj, String key, Object value) {
        switch (key) {
            case "position" -> {
                float[] pos = (float[]) value;
                obj.setPosition(pos[0], pos[1], pos[2]);
            }
            case "rotation" -> {
                float[] rot = (float[]) value;
                obj.setRotation(rot[0], rot[1], rot[2]);
            }
            case "scale" -> obj.setScale((float) value);
            case "name" -> obj.setName((String) value);
        }
    }
}