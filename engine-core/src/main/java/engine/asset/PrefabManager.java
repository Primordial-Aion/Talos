package engine.asset;

import engine.behavior.GameObject;
import engine.util.Logger;
import engine.model.Model;
import engine.model.Mesh;
import engine.model.ModelLoader;
import engine.behavior.TransformNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class PrefabManager {
    private static PrefabManager instance;
    
    private Map<String, Prefab> prefabs;
    private Gson gson;
    
    private PrefabManager() {
        this.prefabs = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public static PrefabManager get() {
        if (instance == null) {
            instance = new PrefabManager();
        }
        return instance;
    }
    
    public void registerPrefab(Prefab prefab) {
        prefabs.put(prefab.id, prefab);
        Logger.info("Registered prefab: " + prefab.id);
    }
    
    public Prefab getPrefab(String id) {
        return prefabs.get(id);
    }
    
    public boolean hasPrefab(String id) {
        return prefabs.containsKey(id);
    }
    
    public GameObject createInstance(String prefabId) {
        return createInstance(prefabId, null);
    }
    
    public GameObject createInstance(String prefabId, Map<String, Object> overrides) {
        Prefab prefab = prefabs.get(prefabId);
        if (prefab == null) {
            Logger.error("Prefab not found: " + prefabId);
            return null;
        }
        
        GameObject obj = new GameObject(prefab.name + "_instance");
        obj.setPrefabId(prefabId);
        
        if (prefab.modelPath != null) {
            try {
                Model model = ModelLoader.loadObjModel(prefab.modelPath);
                obj.setModel(model);
            } catch (Exception e) {
                Logger.warn("Could not load model for prefab: " + prefab.modelPath);
            }
        }
        
        if (prefab.position != null) {
            obj.setPosition(prefab.position[0], prefab.position[1], prefab.position[2]);
        }
        
        if (prefab.rotation != null) {
            obj.setRotation(prefab.rotation[0], prefab.rotation[1], prefab.rotation[2]);
        }
        
        if (prefab.scale != null) {
            obj.setScale(prefab.scale[0], prefab.scale[1], prefab.scale[2]);
        }
        
        if (overrides != null) {
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                applyOverride(obj, entry.getKey(), entry.getValue());
            }
        }
        
        return obj;
    }
    
    private void applyOverride(GameObject obj, String key, Object value) {
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
    
    public void loadPrefabsFromManifest(String manifestJson) {
        try {
            PrefabManifest manifest = gson.fromJson(manifestJson, PrefabManifest.class);
            if (manifest != null && manifest.prefabs != null) {
                for (Prefab prefab : manifest.prefabs) {
                    registerPrefab(prefab);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to parse prefab manifest: " + e.getMessage());
        }
    }
    
    public static class Prefab {
        public String id;
        public String name;
        public String type;
        public String modelPath;
        public float[] position;
        public float[] rotation;
        public float[] scale;
        public String prefabClass;
        public Map<String, Object> properties;
        
        public Prefab() {}
        
        public Prefab(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Prefab(String id, String name, String modelPath) {
            this.id = id;
            this.name = name;
            this.modelPath = modelPath;
        }
    }
    
    public static class PrefabManifest {
        public String version;
        public Prefab[] prefabs;
    }
}