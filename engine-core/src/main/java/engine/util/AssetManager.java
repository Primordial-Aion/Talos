package engine.util;

import engine.model.Model;
import engine.model.ModelLoader;

public class AssetManager {
    public static Model loadModel(String objPath) {
        try {
            return ModelLoader.loadObjModel(objPath);
        } catch (Exception e) {
            Logger.error("Failed to load model " + objPath + ": " + e.getMessage());
            return null;
        }
    }
}