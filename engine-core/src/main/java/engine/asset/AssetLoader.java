package engine.asset;

import engine.util.Logger;
import engine.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetLoader {
    private static AssetLoader instance;
    
    private List<String> supportedFormats;
    private Map<String, AssetMetadata> loadedAssets;
    
    private AssetLoader() {
        this.supportedFormats = new ArrayList<>();
        supportedFormats.add(".glb");
        supportedFormats.add(".gltf");
        supportedFormats.add(".obj");
        this.loadedAssets = new HashMap<>();
    }
    
    public static AssetLoader get() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }
    
    public void loadAssetPackage(String directory) {
        Logger.info("Loading asset package from: " + directory);
        
        try {
            if (FileUtils.class.getResourceAsStream("/assets/" + directory + "/manifest.json") != null) {
                String manifestJson = FileUtils.readFileAsString("/assets/" + directory + "/manifest.json");
                AssetRegistry.get().registerAssetJson(manifestJson, "/assets/" + directory);
            }
        } catch (Exception e) {
            Logger.warn("No manifest found for: " + directory);
        }
        
        Logger.info("Asset package loaded: " + directory);
    }
    
    public void loadAllAssetsInDirectory(String directory) {
        Logger.info("Scanning for assets in: " + directory);
        try {
            java.nio.file.Path dirPath = java.nio.file.Paths.get(directory);
            if (java.nio.file.Files.exists(dirPath) && java.nio.file.Files.isDirectory(dirPath)) {
                try (var stream = java.nio.file.Files.newDirectoryStream(dirPath)) {
                    for (java.nio.file.Path entry : stream) {
                        if (java.nio.file.Files.isDirectory(entry)) {
                            loadAssetPackage(entry.getFileName().toString());
                        }
                    }
                }
            }
        } catch (java.io.IOException e) {
            Logger.warn("Could not scan directory: " + directory);
        }
    }
    
    public AssetMetadata getMetadata(String assetId) {
        return loadedAssets.get(assetId);
    }
    
    public boolean isSupported(String filename) {
        String lower = filename.toLowerCase();
        for (String format : supportedFormats) {
            if (lower.endsWith(format)) {
                return true;
            }
        }
        return false;
    }
    
    public static class AssetMetadata {
        public String id;
        public String type;
        public String filePath;
        public String modelPath;
        public List<String> animationNames;
        public List<String> boneNames;
        public Map<String, SocketData> sockets;
        public String collisionMesh;
        public Map<String, String> properties;
        
        public static class SocketData {
            public String name;
            public float[] position;
            public float[] rotation;
        }
    }
    
    public static class AssetConvention {
        public static String getAssetTypeFromName(String name) {
            if (name.startsWith("player")) return "player";
            if (name.startsWith("tractor")) return "vehicle";
            if (name.startsWith("tree")) return "prop";
            if (name.startsWith("crop")) return "crop";
            if (name.startsWith("tool")) return "tool";
            if (name.startsWith("door")) return "door";
            if (name.startsWith("building")) return "building";
            return "prop";
        }
        
        public static String getAnimationType(String animName) {
            String lower = animName.toLowerCase();
            if (lower.contains("idle")) return "idle";
            if (lower.contains("walk")) return "walk";
            if (lower.contains("run")) return "run";
            if (lower.contains("harvest")) return "harvest";
            if (lower.contains("pickup")) return "pickup";
            if (lower.contains("use")) return "use";
            return "custom";
        }
        
        public static boolean shouldLoop(String animName) {
            String lower = animName.toLowerCase();
            return lower.contains("idle") || lower.contains("walk") || lower.contains("run");
        }
        
        public static float getDefaultScale(String type) {
            return switch (type) {
                case "player" -> 1.0f;
                case "vehicle" -> 1.0f;
                case "prop" -> 1.0f;
                case "building" -> 1.0f;
                default -> 1.0f;
            };
        }
    }
}