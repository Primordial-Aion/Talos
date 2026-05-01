package engine.asset;

import engine.util.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetRegistry {
    private static AssetRegistry instance;
    
    private Map<String, AssetManifest> manifests;
    private Map<String, String> assetIdToPath;
    private Gson gson;
    
    private AssetRegistry() {
        this.manifests = new HashMap<>();
        this.assetIdToPath = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public static AssetRegistry get() {
        if (instance == null) {
            instance = new AssetRegistry();
        }
        return instance;
    }
    
    public void registerAsset(AssetManifest manifest, String filePath) {
        manifests.put(manifest.id, manifest);
        assetIdToPath.put(manifest.id, filePath);
        Logger.info("Registered asset: " + manifest.id + " (type: " + manifest.type + ")");
    }
    
    public void registerAssetJson(String json, String filePath) {
        try {
            AssetManifest manifest = gson.fromJson(json, AssetManifest.class);
            if (manifest != null && manifest.id != null) {
                registerAsset(manifest, filePath);
            }
        } catch (Exception e) {
            Logger.error("Failed to parse asset manifest: " + e.getMessage());
        }
    }
    
    public AssetManifest getManifest(String assetId) {
        return manifests.get(assetId);
    }
    
    public String getAssetPath(String assetId) {
        return assetIdToPath.get(assetId);
    }
    
    public boolean hasAsset(String assetId) {
        return manifests.containsKey(assetId);
    }
    
    public List<AssetManifest> getAssetsByType(String type) {
        List<AssetManifest> result = new ArrayList<>();
        for (AssetManifest manifest : manifests.values()) {
            if (manifest.type.equals(type)) {
                result.add(manifest);
            }
        }
        return result;
    }
    
    public List<String> getAllAssetIds() {
        return new ArrayList<>(manifests.keySet());
    }
    
    public void clear() {
        manifests.clear();
        assetIdToPath.clear();
    }
    
    public static class AssetManifest {
        public String id;
        public String type;
        public String model;
        public String texture;
        public List<String> animations;
        public List<String> colliders;
        public Map<String, AttachmentPoint> attachPoints;
        public Map<String, String> properties;
        public String prefabClass;
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("type", type);
            map.put("model", model);
            map.put("texture", texture);
            map.put("animations", animations);
            map.put("colliders", colliders);
            map.put("attachPoints", attachPoints);
            map.put("properties", properties);
            map.put("prefabClass", prefabClass);
            return map;
        }
    }
    
    public static class AttachmentPoint {
        public String name;
        public float[] position;
        public float[] rotation;
        
        public AttachmentPoint() {}
        
        public AttachmentPoint(String name, float[] position, float[] rotation) {
            this.name = name;
            this.position = position;
            this.rotation = rotation;
        }
    }
}